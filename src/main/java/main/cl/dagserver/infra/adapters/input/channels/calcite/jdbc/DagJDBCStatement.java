package main.cl.dagserver.infra.adapters.input.channels.calcite.jdbc;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;
import lombok.extern.log4j.Log4j2;

@Log4j2
@SuppressWarnings("unused")
public class DagJDBCStatement implements Statement {

	private DagJDBCConnection connection; 
	private String sql;
	private Integer  resultSetType;
	private Integer  resultSetConcurrency;
	private Integer resultSetHoldability;
	private Integer maxFieldSize = 0;
	private Integer maxRows = 0;
	private Integer queryTimeout = 5;
	
	private DagJDBCResultSet result = null;
	
	public DagJDBCStatement(DagJDBCConnection connection) {
		log.info("DagJDBCStatement");
		this.connection = connection;
	}

	public DagJDBCStatement(DagJDBCConnection connection, int resultSetType, int resultSetConcurrency) {
		this.connection = connection;
		this.resultSetType = resultSetType;
		this.resultSetConcurrency = resultSetConcurrency;
	}

	public DagJDBCStatement(DagJDBCConnection connection, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) {
		this.connection = connection;
		this.resultSetType = resultSetType;
		this.resultSetConcurrency = resultSetConcurrency;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return iface.isInstance(this);
	}

	@Override
	public DagJDBCResultSet executeQuery(String sql) throws SQLException {
	    try {
	        this.sql = sql;
	        
	        URL endpoint = new URL(this.connection.getUrl());
            HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
	        conn.setRequestProperty("Content-Type", "application/json");
	        String jsonBody = "{\"sql\":\"" + sql + "\"}";
	        conn.getOutputStream().write(jsonBody.getBytes());
	        int responseCode = conn.getResponseCode();
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	        	var result = parseResultSet(conn.getInputStream());
	        	conn.disconnect();
	        	return result;
	        } else {
	        	conn.disconnect();
	            throw new SQLException("Failed to execute query. HTTP response code: " + responseCode);
	        }
	    } catch (Exception e) {
	        throw new SQLException("Error executing query: " + e.getMessage(), e);
	    }
	}

	private DagJDBCResultSet parseResultSet(InputStream inputStream) throws SQLException {
	    try {
	        String jsonResponse = new BufferedReader(new InputStreamReader(inputStream))
	                .lines().collect(Collectors.joining("\n"));
	        JSONObject jsonArray = new JSONObject(jsonResponse);
	        var rv = new DagJDBCResultSet(jsonArray.getJSONArray("result"),jsonArray.getJSONArray("metadata"));
	        return rv;
	    } catch (Exception e) {
	        throw new SQLException("Error parsing ResultSet from JSON response", e);
	    }
	}
	@Override
	public int executeUpdate(String sql) throws SQLException {
	    try {
	        this.sql = sql;
	        URL endpoint = new URL(this.connection.getUrl());
            HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();
	        conn.setRequestMethod("POST");
	        conn.setDoOutput(true);
	        conn.setRequestProperty("Content-Type", "application/json");

	        String jsonBody = "{\"sql\":\"" + sql + "\"}";
	        conn.getOutputStream().write(jsonBody.getBytes());

	        int responseCode = conn.getResponseCode();
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            // Read the response and return the number of affected rows
	            var intv = readUpdateCount(conn.getInputStream());
	            conn.disconnect();
	            return intv;
	        } else {
	        	conn.disconnect();
	            throw new SQLException("Failed to execute update. HTTP response code: " + responseCode);
	        }
	    } catch (Exception e) {
	        throw new SQLException("Error executing update: " + e.getMessage(), e);
	    }
	}

	private int readUpdateCount(InputStream inputStream) throws SQLException {
	    try {
	        // Leer el InputStream y convertirlo en un String
	        String jsonResponse = new BufferedReader(new InputStreamReader(inputStream))
	                .lines().collect(Collectors.joining("\n"));

	        // Convertir la respuesta JSON en un JSONArray
	        JSONArray jsonArray = new JSONArray(jsonResponse);

	        // El número de objetos JSON en el JSONArray representa el número de filas afectadas
	        return jsonArray.length();
	    } catch (Exception e) {
	        throw new SQLException("Error reading update count from JSON response", e);
	    }
	}

	@Override
	public void close() throws SQLException {
		
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		return this.maxFieldSize.intValue();
	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		this.maxFieldSize = max;
	}

	@Override
	public int getMaxRows() throws SQLException {
		return this.maxRows.intValue();
	}

	@Override
	public void setMaxRows(int max) throws SQLException {
		this.maxRows = max;
	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		return this.queryTimeout;
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		this.queryTimeout = seconds;
	}

	@Override
	public void cancel() throws SQLException {
		
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return null;
	}

	@Override
	public void clearWarnings() throws SQLException {		
	}

	@Override
	public void setCursorName(String name) throws SQLException {
		
	}

	@Override
	public boolean execute(String sql) throws SQLException {
		if(sql.toLowerCase().startsWith("select")) {
			this.result = this.executeQuery(sql);
			return true;
		} else {
			this.executeUpdate(sql);
			return true;
		}
	}

	@Override
	public DagJDBCResultSet getResultSet() throws SQLException {
		return result;
	}

	@Override
	public int getUpdateCount() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFetchDirection() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFetchSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getResultSetType() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addBatch(String sql) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearBatch() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int[] executeBatch() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DagJDBCConnection getConnection() throws SQLException {
		return this.connection;
	}

	@Override
	public boolean getMoreResults(int current) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DagJDBCResultSet getGeneratedKeys() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		return this.executeUpdate(sql);
	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		return this.executeUpdate(sql);
	}

	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		return this.executeUpdate(sql);
	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		if(sql.toLowerCase().startsWith("select")) {
			this.result = this.executeQuery(sql);
			return true;
		} else {
			this.executeUpdate(sql);
			return true;
		}
	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		if(sql.toLowerCase().startsWith("select")) {
			this.result = this.executeQuery(sql);
			return true;
		} else {
			this.executeUpdate(sql);
			return true;
		}
	}

	@Override
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		if(sql.toLowerCase().startsWith("select")) {
			this.result = this.executeQuery(sql);
			return true;
		} else {
			this.executeUpdate(sql);
			return true;
		}
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isPoolable() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void closeOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

}
