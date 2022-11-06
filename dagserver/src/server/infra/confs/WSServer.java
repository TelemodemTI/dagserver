package server.infra.confs;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import server.infra.websockets.WebSocketHandler;
import server.infra.annotations.WebsocketType;
import server.infra.interfaces.WebsocketHandlerInterface;

public class WSServer extends WebSocketServer  {

	private static Logger logger = Logger.getLogger(WSServer.class);
	
	private Map<String,WebsocketHandlerInterface> handlers;
	
	public WSServer( ) throws UnknownHostException {
		super( new InetSocketAddress( 65101 ) );
		setReuseAddr(true);
		this.handlers = new HashMap<String,WebsocketHandlerInterface>();
		var handler = new WebSocketHandler();
		WebsocketType type = handler.getClass().getAnnotation(WebsocketType.class);
		this.handlers.put(type.resource(),handler);
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		this.handlers.get(conn.getResourceDescriptor()).init(this, conn, handshake);
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		this.handlers.get(conn.getResourceDescriptor()).close(this,conn);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		this.handlers.get(conn.getResourceDescriptor()).message(this, conn, message);
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
	}

	@Override
	public void onStart() {
		setConnectionLostTimeout(0);
		setConnectionLostTimeout(100);
	}
}
