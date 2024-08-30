package main.cl.dagserver.infra.adapters.input.channels.calcite.jdbc;


import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;

import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class DagJDBCConnection implements Connection {

	private String url;
	
	public DagJDBCConnection(String url) {
		this.url = url;
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
	public DagJDBCStatement createStatement() throws SQLException {
		return new DagJDBCStatement(this);
	}

	@Override
	public DagJDBCPreparedStatement prepareStatement(String sql) throws SQLException {
		return new DagJDBCPreparedStatement(this, sql);
	}

	@Override
	public DagJDBCCallableStatement prepareCall(String sql) throws SQLException {
		return new DagJDBCCallableStatement(this,sql);
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return true;
	}

	@Override
	public void commit() throws SQLException {
		throw new SQLFeatureNotSupportedException();
		
	}

	@Override
	public void rollback() throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public void close() throws SQLException {
		
	}

	@Override
	public boolean isClosed() throws SQLException {
		return true;
	}

	@Override
	public DagJDBCDatabaseMetadata getMetaData() throws SQLException {
		return new DagJDBCDatabaseMetadata(this);
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return false;
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
	}

	@Override
	public String getCatalog() throws SQLException {
		return null;
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		return Connection.TRANSACTION_NONE;
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return null;
	}

	@Override
	public void clearWarnings() throws SQLException {
	}

	@Override
	public DagJDBCStatement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return new DagJDBCStatement(this,resultSetType,resultSetConcurrency);
	}

	@Override
	public DagJDBCPreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		return new DagJDBCPreparedStatement(this,sql,resultSetType,resultSetConcurrency);
	}

	@Override
	public DagJDBCCallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return new DagJDBCCallableStatement(this,sql,resultSetType,resultSetConcurrency);
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return null;
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
	}

	@Override
	public int getHoldability() throws SQLException {
		return 0;
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		return null;
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		return null;
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
	}

	@Override
	public DagJDBCStatement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return new DagJDBCStatement(this,resultSetType,resultSetConcurrency,resultSetHoldability);
	}

	@Override
	public DagJDBCPreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		return new DagJDBCPreparedStatement(this,sql,resultSetType,resultSetConcurrency,resultSetHoldability);
	}

	@Override
	public DagJDBCCallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		return new DagJDBCCallableStatement(this,sql,resultSetType,resultSetConcurrency,resultSetHoldability);
	}

	@Override
	public DagJDBCPreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return new DagJDBCPreparedStatement(this,sql,autoGeneratedKeys);
	}

	@Override
	public DagJDBCPreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		return new DagJDBCPreparedStatement(this,sql,columnIndexes);
	}

	@Override
	public DagJDBCPreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		return new DagJDBCPreparedStatement(this,sql,columnNames);
	}

	@Override
	public Clob createClob() throws SQLException {
		return null;
	}

	@Override
	public Blob createBlob() throws SQLException {
		return null;
	}

	@Override
	public NClob createNClob() throws SQLException {
		return null;
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return null;
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		return false;
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {		
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		return null;
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return null;
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return null;
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return null;
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		
	}

	@Override
	public String getSchema() throws SQLException {
		return "";
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		return 0;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	

}
