package main.cl.dagserver.infra.adapters.confs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import jakarta.annotation.PostConstruct;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.input.channels.InputChannel;

@Configuration()
public class ChannelScanner {

	private static final String BASEOPPKG = "main.cl.dagserver";
	protected Map<String,Thread> bindings = new HashMap<>();
	
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@PostConstruct
	public void scan() throws DomainException {
		try {
			var lista = this.availableChannels();
			for (Class<? extends InputChannel> class1 : lista) {
                if (class1.getCanonicalName().startsWith(BASEOPPKG)) {
                	InputChannel op = applicationContext.getBean(class1);
                	Thread listenerT = new Thread(() -> {
	           			try {
	           					op.runForever();
	                    } catch (Exception e) {
	           					eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "listenerHandler"));
	                    }
                	});
                	listenerT.start();
                	this.bindings.put(class1.getCanonicalName(), listenerT);
                }
            } 
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	
	public Set<Class<? extends InputChannel>> availableChannels() throws DomainException{
		try (ScanResult scanResult = new ClassGraph()
                .acceptPackages(BASEOPPKG)
                .scan()) {
            Set<Class<InputChannel>> reflecteds = scanResult
                    .getSubclasses(InputChannel.class.getName())
                    .loadClasses(InputChannel.class)
                    .stream()
                    .collect(Collectors.toSet());
            Set<Class<? extends InputChannel>> lista = reflecteds.stream().collect(Collectors.toSet());
            return lista;
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
}
