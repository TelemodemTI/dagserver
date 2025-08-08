package main.cl.dagserver.infra.adapters.operators;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
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


@Operator(args={"groupId","artifactId","packaging","version"})
public class AetherOperator extends OperatorStage {

	@SuppressWarnings("static-access")
	@Override
	public DataFrame call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		log.debug(this.getClass()+" end "+this.name);
		ApplicationContext appCtx = new ApplicationContextUtils().getApplicationContext();
		if(appCtx==null) {
			throw new DomainException(new Exception("Application context is null, cannot proceed with Aether operation."));
		}
		var handler =  appCtx.getBean("internalOperatorService", InternalOperatorUseCase.class);
		try {
	        RepositorySystem repositorySystem = getRepositorySystem();
	        RepositorySystemSession repositorySystemSession = getRepositorySystemSession(repositorySystem);
	        String groupId = this.getInputProperty("groupId");
	        String artifactId = this.getInputProperty("artifactId");
	        String packaging = this.getInputProperty("packaging");
	        String version = this.getInputProperty("version");
	    	Artifact artifact = new DefaultArtifact(groupId, artifactId, packaging, version);
	    	ArtifactRequest artifactRequest = new ArtifactRequest();
	    	artifactRequest.setArtifact(artifact);
	    	artifactRequest.setRepositories(getRepositories(repositorySystem, repositorySystemSession));
	    	ArtifactResult artifactResult = repositorySystem.resolveArtifact(repositorySystemSession, artifactRequest);
	    	artifact = artifactResult.getArtifact();
	    	byte[] content = Files.readAllBytes(artifact.getFile().toPath());
			String jarname = artifact.getFile().getName();
			Path destination = handler.getPath("/"+jarname);
			Files.write(destination, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

		} catch (Exception e) {
			throw new DomainException(e);
		}
		return DataFrameUtils.createStatusFrame("ok");
	}
	

	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.AetherOperator");
		metadata.setParameter("groupId", "text");
		metadata.setParameter("artifactId", "text");
		metadata.setParameter("packaging", "text");
		metadata.setParameter("version", "text");
		metadata.setType("PROCCESS");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "maven.png";
	}
	
	
	public static RepositorySystem getRepositorySystem() {
	    DefaultServiceLocator serviceLocator = MavenRepositorySystemUtils.newServiceLocator();
	    serviceLocator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
	    serviceLocator.addService(TransporterFactory.class, FileTransporterFactory.class);
	    serviceLocator.addService(TransporterFactory.class, HttpTransporterFactory.class);
	    serviceLocator.setErrorHandler(new DefaultServiceLocator.ErrorHandler() {
	      @Override
	      public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
	        System.err.printf("error creating service: %s\n", exception.getMessage());
	        exception.printStackTrace();
	      }
	    });
	    return serviceLocator.getService(RepositorySystem.class);
	  }

	  public static DefaultRepositorySystemSession getRepositorySystemSession(RepositorySystem system) throws IOException {
	    DefaultRepositorySystemSession repositorySystemSession = MavenRepositorySystemUtils.newSession();
	    Path tempDir = Files.createTempDirectory("aether-tmporal-dir");
		LocalRepository localRepository = new LocalRepository(tempDir.toAbsolutePath().toString());
	    repositorySystemSession.setLocalRepositoryManager(system.newLocalRepositoryManager(repositorySystemSession, localRepository));
	    return repositorySystemSession;
	  }

	  public static List<RemoteRepository> getRepositories(RepositorySystem system,RepositorySystemSession session) {
	    return Arrays.asList(getCentralMavenRepository());
	  }

	  private static RemoteRepository getCentralMavenRepository() {
	    return new RemoteRepository.Builder("maven-central", "default", "https://repo1.maven.org/maven2/").build();
	  }

}
