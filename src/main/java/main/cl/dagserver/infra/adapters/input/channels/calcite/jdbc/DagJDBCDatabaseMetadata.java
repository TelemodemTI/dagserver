package main.cl.dagserver.infra.adapters.input.channels.calcite.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

public class DagJDBCDatabaseMetadata implements DatabaseMetaData {

	private DagJDBCConnection connection;

	public DagJDBCDatabaseMetadata(DagJDBCConnection connection) {
		this.connection = connection;
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
	public boolean allProceduresAreCallable() throws SQLException {
		
		return false;
	}

	@Override
	public boolean allTablesAreSelectable() throws SQLException {
		
		return false;
	}

	@Override
	public String getURL() throws SQLException {
		return this.connection.getHandler().getNurl();
	}

	@Override
	public String getUserName() throws SQLException {
		
		return null;
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		
		return false;
	}

	@Override
	public boolean nullsAreSortedHigh() throws SQLException {
		
		return false;
	}

	@Override
	public boolean nullsAreSortedLow() throws SQLException {
		
		return false;
	}

	@Override
	public boolean nullsAreSortedAtStart() throws SQLException {
		
		return false;
	}

	@Override
	public boolean nullsAreSortedAtEnd() throws SQLException {
		
		return false;
	}

	@Override
	public String getDatabaseProductName() throws SQLException {
		return "dagdriver";
	}

	@Override
	public String getDatabaseProductVersion() throws SQLException {
		return null;
	}

	@Override
	public String getDriverName() throws SQLException {
		return "dagdriver";
	}

	@Override
	public String getDriverVersion() throws SQLException {
		return "0";
	}

	@Override
	public int getDriverMajorVersion() {
		return 0;
	}

	@Override
	public int getDriverMinorVersion() {
		return 1;
	}

	@Override
	public boolean usesLocalFiles() throws SQLException {
		return false;
	}

	@Override
	public boolean usesLocalFilePerTable() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsMixedCaseIdentifiers() throws SQLException {
		
		return false;
	}

	@Override
	public boolean storesUpperCaseIdentifiers() throws SQLException {
		
		return false;
	}

	@Override
	public boolean storesLowerCaseIdentifiers() throws SQLException {
		
		return false;
	}

	@Override
	public boolean storesMixedCaseIdentifiers() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
		
		return false;
	}

	@Override
	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
		
