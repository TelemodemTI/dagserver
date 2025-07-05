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
import org.springframework.context.ApplicationContext;

import com.nhl.dflib.DataFrame;

import main.cl.dagserver.application.ports.input.InternalOperatorUseCase;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.DataFrameUtils;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.confs.ApplicationContextUtils;

@Operator(args={"classpath","className"})
public class JavaOperator extends OperatorStage {

	@SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	@Override
	public DataFrame call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		try {
			ApplicationContext appCtx = new ApplicationContextUtils().getApplicationContext();
			if(appCtx != null) {
				var handler =  appCtx.getBean("internalOperatorService", InternalOperatorUseCase.class);
				List<String> fJar = new ArrayList<>();
				String fpath = this.getInputProperty("classpath");
				this.searchJarFiles(new File(fpath),fJar );
				List<URI> list = new ArrayList<>();
				for (Iterator<String> iterator = fJar.iterator(); iterator.hasNext();) {
					String jarpath = iterator.next();
					list.add(new File(jarpath).toURI());
				}
				Class<?> loadedClass = handler.loadFromOperatorJar(this.getInputProperty("className"),list);
				Object result = this.runCallableFromJar(loadedClass);
				log.debug(this.args);
				log.debug(this.getClass()+" end "+this.name);
				
				if (result instanceof List) {
			        var rvl = (List) result;
			        return DataFrameUtils.buildDataFrameFromObject(rvl);	        
			    } else if (result instanceof Map) {
			    	var rvm = (Map) result;
			    	return DataFrameUtils.buildDataFrameFromMap(Arrays.asList(rvm));
			    } else if(result instanceof DataFrame) {
			    	return (DataFrame) result;
			    } else {
			        return DataFrameUtils.createFrame("output", result);
			    }
			} else {
				throw new DomainException(new Exception("jar not loaded"));
			}
		} catch (Exception e) {
			e.printStackTrace();
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

	
	
	private <T> T runCallableFromJar(Class<?> loadedClass) throws Exception {
        try {
        	
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
		metadata.setType("PROCCESS");
		metadata.setParameter("classpath", "text");
		metadata.setParameter("className", "text");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "jar.png";
	}
	
}
