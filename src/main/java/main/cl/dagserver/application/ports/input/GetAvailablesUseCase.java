package main.cl.dagserver.application.ports.input;

import java.util.List;

public interface GetAvailablesUseCase {

	String apply(List<String> t);
}
