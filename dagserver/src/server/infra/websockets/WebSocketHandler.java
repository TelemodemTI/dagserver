package server.infra.websockets;

import org.apache.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import server.infra.annotations.WebsocketType;
import server.infra.interfaces.WebsocketHandlerInterface;


@WebsocketType(resource = "/test")
public class WebSocketHandler implements WebsocketHandlerInterface {

	private static Logger logger = Logger.getLogger(WebSocketHandler.class);
	
	public WebSocketHandler(){
		logger.debug("init WebSocketHandler");
	}

	@Override
	public void init(WebSocketServer server, WebSocket conn, ClientHandshake handshake) {
		conn.send("Welcome to the server!"); 
		server.broadcast( "new connection: " + handshake.getResourceDescriptor() ); 
	}

	@Override
	public void close(WebSocketServer server,WebSocket conn) {
		server.broadcast( conn + " has left the room!" );
	}

	@Override
	public void message(WebSocketServer server, WebSocket conn, String message) {
		server.broadcast( message );
	}
}
