package main.cl.dagserver.domain.core;

import main.cl.dagserver.domain.exceptions.DomainException;
import net.bytebuddy.implementation.Implementation;

public interface Operator {
	public Implementation getDinamicInvoke(String stepName) throws DomainException;
}
