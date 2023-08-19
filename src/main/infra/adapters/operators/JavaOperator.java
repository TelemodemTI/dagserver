package main.infra.adapters.operators;
import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONObject;

import main.domain.annotations.Operator;
import main.domain.core.DagExecutable;
import main.domain.exceptions.DomainException;
import main.infra.adapters.input.graphql.types.OperatorStage;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;

@Operator(args={"jarPath","className"})
public class JavaOperator extends OperatorStage implements Callable<Serializable> {

	@Override
	public Serializable call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		try {
			Serializable result = this.runCallableFromJar(this.args.getProperty("jarPath"), this.args.getProperty("className"));
			log.debug(this.args);
			log.debug(this.getClass()+" end "+this.name);
			return result;	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}

	private <T> T runCallableFromJar(String jarPath, String className) throws Exception {
        File jarFile = new File(jarPath);
        URL jarUrl = jarFile.toURI().toURL();

        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl});
        Class<?> loadedClass = classLoader.loadClass(className);

        if (Callable.class.isAssignableFrom(loadedClass)) {
            @SuppressWarnings("unchecked")
            Callable<T> callableInstance = (Callable<T>) loadedClass.getDeclaredConstructor().newInstance();
            T result = callableInstance.call();
            classLoader.close();
            return result;
        } else {
            classLoader.close();
            throw new IllegalArgumentException("Class must implement Callable and Serializable.");
        }
    }
	
	@Override
	public Implementation getDinamicInvoke(String stepName,String propkey, String optkey) throws DomainException {
		try {
			return MethodCall.invoke(DagExecutable.class.getDeclaredMethod("addOperator", String.class, Class.class, String.class)).with(stepName, JavaOperator.class,propkey);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}

	@Override
	public JSONObject getMetadataOperator() {
		
		JSONArray params = new JSONArray();
		params.put(new JSONObject("{name:\"jarPath\",type:\"text\"}"));
		params.put(new JSONObject("{name:\"className\",type:\"text\"}"));
		
		JSONObject tag = new JSONObject();
		tag.put("class", "main.infra.adapters.operators.JavaOperator");
		tag.put("name", "JavaOperator");
		tag.put("params", params);
		return tag;
	}
	@Override
	public String getIconImage() {
		return "jar.png";
	}
	
}
