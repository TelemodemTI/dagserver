package main.infra.adapters.input.graphql.interceptors;

import org.apache.log4j.Logger;
import groovyjarjarasm.asm.commons.Method;
import net.bytebuddy.asm.Advice.AllArguments;
import net.bytebuddy.asm.Advice.Origin;
import net.bytebuddy.asm.Advice.This;
import net.bytebuddy.implementation.bind.annotation.Empty;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperMethod;

public class DeletePropertyInterceptor {
	
	private static final Logger logger = Logger.getLogger(DeletePropertyInterceptor.class);
	
	@RuntimeType
	public static Object intercept(@This Object self, 
	                                 @Origin Method method, 
	                                 @AllArguments Object[] args, 
	                                 @SuperMethod(nullIfImpossible = true) Method superMethod,
	                                 @Empty Object defaultValue) throws Throwable {
	    logger.debug("sinverguenza");
		return null;
	}
}
