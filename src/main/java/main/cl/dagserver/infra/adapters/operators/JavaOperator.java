package main.cl.dagserver.infra.adapters.operators;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.json.JSONObject;

import com.nhl.dflib.DataFrame;

import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.confs.DagPathClassLoadHelper;

@Operator(args={"classpath","className"})
public class JavaOperator extends OperatorStage {

	private DagPathClassLoadHelper helper = new DagPathClassLoadHelper();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public DataFrame call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		try {
			List<String> fJar = new ArrayList<>();
			String fpath = this.args.getProperty("classpath");
			this.searchJarFiles(new File(fpath),fJar );
			List<URI> list = new ArrayList<>();
			for (Iterator<String> iterator = fJar.iterator(); iterator.hasNext();) {
				String jarpath = iterator.next();
				list.add(new File(jarpath).toURI());
			}
			Object result = this.runCallableFromJar(this.args.getProperty("className"),list);
			log.debug(this.args);
			log.debug(this.getClass()+" end "+this.name);
			
			if (result instanceof List) {
		        var rvl = (List) result;
		        return OperatorStage.buildDataFrame(rvl);	        
		    } else if (result instanceof Map) {
		    	var rvm = (Map) result;
		    	return OperatorStage.buildDataFrame(Arrays.asList(rvm));
		    } else if(result instanceof DataFrame) {
		    	return (DataFrame) result;
		    } else {
		        return this.createFrame("output", result);
		    }
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}

	
	private void searchJarFiles(File directorio, List<String> archivosJar) {
        File[] archivos = directorio.listFiles();

        if (archivos != null) {
            for (File archivo : archivos) {
                if (archivo.isFile() && archivo.getName().endsWith(".jar")) {
                    archivosJar.add(archivo.getAbsolutePath());
                } else if (archivo.isDirectory()) {
                	searchJarFiles(archivo, archivosJar);
                }
            }
        }
    }

	
	
	private <T> T runCallableFromJar(String className, List<URI> list) throws Exception {
        try {
        	Class<?> loadedClass = helper.loadFromOperatorJar(className,list);
        	if (Callable.class.isAssignableFrom(loadedClass)) {
                @SuppressWarnings("unchecked")
                Callable<T> callableInstance = (Callable<T>) loadedClass.getDeclaredConstructor().newInstance();
                if(this.methodExist(callableInstance, "setXcom")){
                	this.execSetParams(callableInstance,"setXcom" , this.xcom.toString());
                }
                if(this.methodExist(callableInstance, "setArgs")){
                	this.execSetParams(callableInstance,"setArgs" , this.args);
                }
                return callableInstance.call();
            } else {
                throw new IllegalArgumentException("Class must implement Callable and Serializable.");
            }
		} catch (Exception e) {
			throw new DomainException(e);
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
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.JavaOperator");
		metadata.setParameter("classpath", "text");
		metadata.setParameter("className", "text");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "jar.png";
	}
	
}
