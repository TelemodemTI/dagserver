package main.cl.dagserver.infra.adapters.output.compiler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileDeleteStrategy;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import net.bytebuddy.agent.ByteBuddyAgent;
import main.cl.dagserver.application.ports.output.CompilerOutputPort;
import main.cl.dagserver.domain.annotations.Dag;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.DagExecutable;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
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
	private static final String JARNAME = "jarname";
	private static final String VALUE = "value";
	private static final String NAME = "name";
	private static final String BASEOPPKG = "main.cl.dagserver.infra.adapters.operators";
	
	@Value("${param.folderpath}")
	private String pathfolder;
	
	@Autowired
    private ApplicationEventPublisher eventPublisher;
	
	@Autowired
	CompilerOperatorBuilder builder;
	
	@Override
	public void createJar(String bin,Boolean force,Properties props) throws DomainException {
		try {
			ByteBuddyAgent.install();
			ClassReloadingStrategy.fromInstalledAgent().reset(DagExecutable.class);
			JSONObject def = new JSONObject(bin);
			String jarname = def.getString(JARNAME);
			validateOverwrtire(jarname,force);
			for (int i = 0; i < def.getJSONArray("dags").length(); i++) {
				JSONObject dag = def.getJSONArray("dags").getJSONObject(i);
				String crondef = dag.has("cron") ? dag.getString("cron") : ""  ;
				String onstartdef = dag.has("onstart") ? dag.getString("onstart") : "";  
				String onenddef = dag.has("onend") ? dag.getString("onend") : "" ;
				String triggerv = dag.getString("trigger");
				String loc = dag.getString("loc");
				String classname = dag.getString("class");
				String group = dag.getString(GROUP);
				validateParams(dag.getJSONArray("boxes"));
				Map<String,String> dtomap = new HashMap<>();
				dtomap.put(JARNAME, jarname);
				dtomap.put("classname", classname);
				dtomap.put(NAME, classname);
				dtomap.put("type", triggerv);
				dtomap.put(VALUE, crondef);
				dtomap.put(GROUP, group);
				dtomap.put("onstart", onstartdef);
				dtomap.put("onend", onenddef);
				dtomap.put("listenerLabel", loc);
				var dagdef1 = this.getClassDefinition(dtomap ,dag.getJSONArray("boxes"));
				this.packageJar(jarname, classname, dagdef1.getBytes(),props);

			}	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	private void validateParams(JSONArray jsonArray) throws DomainException {
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject item = jsonArray.getJSONObject(i);
			String type = item.getString("type");
			try {
				Class<?> clazz = Class.forName(type);	
		        // Obtener las anotaciones de la clase
				Operator annotation = clazz.getAnnotation(Operator.class);
		        this.validateHasParams(item, annotation);
			} catch (Exception e) {
				throw new DomainException(e);
			}
		}
	}
	
	private void validateHasParams(JSONObject item,Operator annotation) throws JSONException, DomainException {
		if(item.has(PARAMS)) {
	        for (int j = 0; j < item.getJSONArray(PARAMS).length(); j++) {
					JSONObject param = item.getJSONArray(PARAMS).getJSONObject(j);
					if(!searchValue(annotation.args(), param.getString("key")) && !searchValue(annotation.optionalv(), param.getString("key"))) {
						throw new DomainException(new Exception(param.getString("key")+" not found in args "+annotation.args()+ " with opts "+annotation.optionalv()));	
					}
			}
			if(item.getJSONArray(PARAMS).length() < annotation.args().length ) {
				throw new DomainException(new Exception(item.getJSONArray(PARAMS).toString()+"not enough params "+annotation.args()+ "with opts "+annotation.optionalv()));
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
	private void validateOverwrtire(String jarname, Boolean force) throws DomainException {
		File file = new File(pathfolder+jarname);
        if (file.exists() && Boolean.FALSE.equals(force)) {
            throw new DomainException(new Exception("File exists"));
        }
	}
	private Unloaded<DagExecutable> getClassDefinition(Map<String,String> dtomap,JSONArray boxes) throws DomainException {
		ClassFileLocator classFileLocator = new DirectoryClassFileLocator(pathfolder);
		var pool = new TypePool.Default(new CacheProvider.Simple(),classFileLocator,TypePool.Default.ReaderMode.FAST);
		
		var byteBuddy = new ByteBuddy();
		
		Builder<DagExecutable> builderbb = byteBuddy.subclass(DagExecutable.class, ConstructorStrategy.Default.NO_CONSTRUCTORS).name(dtomap.get("classname"));
		Initial<DagExecutable> inicial = builderbb.defineConstructor(Visibility.PUBLIC);
		
		ReceiverTypeDefinition<DagExecutable>  receiver = inicial.intercept(builder.build(dtomap.get(JARNAME),boxes));
		Unloaded<DagExecutable> varu = null;
		if(dtomap.get("type").equals("cron")) {
			varu = receiver.annotateType(AnnotationDescription.Builder.ofType(Dag.class)
	                .define(NAME, dtomap.get(NAME))
	                .define("cronExpr", dtomap.get(VALUE))
	                .define(GROUP, dtomap.get(GROUP))
	                .build())
			.make(pool);	
		} else {
			varu = receiver.annotateType(AnnotationDescription.Builder.ofType(Dag.class)
	                .define(NAME, dtomap.get(NAME))
	                .define(dtomap.get("listenerLabel"), dtomap.get(VALUE))
	                .define(GROUP, dtomap.get(GROUP))
	                .build())
			.make(pool);	
		}
		return varu;
	}
	private void packageJar(String jarname,String classname, byte[] bytes, Properties props) {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try(
        		InputStream fis = classloader.getResourceAsStream("basedag.zip");
        		FileOutputStream fos = new FileOutputStream(pathfolder+jarname);
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
            var strcom = classname.replace(".", "/");
            zos.putNextEntry(new ZipEntry(this.getPackageDef(classname)));
            zos.closeEntry();

            zos.putNextEntry(new ZipEntry(strcom+".class"));
            zos.write(bytes);
            zos.closeEntry();
            
            zos.putNextEntry(new ZipEntry("config.properties"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            props.store(baos, null);
            zos.write(baos.toByteArray());
            zos.closeEntry();
            
		} catch (Exception e) {
			eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "packageJar"));
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
        transformedString = transformedString.replace(".", "/") + "/";
        
        return transformedString;
    }
	@Override
	public JSONArray operators() throws DomainException {	
		try {
			Reflections reflections = new Reflections(BASEOPPKG, Scanners.SubTypes);			
			var lista = reflections.getSubTypesOf(OperatorStage.class).stream().collect(Collectors.toSet());
			JSONArray arr = new JSONArray();
			for (Iterator<Class<? extends OperatorStage>> iterator = lista.iterator(); iterator.hasNext();) {
				Class<? extends OperatorStage> class1 = iterator.next();
				if(class1.getCanonicalName().startsWith(BASEOPPKG)) {
					OperatorStage op = class1.getDeclaredConstructor().newInstance();
					var item = op.getMetadataOperator(); 
					if(item != null) {
						arr.put(item);	
					}	
				}
			}
			return arr;	
		} catch (Exception e) {
			throw new DomainException(e);
		}
		
	}
	@Override
	public void deleteJarfile(String jarname) throws DomainException {
		try {
			File remove = new File(pathfolder + jarname);
			FileDeleteStrategy.FORCE.delete(remove);
			Thread.sleep(2000);
		} catch (IOException e) {
			throw new DomainException(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
	
}
