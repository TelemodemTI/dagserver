package main.cl.dagserver.infra.adapters.output.compiler;

import java.io.ByteArrayOutputStream;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileDeleteStrategy;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
@Log4j2
public class CompilerHandler implements CompilerOutputPort {

	private static final String GROUP = "group";
	private static final String PARAMS = "params";
	private static final String JARNAME = "jarname";
	private static final String VALUE = "value";
	private static final String NAME = "name";
	private static final String ONSTART = "onstart";
	private static final String ONEND = "onend";
	private static final String BASEOPPKG = "main.cl.dagserver";
	private static final String LISTENERLABEL = "listenerLabel";
	
	@Value("${param.folderpath}")
	private String pathfolder;
	
    private ApplicationEventPublisher eventPublisher;
	private CompilerOperatorBuilder builder;
	
	@Autowired
	public CompilerHandler(CompilerOperatorBuilder builder,ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
		this.builder = builder;
	}
	
	@Override
	public void createJar(String bin,Boolean force,Properties props) throws DomainException {
		try {
			ByteBuddyAgent.install();
			ClassReloadingStrategy.fromInstalledAgent().reset(DagExecutable.class);
			JSONObject def = new JSONObject(bin);
			String jarname = def.getString(JARNAME);
			validateOverwrtire(jarname,force);
			Map<String,byte[]> classBytes = new HashMap<>();
			for (int i = 0; i < def.getJSONArray("dags").length(); i++) {
				JSONObject dag = def.getJSONArray("dags").getJSONObject(i);
				validateDagOverwrite(dag.getString("name"));
				String crondef = dag.has("cron") ? dag.getString("cron") : ""  ;
				String loc = dag.getString("loc");
				String onstartdef = "";
				String onenddef = "";
				if(loc.equals("onStart")) {
					onstartdef = dag.getString("targetDag");  		
				} else if(loc.equals("onEnd")) {
					onenddef = dag.getString("targetDag");
				}
				String triggerv = dag.getString("trigger");
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
				dtomap.put(ONSTART, onstartdef);
				dtomap.put(ONEND, onenddef);
				dtomap.put(LISTENERLABEL, loc);
				var dagdef1 = this.getClassDefinition(dtomap ,dag.getJSONArray("boxes"));
				classBytes.put(classname, dagdef1.getBytes());
			}	
			this.packageJar(jarname, classBytes ,props, bin);
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
	private void validateOverwrtire(String jarname,Boolean force) throws DomainException {
		File file = new File(pathfolder+jarname);
        if (file.exists() && Boolean.FALSE.equals(force)) {
            throw new DomainException(new Exception("File exists"));
        }
	}
	public void validateDagOverwrite(String dagname) throws DomainException {
		File folder = new File(pathfolder);
	    File[] files = folder.listFiles((dir, name) -> name.endsWith(".jar"));
	    String className = "generated_dag/main/" + dagname + ".class";
	    for (File file : files) {
	        try (JarFile jarFile = new JarFile(file)) {
	            Enumeration<JarEntry> entries = jarFile.entries();
	            while (entries.hasMoreElements()) {
	                JarEntry entry = entries.nextElement();
	                if (entry.getName().equals(className)) {
	                    throw new DomainException(new Exception("dagname already exists"));
	                } 
	            }
	        } catch (IOException e) {
	            log.debug("Error reading jar file: " + file.getName());
	        }
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
		} else if(dtomap.get("type").equals("listener")) {
			varu = receiver.annotateType(AnnotationDescription.Builder.ofType(Dag.class)
	                .define(NAME, dtomap.get(NAME))
	                .define(dtomap.get(LISTENERLABEL), dtomap.get(dtomap.get(LISTENERLABEL).toLowerCase()))
	                .define(GROUP, dtomap.get(GROUP))
	                .build())
			.make(pool);	
		} else {
			varu = receiver.annotateType(AnnotationDescription.Builder.ofType(Dag.class)
	                .define(NAME, dtomap.get(NAME))
	                .define(GROUP, dtomap.get(GROUP))
	                .build())
			.make(pool);
		}
		return varu;
	}
	private void packageJar(String jarname,Map<String, byte[]> classbytes,Properties props, String bin) {
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
            var keys = classbytes.keySet();
            for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
				String classname = iterator.next();
				var strcom = classname.replace(".", "/");
	            zos.putNextEntry(new ZipEntry(this.getPackageDef(classname)));
	            zos.closeEntry();
	            zos.putNextEntry(new ZipEntry(strcom+".class"));
	            zos.write(classbytes.get(classname));
	            zos.closeEntry();
			}
            
            zos.putNextEntry(new ZipEntry("dagdef.json"));
            zos.write(bin.getBytes());
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
	
    public JSONArray operators() throws DomainException {
        try {
            // Scan for subclasses of OperatorStage
            try (ScanResult scanResult = new ClassGraph()
                    .acceptPackages(BASEOPPKG)
                    .scan()) {
                Set<Class<OperatorStage>> reflecteds = scanResult
                        .getSubclasses(OperatorStage.class.getName())
                        .loadClasses(OperatorStage.class)
                        .stream()
                        .collect(Collectors.toSet());
                Set<Class<? extends OperatorStage>> lista = reflecteds.stream().collect(Collectors.toSet());
                JSONArray arr = new JSONArray();
                for (Class<? extends OperatorStage> class1 : lista) {
                    if (class1.getCanonicalName().startsWith(BASEOPPKG)) {
                        OperatorStage op = class1.getDeclaredConstructor().newInstance();
                        var item = op.getMetadataOperator();
                        if (item != null) {
                        	arr.put(item);
                        }
                    }
                }
                return arr;
            }
        } catch (Exception e) {
        	e.printStackTrace();
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

	@Override
	public JSONObject reimport(String jarname) throws DomainException {
	    File jarFile = new File(pathfolder + jarname);
	    if (!jarFile.exists()) {
	        throw new DomainException(new Exception("Jar file not found"));
	    }

	    try (ZipInputStream zis = new ZipInputStream(new FileInputStream(jarFile))) {
	        ZipEntry entry;
	        while ((entry = zis.getNextEntry()) != null) {
	            if ("dagdef.json".equals(entry.getName())) {
	                ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                byte[] buffer = new byte[1024];
	                int len;
	                while ((len = zis.read(buffer)) > 0) {
	                    baos.write(buffer, 0, len);
	                }
	                String jsonStr = baos.toString(StandardCharsets.UTF_8);
	                return new JSONObject(jsonStr);
	            }
	        }
	        throw new DomainException(new Exception("dagdef.json not found in jar"));
	    } catch (IOException | JSONException e) {
	        throw new DomainException(e);
	    }
	}

}
