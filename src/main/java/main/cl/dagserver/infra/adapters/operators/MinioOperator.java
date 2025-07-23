package main.cl.dagserver.infra.adapters.operators;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import com.nhl.dflib.DataFrame;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.UploadObjectArgs;
import io.minio.messages.Item;
import main.cl.dagserver.application.ports.input.InternalOperatorUseCase;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.DataFrameUtils;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.CredentialsDTO;
import main.cl.dagserver.infra.adapters.confs.ApplicationContextUtils;

@Operator(args={"host","credentials","commands","baseBucket"})
public class MinioOperator extends OperatorStage {
	private CredentialsDTO credentials = null;
	
	@SuppressWarnings("static-access")
	@Override
	public DataFrame call() throws DomainException {
		DataFrame df = null;
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		ApplicationContext appCtx = new ApplicationContextUtils().getApplicationContext();
		if(appCtx != null) {
			var handler =  appCtx.getBean("internalOperatorService", InternalOperatorUseCase.class);
			this.credentials = handler.getCredentials(this.args.getProperty("credentials"));
		}
		if(this.credentials == null) {
			throw new DomainException(new Exception("invalid credentials entry in keystore"));
		}
		try {
			MinioClient minioClient = MinioClient.builder().endpoint(this.getInputProperty("host").trim()).credentials(this.credentials.getUsername().trim(), this.credentials.getPassword().trim()).build();
			
			List<String> comds = Arrays.asList(this.args.getProperty("commands").split(";"));
			for (Iterator<String> iterator = comds.iterator(); iterator.hasNext();) {
				String[] cmd = iterator.next().split(" ");
				switch (cmd[0]) {
				case "list":
					df = this.list(minioClient, cmd[1]);
					break;
				case "upload":
					this.upload(minioClient, cmd[1], cmd[2]);
					df = DataFrameUtils.createStatusFrame("ok");
					break;
				case "download":
					this.download(minioClient, cmd[1], cmd[2]);
					df = DataFrameUtils.createStatusFrame("ok");
					break;
				 default:
					throw new DomainException(new Exception("command invalid"));
				}
			}
			return df;
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}

	public void download(MinioClient minioClient, String remoteFilePath, String localPath) throws IOException {
        try {
            Path localFilePath = Path.of(localPath);
            Files.createDirectories(localFilePath.getParent());
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(this.getInputProperty("baseBucket")) // Nombre del bucket
                            .object(remoteFilePath) // Ruta del archivo en MinIO
                            .build()
            );

            // Escribir el contenido del archivo en la ruta local
            try (var outputStream = Files.newOutputStream(localFilePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                byte[] buf = new byte[16384];
                int bytesRead;
                while ((bytesRead = stream.read(buf, 0, buf.length)) >= 0) {
                    outputStream.write(buf, 0, bytesRead);
                }
            }

            // Cerrar el InputStream
            stream.close();
        } catch (Exception e) {
            throw new IOException("Error al descargar el archivo", e);
        }
    }

	private void upload(MinioClient minioClient, String remoteFilePath, String fileInput) throws IOException {
		try {
			var path = Paths.get(fileInput);
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                        .bucket(this.getInputProperty("baseBucket"))
                        .object(path.getFileName().toString())
                        .filename(fileInput)
                        .build());
        } catch (Exception e) {
        	e.printStackTrace();
            throw new IOException("Error al cargar el archivo", e);
        }
	}

	private DataFrame list(MinioClient minioClient,String directory) throws IOException, Exception {
		Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(this.getInputProperty("baseBucket")).build());
		List<Map<String, Object>> content = new ArrayList<>();
		for (Result<Item> result : results) {
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("filename", result.get().objectName());
			StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(this.getInputProperty("baseBucket"))
                    .object(result.get().objectName())
                    .build());
			map.put("size", stat.size());
			content.add(map);
        }
		return DataFrameUtils.buildDataFrameFromMap(content);	
	}
	
	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.MinioOperator");
		metadata.setType("REMOTE");
		metadata.setParameter("host", "text");
		metadata.setParameter("credentials", "credentials");
		metadata.setParameter("baseBucket", "text");
		metadata.setParameter("commands", "remote");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "minio.png";
	}
}
