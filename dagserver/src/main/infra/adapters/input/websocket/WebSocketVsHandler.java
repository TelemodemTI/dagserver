package main.infra.adapters.input.websocket;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import main.application.ports.input.GetAvailablesUseCase;
import main.application.ports.input.GetLogsUseCase;
import main.application.ports.input.LoginUseCase;
import main.domain.messages.Message;


@SuppressWarnings({"rawtypes","unchecked"})
public class WebSocketVsHandler extends TextWebSocketHandler  {
	
	@Autowired
	ApplicationContext  applicationContext;
	
	@Autowired
	LoginUseCase  login;
	
	@Autowired 
	GetAvailablesUseCase available;
	
	@Autowired
	GetLogsUseCase logs;
	
	private Map<String,Function> commands = new HashMap<>();
	
	private static Logger logger = Logger.getLogger(WebSocketVsHandler.class);
	
	
	public WebSocketVsHandler( ){
		logger.debug("init WebSocketHandler");		
	}
	
	public WebSocketVsHandler initFactory() throws Exception {
		if(commands.isEmpty()) {
			commands.put("login", (Function) login);
			commands.put("availables", (Function) available);
			commands.put("logs", (Function) logs);
		}
		return this;
	}

	@Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		this.initFactory();
		ObjectMapper mapper = new ObjectMapper();
		Message msg = mapper.readValue(message.getPayload(), Message.class);
		var result = commands.get(msg.getType()).apply(msg.getArgs()).toString();
		session.sendMessage(new TextMessage(result));
    }
}
