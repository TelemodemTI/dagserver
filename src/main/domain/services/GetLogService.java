package main.domain.services;

import java.util.List;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import main.application.ports.input.GetLogUseCase;
import main.domain.repositories.SchedulerRepository;


@Service
public class GetLogService implements GetLogUseCase,Function<List<String>,String> {

	
	@Autowired
	SchedulerRepository repository;
	
	@Override
	public String apply(List<String> t) {
		var log = repository.getLog(Integer.parseInt( t.get(1)));
		return log.getValue();
	}

}
