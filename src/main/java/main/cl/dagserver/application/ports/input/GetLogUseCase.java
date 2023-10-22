package main.cl.dagserver.application.ports.input;

import java.util.List;

public interface GetLogUseCase {
	String apply(List<String> t);
}
