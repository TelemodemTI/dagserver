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

import org.json.JSONObject;
public class DagJDBCDriver implements Driver {

	@Override
	public DagJDBCConnection connect(String url, Properties info) throws SQLException {
		if (!acceptsURL(url)) {
            return null; // Returning null if the URL is not accepted
        }
        try {
        	//jdbc:dag:http://localhost:8081/calcite/execute
        	//jdbc:dag:localhost:8081
        	String username = info.getProperty("user");
            String password = info.getProperty("password");
            System.out.println("el usuario es:"+new JSONObject(info).toString());
        	var handler = new DagJDBCAuth(url,username,password);
            URL endpoint = new URL(handler.getNurl());
            HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
            connection.setRequestMethod("POST");
            handler.secure(connection);
            connection.setDoOutput(true);
            return new DagJDBCConnection(handler);

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
		arr[0]=new DriverPropertyInfo("username", "");
		arr[1]=new DriverPropertyInfo("password", "");
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
