package main.cl.dagserver.application.ports.input;

import java.util.List;

public interface GetLogsUseCase {
	String apply(List<String> t);
}
