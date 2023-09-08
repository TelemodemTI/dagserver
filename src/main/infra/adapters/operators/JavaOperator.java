package main.infra.adapters.operators;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.json.JSONObject;
import main.domain.annotations.Operator;
import main.domain.core.MetadataManager;
import main.domain.core.OperatorStage;
import main.domain.exceptions.DomainException;
import main.infra.adapters.confs.DagPathClassLoadHelper;

@Operator(args={"jarPath","className"})
public class JavaOperator extends OperatorStage implements Callable<Serializable> {

	private DagPathClassLoadHelper helper = new DagPathClassLoadHelper();
	
	@Override
	public Serializable call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		try {
			String[] jars = this.args.getProperty("jarPath").split(";");
			List<URI> list = new ArrayList<>();
			for (int i = 0; i < jars.length; i++) {
				String string = jars[i];
				list.add(new File(string).toURI());
			}
			Serializable result = this.runCallableFromJar(this.args.getProperty("className"),list);
			log.debug(this.args);
			log.debug(this.getClass()+" end "+this.name);
			return result;	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}

	private <T> T runCallableFromJar(String className, List<URI> list) throws Exception {
        try {
        	Class<?> loadedClass = helper.loadFromOperatorJar(className,list);
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
