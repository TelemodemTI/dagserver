package main.infra.adapters.input.controllers;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import main.domain.annotations.Dag;
import main.domain.core.DagExecutable;
import main.infra.adapters.operators.DummyOperator;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodCall;


@Controller
public class DefaultController {
	
	
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(DefaultController.class);
	
	
	

	@RequestMapping(value="/version",method = RequestMethod.GET)
    public ResponseEntity<?> version(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		var rv = this.getClassDefinition();
		Class<?> dynamicType = rv.load(getClass().getClassLoader()).getLoaded();
		var bytes = rv.getBytes();
		var dagdinamyc = dynamicType.getDeclaredConstructor().newInstance();
		logger.debug(dagdinamyc);
		
		
		// Obtener los métodos de la clase
		Method[] methods = dynamicType.getDeclaredMethods();
		for (Method method : methods) {
			logger.debug("Método: " + method.getName());
		}

		// Obtener las anotaciones de la clase
		Annotation[] annotations = dynamicType.getDeclaredAnnotations();
		for (Annotation annotation : annotations) {
			logger.debug("Anotación: " + annotation.annotationType().getName());
		}
		

		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream fis = classloader.getResourceAsStream("basedag.zip");
        ZipInputStream zis = new ZipInputStream(fis);
		 	
        
        

        // Crea un archivo ZIP temporal en la ruta de destino
        FileOutputStream fos = new FileOutputStream("c:\\tmp\\generated.jar");
        ZipOutputStream zos = new ZipOutputStream(fos);

        // Copia las entradas existentes al nuevo archivo ZIP
        ZipEntry entrada;
        while ((entrada = zis.getNextEntry()) != null) {
            String nombreArchivo = entrada.getName();
            // Agrega la entrada al nuevo archivo ZIP
            zos.putNextEntry(new ZipEntry(nombreArchivo));

            // Copia los datos desde el archivo ZIP existente
            byte[] buffer = new byte[1024];
            int leido;
            while ((leido = zis.read(buffer)) > 0) {
                zos.write(buffer, 0, leido);
            }

            // Cierra la entrada actual
            zos.closeEntry();
        }

        // Crea la nueva carpeta dentro del nuevo archivo ZIP
        zos.putNextEntry(new ZipEntry("example_dag/main/"));
        zos.closeEntry();

        zos.putNextEntry(new ZipEntry("example_dag/main/ExampleDag2_DINAMIC.class"));
        zos.write(bytes);
        zos.closeEntry();
        
        
        // Cierra los flujos
        zis.close();
        zos.close();
        
        
        
        
		
		
		return new ResponseEntity<String>("dagserver is running!", HttpStatus.OK);
	}

	private Unloaded<DagExecutable> getClassDefinition() throws NoSuchMethodException, SecurityException {
		return new ByteBuddy().subclass(DagExecutable.class, ConstructorStrategy.Default.NO_CONSTRUCTORS)
		.name("example_dag.main.ExampleDag2_DINAMIC")
		
		.defineConstructor(Visibility.PUBLIC)


		.intercept(MethodCall.invoke(DagExecutable.class.getConstructor()).andThen(
	    		MethodCall.invoke(DagExecutable.class.getDeclaredMethod("addOperator", String.class, Class.class))
	            .with("Dummy", DummyOperator.class)
	    	))
                
		
		.annotateType(AnnotationDescription.Builder.ofType(Dag.class)
                .define("name", "testDinamic")
                .define("cronExpr", "0 0/1 * * * ?")
                .define("group", "grupo_1")
                .build())
		.make();
	}
}