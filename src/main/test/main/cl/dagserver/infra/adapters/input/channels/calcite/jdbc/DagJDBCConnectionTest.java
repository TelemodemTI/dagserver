package main.cl.dagserver.infra.adapters.input.channels.calcite.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DagJDBCConnectionTest {

    @Mock
    private DagJDBCAuth mockHandler;

    private DagJDBCConnection connection;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        connection = new DagJDBCConnection(mockHandler);
    }

    @Test
    void testCreateStatement() throws SQLException {
        assertNotNull(connection.createStatement());
    }

    @Test
    void testPrepareStatement() throws SQLException {
        String sql = "SELECT * FROM test";
        assertNotNull(connection.prepareStatement(sql));
    }

    @Test
    void testPrepareCall() throws SQLException {
        String sql = "CALL procedure";
        assertNotNull(connection.prepareCall(sql));
    }

    @Test
    void testNativeSQL_throwsException() {
        assertThrows(SQLFeatureNotSupportedException.class, () -> connection.nativeSQL("SELECT * FROM test"));
    }

    @Test
    void testSetAutoCommit_throwsException() {
        assertThrows(SQLFeatureNotSupportedException.class, () -> connection.setAutoCommit(false));
    }

    @Test
    void testGetAutoCommit() throws SQLException {
        assertTrue(connection.getAutoCommit());
    }

    @Test
    void testCommit_throwsException() {
        assertThrows(SQLFeatureNotSupportedException.class, connection::commit);
    }

    @Test
    void testRollback_throwsException() {
        assertThrows(SQLFeatureNotSupportedException.class, connection::rollback);
    }

    @Test
    void testClose() throws SQLException {
        connection.close();
        // No exception expected
    }

    @Test
    void testIsClosed() throws SQLException {
        assertTrue(connection.isClosed());
    }

    @Test
    void testGetMetaData() throws SQLException {
        assertNotNull(connection.getMetaData());
    }

    @Test
    void testSetReadOnly() throws SQLException {
        connection.setReadOnly(true);
        // No exception expected
    }

    @Test
    void testIsReadOnly() throws SQLException {
        assertFalse(connection.isReadOnly());
    }

    @Test
    void testSetCatalog() throws SQLException {
        connection.setCatalog("testCatalog");
        // No exception expected
    }

    @Test
    void testGetCatalog() throws SQLException {
        assertNull(connection.getCatalog());
    }

    @Test
    void testSetTransactionIsolation() throws SQLException {
        connection.setTransactionIsolation(Connection.TRANSACTION_NONE);
        // No exception expected
    }

    @Test
    void testGetTransactionIsolation() throws SQLException {
        assertEquals(Connection.TRANSACTION_NONE, connection.getTransactionIsolation());
    }

    @Test
    void testGetWarnings() throws SQLException {
        assertNull(connection.getWarnings());
    }

    @Test
    void testClearWarnings() throws SQLException {
        connection.clearWarnings();
        // No exception expected
    }

    @Test
    void testGetHandler() {
        assertEquals(mockHandler, connection.getHandler());
    }

    @Test
    void testSetHandler() {
        DagJDBCAuth newHandler = mock(DagJDBCAuth.class);
        connection.setHandler(newHandler);
        assertEquals(newHandler, connection.getHandler());
    }
}
