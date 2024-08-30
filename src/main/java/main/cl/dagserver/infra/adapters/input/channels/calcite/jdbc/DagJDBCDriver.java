package main.cl.dagserver.infra.adapters.input.channels.calcite.jdbc;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class DagJDBCDriver implements Driver {

	@Override
	public DagJDBCConnection connect(String url, Properties info) throws SQLException {
		if (!acceptsURL(url)) {
            return null; // Returning null if the URL is not accepted
        }
        try {
        	String nurl = url.replace("jdbc:dag:", "");
            // Open a connection to the specified URL
            URL endpoint = new URL(nurl);
            HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            return new DagJDBCConnection(nurl);

        } catch (IOException e) {
            throw new SQLException(url, e);
        }
	}

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		 return url != null && url.startsWith("jdbc:dag:");
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		var arr = new DriverPropertyInfo[1];
		arr[0]=new DriverPropertyInfo("test", "value");
		return arr;
	}

	@Override
	public int getMajorVersion() {
		return 0;
	}

	@Override
	public int getMinorVersion() {
		return 1;
	}

	@Override
	public boolean jdbcCompliant() {
		return false;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}

}
