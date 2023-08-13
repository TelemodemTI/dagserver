package main.domain.core;

import main.domain.exceptions.DomainException;
import net.bytebuddy.implementation.Implementation;

public interface Operator {
	public Implementation getDinamicInvoke(String stepName) throws DomainException;
}
