package main.cl.dagserver.domain.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Callable;
import com.nhl.dflib.DataFrame;

import main.cl.dagserver.domain.exceptions.DomainException;

public class DagOperatorApi {
	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	Properties args = new Properties();
	Properties optionals = new Properties();
	Map<String,DataFrame> xcom = new HashMap<>();
	Callable<?> operator;
	
	public DagOperatorApi setArgs(Properties args) {
		this.args = args;
		return this;
	}
	
	public DagOperatorApi setOptionals(Properties optionals) {
		this.optionals = optionals;
		return this;
	}
	
	private String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(index));
        }
        return sb.toString();
    }
	
	public DagOperatorApi setOperator(String operatorName) throws DomainException {
	    try {
	    	Class<?> operatorClass = Class.forName("main.cl.dagserver.infra.adapters.operators." + operatorName);
		    OperatorStage op = (OperatorStage) operatorClass.getDeclaredConstructor().newInstance();
			op.setArgs(args);
			op.setXcom(xcom);
			op.setName(this.generateRandomString(10));
			op.setOptionals(optionals);
			Callable<?> instance  = (Callable<?>) op; 
	        this.operator = instance;
	        return this;	
		} catch (Exception e) {
			throw new DomainException(e);
		}
    }
	public DataFrame execute() throws Exception {
        if (operator == null) {
            throw new IllegalStateException("Operator must be set before execution");
        }
        return (DataFrame) operator.call();
    }
}
