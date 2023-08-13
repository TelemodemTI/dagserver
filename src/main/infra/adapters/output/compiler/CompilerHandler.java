package main.infra.adapters.output.compiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import net.bytebuddy.agent.ByteBuddyAgent;
import main.application.ports.output.CompilerOutputPort;
import main.domain.annotations.Dag;
import main.domain.annotations.Operator;
import main.domain.core.DagExecutable;
import main.domain.exceptions.DomainException;
import main.infra.adapters.input.graphql.types.OperatorStage;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ParameterDefinition.Initial;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.pool.TypePool.CacheProvider;

@Component
@ImportResource("classpath:properties-config.xml")
public class CompilerHandler implements CompilerOutputPort {

	private static final String GROUP = "group";
	private static final String PARAMS = "params";
	
	@Value("${param.folderpath}")
	private String pathfolder;
	
	@Autowired
	CompilerOperatorBuilder builder;
	
	private static Logger log = Logger.getLogger(CompilerHandler.class);
	
	@Override
	public void createJar(String bin,Boolean force) throws DomainException {
		try {
			ByteBuddyAgent.install();
			ClassReloadingStrategy.fromInstalledAgent().reset(DagExecutable.class);
			JSONObject def = new JSONObject(bin);
			String jarname = def.getString("jarname");
			validateOverwrtire(jarname,force);
			for (int i = 0; i < def.getJSONArray("dags").length(); i++) {
				JSONObject dag = def.getJSONArray("dags").getJSONObject(i);
				String crondef = dag.getString("cron");
				String triggerv = dag.getString("trigger");
				String loc = dag.getString("loc");
				String classname = dag.getString("class");
				String group = dag.getString(GROUP);
				validateParams(dag.getJSONArray("boxes"));
				var dagdef1 = this.getClassDefinition(jarname,classname, classname, triggerv ,crondef, group,loc ,dag.getJSONArray("boxes"));
				this.packageJar(jarname, classname, dagdef1.getBytes());

			}	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	private void validateParams(JSONArray jsonArray) throws Exception {
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject item = jsonArray.getJSONObject(i);
			String type = item.getString("type");
			Class<?> clazz = Class.forName(type);
	        // Obtener las anotaciones de la clase
			Operator annotation = clazz.getAnnotation(Operator.class);
	        if(item.has(PARAMS)) {
	        for (int j = 0; j < item.getJSONArray(PARAMS).length(); j++) {
					JSONObject param = item.getJSONArray(PARAMS).getJSONObject(j);
					if(!searchValue(annotation.args(), param.getString("key")) && !searchValue(annotation.optionalv(), param.getString("key"))) {
						throw new IOException(param.getString("key")+" not found in args "+annotation.args()+ " with opts "+annotation.optionalv());	
					}
			}
			if(item.getJSONArray(PARAMS).length() < annotation.args().length ) {
				throw new IOException(item.getJSONArray(PARAMS).toString()+"not enough params "+annotation.args()+ "with opts "+annotation.optionalv());
			}	
	        }
		}
	}
	private boolean searchValue(String[] array, String value) {
        for (String element : array) {
            if (element.equals(value)) {
                return true;
            }
        }
        return false;
    }
	private void validateOverwrtire(String jarname, Boolean force) throws Exception {
		File file = new File(pathfolder+jarname);
        if (file.exists()) {
            if (!force) {
                throw new IOException("File exists");
            } 
        }
	}
	private Unloaded<DagExecutable> getClassDefinition(String jarname, String classname, String name, String type, String value, String group, String listenerLabel,JSONArray boxes) throws Exception {
		log.error(boxes);
		
		
		ClassFileLocator classFileLocator = new DirectoryClassFileLocator(pathfolder+"/testingbb/");
		var pool = new TypePool.Default(new CacheProvider.Simple(),classFileLocator,TypePool.Default.ReaderMode.FAST);
		
		var byteBuddy = new ByteBuddy();
		
		Builder<DagExecutable> builderbb = byteBuddy.subclass(DagExecutable.class, ConstructorStrategy.Default.   NO_CONSTRUCTORS).name(classname);
		Initial<DagExecutable> inicial = builderbb.defineConstructor(Visibility.PUBLIC);
		
		ReceiverTypeDefinition<DagExecutable>  receiver = inicial.intercept(builder.build(jarname,boxes));
		Unloaded<DagExecutable> varu = null;
		if(type.equals("cron")) {
			varu = receiver.annotateType(AnnotationDescription.Builder.ofType(Dag.class)
	                .define("name", name)
	                .define("cronExpr", value)
	                .define(GROUP, group)
	                .build())
			.make(pool);	
		} else {
			varu = receiver.annotateType(AnnotationDescription.Builder.ofType(Dag.class)
	                .define("name", name)
	                .define(listenerLabel, value)
	                .define(GROUP, group)
	                .build())
			.make(pool);	
		}
		return varu;
	}
	private void packageJar(String jarname,String classname, byte[] bytes) throws Exception {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream fis = classloader.getResourceAsStream("basedag.zip");
        FileOutputStream fos = new FileOutputStream(pathfolder+jarname);
        try(
        		ZipOutputStream zos = new ZipOutputStream(fos);
        		ZipInputStream zis = new ZipInputStream(fis);
        		) {
            ZipEntry entrada;
            while ((entrada = zis.getNextEntry()) != null) {
                String nombreArchivo = entrada.getName();
                zos.putNextEntry(new ZipEntry(nombreArchivo));
                byte[] buffer = new byte[1024];
                int leido;
                while ((leido = zis.read(buffer)) > 0) {
                    zos.write(buffer, 0, leido);
                }
                zos.closeEntry();
            }
            var strcom = classname.replaceAll("\\.", "/");
            zos.putNextEntry(new ZipEntry(this.getPackageDef(classname)));
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry(strcom+".class"));
            zos.write(bytes);
            zos.closeEntry();
            
		} catch (Exception e) {
			log.error(e);
		}
	}
    public String getPackageDef(String input) {
        String[] segments = input.split("\\.");
        
        if (segments.length < 2) {
            throw new IllegalArgumentException("classname format error.");
        }
        
        String firstSegment = segments[0];
        String secondSegment = segments[1];
        
        String transformedString = firstSegment.toLowerCase() + "." + secondSegment.toLowerCase();
        transformedString = transformedString.replaceAll("\\.", "/") + "/";
        
        return transformedString;
    }
	@Override
	public JSONArray operators() throws DomainException {	
		try {
			Reflections reflections = new Reflections("main.infra.adapters.operators", new SubTypesScanner(false));
			var lista = reflections.getSubTypesOf(OperatorStage.class).stream().collect(Collectors.toSet());
			JSONArray arr = new JSONArray();
			for (Iterator<Class<? extends OperatorStage>> iterator = lista.iterator(); iterator.hasNext();) {
				Class<? extends OperatorStage> class1 = iterator.next();
				OperatorStage op = class1.getDeclaredConstructor().newInstance();
				var item = op.getMetadataOperator(); 
				if(item != null) {
					arr.put(item);	
				}
			}
			return arr;	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
		
	}
}
