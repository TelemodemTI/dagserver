package main.infra.adapters.operators;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.Callable;
import org.json.JSONObject;
import main.domain.annotations.Operator;
import main.domain.core.MetadataManager;
import main.domain.core.OperatorStage;
import main.domain.exceptions.DomainException;

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
        try(
        		URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl});
        	) {
        	Class<?> loadedClass = classLoader.loadClass(className);
        	if (Callable.class.isAssignableFrom(loadedClass)) {
                @SuppressWarnings("unchecked")
                Callable<T> callableInstance = (Callable<T>) loadedClass.getDeclaredConstructor().newInstance();
                if(this.methodExist(callableInstance, "setXcom")){
                	this.execSetParams(callableInstance,"setXcom" , this.xcom);
                }
                if(this.methodExist(callableInstance, "setArgs")){
                	this.execSetParams(callableInstance,"setArgs" , this.args);
                }
                return callableInstance.call();
            } else {
                throw new IllegalArgumentException("Class must implement Callable and Serializable.");
            }
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
    }
	
	private void execSetParams(Object objeto, String nombreMetodo, Object data) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	        Class<?> clase = objeto.getClass();
	        Method metodo = clase.getMethod(nombreMetodo, Object.class);
	        metodo.invoke(objeto, data);
	}
	
	private boolean methodExist(Object objeto, String nombreMetodo) {
        Class<?> clase = objeto.getClass();
        for (Method metodo : clase.getMethods()) {
            if (metodo.getName().equals(nombreMetodo)) {
                return true;
            }
        }
        return false;
    }

	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.infra.adapters.operators.JavaOperator");
		metadata.setParameter("jarPath", "text");
		metadata.setParameter("className", "text");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "jar.png";
	}
	
}
