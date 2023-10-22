package main.cl.dagserver.domain.core;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CoreAspect {

	private Logger log = LoggerFactory.getLogger(CoreAspect.class);
	
	@Pointcut("execution(public * main.cl.dagserver..*.*(..))")
	public void publicMethods() {}

	
	@Around("publicMethods()")
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
	    long startTime = System.currentTimeMillis();
	    Object result = joinPoint.proceed();
	    long elapsedTime = System.currentTimeMillis() - startTime;
	    log.debug("Method [{}] executed in {} ms", joinPoint.getSignature(), elapsedTime);
	    return result;
	  }
}
