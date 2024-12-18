package main.cl.dagserver.infra.adapters.input.channels.calcite.jdbc;


import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.json.JSONArray;

public class DagJDBCResultSetMetaData implements ResultSetMetaData {

	private JSONArray jsonArray;
	
	public DagJDBCResultSetMetaData(JSONArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		
		return false;
	}

	@Override
	public int getColumnCount() throws SQLException {
		return jsonArray.length();
	}

	@Override
	public boolean isAutoIncrement(int column) throws SQLException {
		
		return false;
	}

	@Override
	public boolean isCaseSensitive(int column) throws SQLException {
		
		return false;
	}

	@Override
	public boolean isSearchable(int column) throws SQLException {
		
		return false;
	}

	@Override
	public boolean isCurrency(int column) throws SQLException {
		
		return false;
	}

	@Override
	public int isNullable(int column) throws SQLException {
		
		return 0;
	}

	@Override
	public boolean isSigned(int column) throws SQLException {
		
		return false;
	}

	@Override
	public int getColumnDisplaySize(int column) throws SQLException {
		if(column > jsonArray.length()) {
			throw new SQLException("no existe la columna ::"+jsonArray.toString());
		} else {
			return jsonArray.getJSONObject(column - 1).getInt("size");	
		}
	}

	@Override
	public String getColumnLabel(int column) throws SQLException {
		if(column > jsonArray.length()) {
			throw new SQLException("no existe la columna ::"+column+jsonArray.toString());
		} else {
			return jsonArray.getJSONObject(column - 1).getString("name");
		}
	}

	@Override
	public String getColumnName(int column) throws SQLException {
		if(column > jsonArray.length()) {
			throw new SQLException("no existe la columna ::"+column+jsonArray.toString());
		} else {
			return jsonArray.getJSONObject(column - 1).getString("name");
		}
	}

	@Override
	public String getSchemaName(int column) throws SQLException {
		
		return null;
	}

	@Override
	public int getPrecision(int column) throws SQLException {
		
		return 0;
	}

	@Override
	public int getScale(int column) throws SQLException {
		
		return 0;
	}

	@Override
	public String getTableName(int column) throws SQLException {
		
		return null;
	}

	@Override
	public String getCatalogName(int column) throws SQLException {
		
		return null;
	}

	@Override
	public int getColumnType(int column) throws SQLException {
		
		return 0;
	}

	@Override
	public String getColumnTypeName(int column) throws SQLException {
		
		return null;
	}

	@Override
	public boolean isReadOnly(int column) throws SQLException {
		
		return false;
	}

	@Override
	public boolean isWritable(int column) throws SQLException {
		
		return false;
	}

	@Override
	public boolean isDefinitelyWritable(int column) throws SQLException {
		
		return false;
	}

	@Override
	public String getColumnClassName(int column) throws SQLException {
		
		return null;
	}

}
