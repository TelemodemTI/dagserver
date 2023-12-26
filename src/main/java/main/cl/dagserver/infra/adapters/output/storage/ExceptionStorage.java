package main.cl.dagserver.infra.adapters.output.storage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import main.cl.dagserver.application.ports.output.ExceptionStorageUseCase;
import main.cl.dagserver.domain.core.ExceptionEventLog;

@Component
@ImportResource("classpath:properties-config.xml")
public class ExceptionStorage implements ExceptionStorageUseCase {

	@Value("${param.storage.exception}")
	private String exceptionstoragefile;
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void add(ExceptionEventLog event) {
		try(DB db = DBMaker.fileDB(exceptionstoragefile).make()){
			ConcurrentMap map = db.hashMap("exceptions").createOrOpen();
			String classname = event.getSource().getClass().getCanonicalName();
			String method = event.getMessage();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMsshhmmss");
			StringWriter stringWriter = new StringWriter();
	        PrintWriter printWriter = new PrintWriter(stringWriter);
	        event.getException().printStackTrace(printWriter);
	        String stacktrace = stringWriter.toString();      
			Map<String,String> excpd = new HashMap<>();
			excpd.put("classname", classname);
			excpd.put("method",method);
			excpd.put("stacktrace",stacktrace);
			map.put(sdf.format(new Date()), excpd);	
		}
	}

}