		return false;
	}

	@Override
	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
		
		return false;
	}

	@Override
	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
		
		return false;
	}

	@Override
	public String getIdentifierQuoteString() throws SQLException {
		
		return null;
	}

	@Override
	public String getSQLKeywords() throws SQLException {
		
		return null;
	}

	@Override
	public String getNumericFunctions() throws SQLException {
		
		return null;
	}

	@Override
	public String getStringFunctions() throws SQLException {
		
		return null;
	}

	@Override
	public String getSystemFunctions() throws SQLException {
		
		return null;
	}

	@Override
	public String getTimeDateFunctions() throws SQLException {
		
		return null;
	}

	@Override
	public String getSearchStringEscape() throws SQLException {
		
		return null;
	}

	@Override
	public String getExtraNameCharacters() throws SQLException {
		
		return null;
	}

	@Override
	public boolean supportsAlterTableWithAddColumn() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsAlterTableWithDropColumn() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsColumnAliasing() throws SQLException {
		
		return false;
	}

	@Override
	public boolean nullPlusNonNullIsNull() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsConvert() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsConvert(int fromType, int toType) throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsTableCorrelationNames() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsDifferentTableCorrelationNames() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsExpressionsInOrderBy() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsOrderByUnrelated() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsGroupBy() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsGroupByUnrelated() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsGroupByBeyondSelect() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsLikeEscapeClause() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsMultipleResultSets() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsMultipleTransactions() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsNonNullableColumns() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsMinimumSQLGrammar() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsCoreSQLGrammar() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsExtendedSQLGrammar() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsANSI92EntryLevelSQL() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsANSI92IntermediateSQL() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsANSI92FullSQL() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsIntegrityEnhancementFacility() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsOuterJoins() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsFullOuterJoins() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsLimitedOuterJoins() throws SQLException {
		
		return false;
	}

	@Override
	public String getSchemaTerm() throws SQLException {
		
		return null;
	}

	@Override
	public String getProcedureTerm() throws SQLException {
		
		return null;
	}

	@Override
	public String getCatalogTerm() throws SQLException {
		
		return null;
	}

	@Override
	public boolean isCatalogAtStart() throws SQLException {
		
		return false;
	}

	@Override
	public String getCatalogSeparator() throws SQLException {
		
		return null;
	}

	@Override
	public boolean supportsSchemasInDataManipulation() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsSchemasInProcedureCalls() throws SQLException {
		
		return true;
	}

	@Override
	public boolean supportsSchemasInTableDefinitions() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsSchemasInIndexDefinitions() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsCatalogsInDataManipulation() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsCatalogsInProcedureCalls() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsCatalogsInTableDefinitions() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsPositionedDelete() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsPositionedUpdate() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsSelectForUpdate() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsStoredProcedures() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsSubqueriesInComparisons() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsSubqueriesInExists() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsSubqueriesInIns() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsSubqueriesInQuantifieds() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsCorrelatedSubqueries() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsUnion() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsUnionAll() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
		
		return false;
	}

	@Override
	public int getMaxBinaryLiteralLength() throws SQLException {
		
		return 0;
	}

	@Override
	public int getMaxCharLiteralLength() throws SQLException {
		
		return 0;
	}

	@Override
	public int getMaxColumnNameLength() throws SQLException {
		
		return 0;
	}

	@Override
	public int getMaxColumnsInGroupBy() throws SQLException {
		
		return 0;
	}

	@Override
	public int getMaxColumnsInIndex() throws SQLException {
		
		return 0;
	}

	@Override
	public int getMaxColumnsInOrderBy() throws SQLException {
		
		return 0;
	}

	@Override
	public int getMaxColumnsInSelect() throws SQLException {
		
		return 0;
	}

	@Override
	public int getMaxColumnsInTable() throws SQLException {
		
		return 0;
	}

	@Override
	public int getMaxConnections() throws SQLException {
		
		return 0;
	}

	@Override
	public int getMaxCursorNameLength() throws SQLException {
		
		return 0;
	}

	@Override
	public int getMaxIndexLength() throws SQLException {
		
		return 0;
	}

	@Override
	public int getMaxSchemaNameLength() throws SQLException {
		
		return 0;
	}

	@Override
	public int getMaxProcedureNameLength() throws SQLException {
		
		return 0;
	}

	@Override
	public int getMaxCatalogNameLength() throws SQLException {
		
		return 0;
	}

	@Override
	public int getMaxRowSize() throws SQLException {
		
		return 0;
	}

	@Override
	public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
		
		return false;
	}

	@Override
	public int getMaxStatementLength() throws SQLException {
		
		return 0;
	}

	@Override
	public int getMaxStatements() throws SQLException {
		
		return 0;
	}

	@Override
	public int getMaxTableNameLength() throws SQLException {
		
		return 0;
	}

	@Override
	public int getMaxTablesInSelect() throws SQLException {
		
		return 0;
	}

	@Override
	public int getMaxUserNameLength() throws SQLException {
		
		return 0;
	}

	@Override
	public int getDefaultTransactionIsolation() throws SQLException {
		
		return 0;
	}

	@Override
	public boolean supportsTransactions() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
		
		return false;
	}

	@Override
	public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
		
		return false;
	}

	@Override
	public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
		
		return false;
	}

	@Override
	public DagJDBCResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern)
			throws SQLException {
		var stmt = new DagJDBCStatement(connection);
		var rs = stmt.executeQuery("SELECT * FROM SCHEMAS.PROCEDURES WHERE TABLE_CAT = '"+catalog+"' AND TABLE_SCHEM = '"+schemaPattern+"'");
		stmt.close();
		return rs;
	}

	@Override
	public DagJDBCResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern,
			String columnNamePattern) throws SQLException {
		var stmt = new DagJDBCStatement(connection);
		var rs = stmt.executeQuery("SELECT * FROM SCHEMAS.COLUMNS WHERE TABLE_NAME = '"+procedureNamePattern+"' and COLUMN_TYPE = 'PROCEDURE'");
		stmt.close();
		return rs;
	}

	@Override
	public DagJDBCResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types)
			throws SQLException {
		var stmt = new DagJDBCStatement(connection);
		
		StringBuilder typeCondition = new StringBuilder();
	    if (types != null && types.length > 0) {
	        typeCondition.append(" AND TABLE_TYPE IN (");
	        for (int i = 0; i < types.length; i++) {
	            typeCondition.append("'").append(types[i]).append("'");
	            if (i < types.length - 1) {
	                typeCondition.append(", ");
	            }
	        }
	        typeCondition.append(")");
	    }
		var rs = stmt.executeQuery("SELECT * FROM SCHEMAS.TABLES WHERE TABLE_SCHEM = '"+schemaPattern+"'"+ typeCondition.toString());
		stmt.close();
		return rs;
	}

	@Override
	public DagJDBCResultSet getSchemas() throws SQLException {
		var stmt = new DagJDBCStatement(connection);
		var rs = stmt.executeQuery("SELECT * FROM SCHEMAS.SCHEMAS");
		stmt.close();
		return rs;
	}

	@Override
	public DagJDBCResultSet getCatalogs() throws SQLException {
		var stmt = new DagJDBCStatement(connection);
		var rs = stmt.executeQuery("SELECT * FROM SCHEMAS.CATALOG");
		stmt.close();
		return rs;
	}

	@Override
	public DagJDBCResultSet getTableTypes() throws SQLException {
		var stmt = new DagJDBCStatement(connection);
		var rs = stmt.executeQuery("SELECT DISTINCT TABLE_TYPE FROM SCHEMAS.TABLES");
		stmt.close();
		return rs;
	}

	@Override
	public DagJDBCResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
			throws SQLException {
		var stmt = new DagJDBCStatement(connection);
		var rs = stmt.executeQuery("SELECT * FROM SCHEMAS.COLUMNS WHERE TABLE_NAME = '"+tableNamePattern+"' and COLUMN_TYPE = 'TABLE'");
		stmt.close();
		return rs;		
	}

	@Override
	public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern)
			throws SQLException {
		
		return null;
	}

	@Override
	public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern)
			throws SQLException {
		
		return null;
	}

	@Override
	public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable)
			throws SQLException {
		
		return null;
	}

	@Override
	public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
		
		return null;
	}

	@Override
	public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
		
		return null;
	}

	@Override
	public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
		
		return null;
	}

	@Override
	public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
		
		return null;
	}

	@Override
	public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable,
			String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
		
		return null;
	}

	@Override
	public ResultSet getTypeInfo() throws SQLException {
		
		return null;
	}

	@Override
	public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate)
			throws SQLException {
		
		return null;
	}

	@Override
	public boolean supportsResultSetType(int type) throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
		
		return false;
	}

	@Override
	public boolean ownUpdatesAreVisible(int type) throws SQLException {
		
		return false;
	}

	@Override
	public boolean ownDeletesAreVisible(int type) throws SQLException {
		
		return false;
	}

	@Override
	public boolean ownInsertsAreVisible(int type) throws SQLException {
		
		return false;
	}

	@Override
	public boolean othersUpdatesAreVisible(int type) throws SQLException {
		
		return false;
	}

	@Override
	public boolean othersDeletesAreVisible(int type) throws SQLException {
		
		return false;
	}

	@Override
	public boolean othersInsertsAreVisible(int type) throws SQLException {
		
		return false;
	}

	@Override
	public boolean updatesAreDetected(int type) throws SQLException {
		
		return false;
	}

	@Override
	public boolean deletesAreDetected(int type) throws SQLException {
		
		return false;
	}

	@Override
	public boolean insertsAreDetected(int type) throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsBatchUpdates() throws SQLException {
		
		return false;
	}

	@Override
	public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types)
			throws SQLException {
		
		return null;
	}

	@Override
	public DagJDBCConnection getConnection() throws SQLException {
		return this.connection;
	}

	@Override
	public boolean supportsSavepoints() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsNamedParameters() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsMultipleOpenResults() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsGetGeneratedKeys() throws SQLException {
		
		return false;
	}

	@Override
	public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
		
		return null;
	}

	@Override
	public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
		
		return null;
	}

	@Override
	public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern,
			String attributeNamePattern) throws SQLException {
		
		return null;
	}

	@Override
	public boolean supportsResultSetHoldability(int holdability) throws SQLException {
		
		return false;
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		
		return 0;
	}

	@Override
	public int getDatabaseMajorVersion() throws SQLException {
		
		return 0;
	}

	@Override
	public int getDatabaseMinorVersion() throws SQLException {
		
		return 1;
	}

	@Override
	public int getJDBCMajorVersion() throws SQLException {
		
		return 0;
	}

	@Override
	public int getJDBCMinorVersion() throws SQLException {
		
		return 0;
	}

	@Override
	public int getSQLStateType() throws SQLException {
		
		return 0;
	}

	@Override
	public boolean locatorsUpdateCopy() throws SQLException {
		
		return false;
	}

	@Override
	public boolean supportsStatementPooling() throws SQLException {
		
		return false;
	}

	@Override
	public RowIdLifetime getRowIdLifetime() throws SQLException {
		
		return null;
	}

	@Override
	public DagJDBCResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
		var stmt = new DagJDBCStatement(connection);
		var rs = stmt.executeQuery("SELECT * FROM SCHEMAS.SCHEMAS WHERE TABLE_CAT = '"+catalog+"'");
		stmt.close();
		return rs;
	}

	@Override
	public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
		
		return false;
	}

	@Override
	public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
		
		return false;
	}

	@Override
	public ResultSet getClientInfoProperties() throws SQLException {
		
		return null;
	}

	@Override
	public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern)
			throws SQLException {
		
		return null;
	}

	@Override
	public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern,
			String columnNamePattern) throws SQLException {
		
		return null;
	}

	@Override
	public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern,
			String columnNamePattern) throws SQLException {
		
		return null;
	}

	@Override
	public boolean generatedKeyAlwaysReturned() throws SQLException {
		
		return false;
	}

}
