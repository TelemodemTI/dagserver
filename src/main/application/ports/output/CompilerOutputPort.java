package main.application.ports.output;

public interface CompilerOutputPort {

	void createJar(String bin, Boolean force) throws Exception;

}
