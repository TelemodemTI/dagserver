package main.cl.dagserver.infra.adapters.input.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import main.cl.dagserver.application.ports.input.ExceptionListenerUseCase;
import main.cl.dagserver.domain.core.ExceptionEventLog;

@Component
public class ExceptionEventListener implements ApplicationListener<ExceptionEventLog> {

    private final ExceptionListenerUseCase handler;

    @Autowired
    public ExceptionEventListener(ExceptionListenerUseCase handler) {
        this.handler = handler;
    }

    @Override
    public void onApplicationEvent(ExceptionEventLog event) {
        handler.registerException(event);
    }
}