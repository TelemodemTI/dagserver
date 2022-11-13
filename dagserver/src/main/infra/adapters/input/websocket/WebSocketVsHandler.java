package main.infra.adapters.input.websocket;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import main.application.ports.input.GetAvailablesUseCase;
import main.application.ports.input.LoginUseCase;
import main.domain.messages.Message;



public class WebSocketVsHandler extends TextWebSocketHandler  {


	@Autowired
	GetAvailablesUseCase displayer;
	
	@Autowired
	LoginUseCase login;
	
	@Autowired
	ApplicationContext  applicationContext;
	
	
	private static Logger logger = Logger.getLogger(WebSocketVsHandler.class);
	
	public WebSocketVsHandler( ){
		logger.debug("init WebSocketHandler");
		
	}

	@Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Message msg = mapper.readValue(message.getPayload(), Message.class);
		if(msg.getType().equals("login")) {
			var result = login.apply(msg.getArgs()).toString();
			session.sendMessage(new TextMessage(result));
		} else {
			var result = displayer.apply(msg.getArgs()).toString();
			session.sendMessage(new TextMessage(result));
		}
    }
}
