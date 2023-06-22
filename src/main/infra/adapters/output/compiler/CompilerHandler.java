package main.infra.adapters.output.compiler;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.hibernate.type.AnyType.ObjectTypeCacheEntry;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder.DescriptionStrategy;
import main.application.ports.output.CompilerOutputPort;
import main.domain.annotations.Dag;
import main.domain.core.DagExecutable;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.TypeCache;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ParameterDefinition.Initial;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.Implementation.Composable;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.pool.TypePool.CacheProvider;

@Component
@ImportResource("classpath:properties-config.xml")
public class CompilerHandler implements CompilerOutputPort {

	@Value("${param.folderpath}")
	private String pathfolder;
	
	@Autowired
	CompilerOperatorBuilder builder;
	
	private static Logger log = Logger.getLogger(CompilerHandler.class);
	
	@Override
	public void createJar(String bin) throws Exception {
		ByteBuddyAgent.install();
		ClassReloadingStrategy.fromInstalledAgent().reset(DagExecutable.class);
		JSONObject def = new JSONObject(bin);
		String jarname = def.getString("jarname");
		for (int i = 0; i < def.getJSONArray("dags").length(); i++) {
			JSONObject dag = def.getJSONArray("dags").getJSONObject(i);
			String crondef = dag.getString("cron");
			String classname = dag.getString("class");
			String group = dag.getString("group");
			var dagdef1 = this.getClassDefinition(classname, classname, crondef, group, dag.getJSONArray("boxes"));
						
			this.packageJar(jarname, classname, dagdef1.getBytes());
			log.error(dagdef1);
		}
	}
	private Unloaded<DagExecutable> getClassDefinition(String classname, String name, String cron, String group, JSONArray boxes) throws Exception {
		log.error(boxes);
		
		
		ClassFileLocator classFileLocator = new DirectoryClassFileLocator(pathfolder+"/testingbb/");
		var pool = new TypePool.Default(new CacheProvider.Simple(),classFileLocator,TypePool.Default.ReaderMode.FAST);
		
		
		var byteBuddy = new ByteBuddy();
		
		Builder<DagExecutable> builderbb = byteBuddy.subclass(DagExecutable.class, ConstructorStrategy.Default.   NO_CONSTRUCTORS).name(classname);
		Initial<DagExecutable> inicial = builderbb.defineConstructor(Visibility.PUBLIC);
		
		ReceiverTypeDefinition<DagExecutable>  receiver = inicial.intercept(builder.build(boxes));
		Unloaded<DagExecutable> varu = receiver.annotateType(AnnotationDescription.Builder.ofType(Dag.class)
                .define("name", name)
                .define("cronExpr", cron)
                .define("group", group)
                .build())
		.make(pool);
		
		
		
		
		return varu;
	}
	private void packageJar(String jarname,String classname, byte[] bytes) throws Exception {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream fis = classloader.getResourceAsStream("basedag.zip");
        ZipInputStream zis = new ZipInputStream(fis);
        
        // Crea un archivo ZIP temporal en la ruta de destino
        FileOutputStream fos = new FileOutputStream(pathfolder+jarname);
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

        var strcom = classname.replaceAll("\\.", "/");
        
        
        // Crea la nueva carpeta dentro del nuevo archivo ZIP
        zos.putNextEntry(new ZipEntry(this.getPackageDef(classname)));
        zos.closeEntry();

        zos.putNextEntry(new ZipEntry(strcom+".class"));
        zos.write(bytes);
        zos.closeEntry();
        
        
        // Cierra los flujos
        zis.close();
        zos.close();
        

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
}
