package main.cl.dagserver.infra.adapters.input.controllers;

import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.SchemaPlus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.application.ports.input.CalciteUseCase;
import main.cl.dagserver.infra.adapters.input.channels.calcite.core.schemas.DagserverSchema;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
@RestController
@Log4j2
@RequestMapping("/calcite")
public class CalciteController {

	@Autowired
	CalciteUseCase calcite;
	
    @PostMapping("/execute")
    public String execute(@RequestBody String bodyStr) {
        try {
            var body = sanitizeBody(bodyStr);
            log.info(body);
            String[] sqlStatements = new JSONObject(body).getString("sql").split(";");
            return executeStatements(sqlStatements);
        } catch (Exception e) {
            log.error("Error parsing request body: ", e);
            return buildErrorResponse(e);
        }
    }
    
    private String executeStatements(String[] sqlStatements) {
    	try (Connection connection = createConnection();
            	CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
    			Statement statement = connection.createStatement()) {
    			SchemaPlus rootSchema = calciteConnection.getRootSchema();
    			List<String> schemas = this.calcite.getAllSchemas();
    			for (Iterator<String> iterator = schemas.iterator(); iterator.hasNext();) {
    				String string = iterator.next();
    				DagserverSchema dynamicSchema = new DagserverSchema(string);
        			rootSchema.add("SCH"+string, dynamicSchema);	
				}
    			boolean hasResultSet = false;
                for (String sql : sqlStatements) {
                    if (!sql.trim().isEmpty()) {
                        hasResultSet = statement.execute(sql.trim());
                    }
                }
                return buildResponse(statement, hasResultSet);
            } catch (SQLException | ClassNotFoundException e) {
                return buildErrorResponse(e);
            }
    }

    private String sanitizeBody(String bodyStr) {
        return bodyStr.replace("\n", " ").replace("\r", " ").trim();
    }

    private Connection createConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.apache.calcite.jdbc.Driver");
        Properties info = new Properties();
        info.setProperty("lex", "JAVA");
        info.setProperty(CalciteConnectionProperty.CASE_SENSITIVE.camelName(), "false");
        info.setProperty("defaultSchema", "SCHEMAS");
        info.setProperty("model", "./src/main/resources/model.json");
        return DriverManager.getConnection("jdbc:calcite:", info);
    }

    private String buildResponse(Statement statement, boolean hasResultSet) throws SQLException {
        JSONObject returnObj = new JSONObject();
        if (hasResultSet) {
            ResultSet resultSet = statement.getResultSet();
            returnObj.put("result", buildResultArray(resultSet));
            returnObj.put("metadata", buildMetadataArray(resultSet.getMetaData()));
        } else {
            int updateCount = statement.getUpdateCount();
            returnObj.put("updateCount", updateCount);
        }
        return returnObj.toString();
    }

    private JSONArray buildResultArray(ResultSet resultSet) throws SQLException {
        JSONArray resultArray = new JSONArray();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (resultSet.next()) {
            JSONObject rowObj = new JSONObject();
            for (int i = 1; i <= columnCount; i++) {
                rowObj.put(metaData.getColumnName(i), resultSet.getString(i));
            }
            resultArray.put(rowObj);
        }
        return resultArray;
    }

    private JSONArray buildMetadataArray(ResultSetMetaData metaData) throws SQLException {
        JSONArray metadataArray = new JSONArray();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            JSONObject columnInfo = new JSONObject();
            columnInfo.put("name", metaData.getColumnName(i));
            columnInfo.put("type", metaData.getColumnTypeName(i));
            columnInfo.put("size", metaData.getColumnDisplaySize(i));
            columnInfo.put("isNullable", metaData.isNullable(i) == ResultSetMetaData.columnNullable);
            metadataArray.put(columnInfo);
        }
        return metadataArray;
    }

    private String buildErrorResponse(Exception e) {
        JSONObject errorObj = new JSONObject();
        errorObj.put("error", e.getMessage());
        log.error("SQL execution error: ", e);
        return errorObj.toString();
    }
}
