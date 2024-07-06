package main.cl.dagserver.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import main.cl.dagserver.application.ports.input.ExceptionListenerUseCase;
import main.cl.dagserver.application.ports.output.Storage;
import main.cl.dagserver.domain.core.ExceptionEventLog;

@Service
public class InternalEventsService implements ExceptionListenerUseCase {

    private final Storage storage;

    @Autowired
    public InternalEventsService(Storage storage) {
        this.storage = storage;
    }

    @Override
    public void registerException(ExceptionEventLog event) {
        storage.addException(event);
    }
}