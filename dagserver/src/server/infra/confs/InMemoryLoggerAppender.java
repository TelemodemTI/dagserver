package server.infra.confs;

import java.text.SimpleDateFormat;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Records logging events in a String.
 * 
 * @author Lorenzo Bettini
 *
 */
public class InMemoryLoggerAppender extends AppenderSkeleton {

	private String dateformat = "";
    private StringBuilder builder = new StringBuilder();

    @Override
    public void close() {
        // nothing to close
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    @Override
    protected void append(LoggingEvent event) {
    	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
    	builder.append(timeStamp + ":" + event.getLevel().toString() + ": " + event.getMessage().toString() + System.lineSeparator());
    }

  
    public String getResult() {
        return builder.toString();
    }
}