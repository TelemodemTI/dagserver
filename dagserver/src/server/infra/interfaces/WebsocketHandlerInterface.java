package server.infra.interfaces;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public interface WebsocketHandlerInterface {
	public void init(WebSocketServer server,WebSocket conn,ClientHandshake handshake);
	public void close(WebSocketServer server,WebSocket conn);
	public void message(WebSocketServer server,WebSocket conn, String message);
}
