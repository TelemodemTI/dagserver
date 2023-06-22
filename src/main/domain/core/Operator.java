package main.domain.core;

import net.bytebuddy.implementation.Implementation;

public interface Operator {
	public Implementation getDinamicInvoke(String stepName) throws Exception;
}
