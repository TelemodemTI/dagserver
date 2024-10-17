package main.cl.dagserver.infra.adapters.input.channels.calcite.jdbc;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

 class DagJDBCCallableStatementTest {

    private DagJDBCCallableStatement callableStatement;
    private DagJDBCConnection mockConnection;
    private String testSql = "SELECT * FROM users";
    
    @BeforeEach
     void setup() {
        mockConnection = Mockito.mock(DagJDBCConnection.class);
    }

    @Test
     void testExecuteQuery() throws SQLException {
        DagJDBCResultSet mockResultSet = Mockito.mock(DagJDBCResultSet.class);
        callableStatement = Mockito.spy(new DagJDBCCallableStatement(mockConnection, testSql));
        
        Mockito.doReturn(mockResultSet).when(callableStatement).executeQuery(testSql);
        
        DagJDBCResultSet result = callableStatement.executeQuery();
        assertNotNull(result);
        assertEquals(mockResultSet, result);
        
        Mockito.verify(callableStatement).executeQuery(testSql);
    }
    
    
    @Test
     void testExecute() throws SQLException {
        DagJDBCResultSet mockResultSet = Mockito.mock(DagJDBCResultSet.class);
        callableStatement = Mockito.spy(new DagJDBCCallableStatement(mockConnection, testSql));
        
        Mockito.doReturn(mockResultSet).when(callableStatement).executeQuery(testSql);
        
        Boolean result = callableStatement.execute();
        assertNotNull(result);
    }
    
    @Test
     void testExecuteQuery2() throws SQLException {
        DagJDBCResultSet mockResultSet = Mockito.mock(DagJDBCResultSet.class);
        callableStatement = Mockito.spy(new DagJDBCCallableStatement(mockConnection, testSql,1,1));
        
        Mockito.doReturn(mockResultSet).when(callableStatement).executeQuery(testSql);
        
        DagJDBCResultSet result = callableStatement.executeQuery();
        assertNotNull(result);
        assertEquals(mockResultSet, result);
        
        Mockito.verify(callableStatement).executeQuery(testSql);
    }
    
    @Test
     void testExecuteQuery3() throws SQLException {
        DagJDBCResultSet mockResultSet = Mockito.mock(DagJDBCResultSet.class);
        callableStatement = Mockito.spy(new DagJDBCCallableStatement(mockConnection, testSql,1,1,1));
        
        Mockito.doReturn(mockResultSet).when(callableStatement).executeQuery(testSql);
        
        DagJDBCResultSet result = callableStatement.executeQuery();
        assertNotNull(result);
        assertEquals(mockResultSet, result);
        
        Mockito.verify(callableStatement).executeQuery(testSql);
    }

    @Test
     void testExecuteUpdate() throws SQLException {
        callableStatement = Mockito.spy(new DagJDBCCallableStatement(mockConnection, testSql));
        
        Mockito.doReturn(1).when(callableStatement).executeUpdate(testSql);
        
        int rowsUpdated = callableStatement.executeUpdate();
        assertEquals(1, rowsUpdated);
        
        Mockito.verify(callableStatement).executeUpdate(testSql);
    }
}
