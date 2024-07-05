package main.cl.dagserver.infra.adapters.operators;

import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.json.JSONObject;

import com.nhl.dflib.DataFrame;

import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;


@Operator(args={"path"})
public class PathDirOperator extends OperatorStage {

	@Override
	public DataFrame call() throws DomainException {		
		log.debug(this.getClass() + " init " + this.name);
	    log.debug("args");
	    log.debug(this.args);
	    List<Map<String, Object>> returnv = new ArrayList<>();
	    String path = this.args.getProperty("path");
	    try {
	        Path dirPath = Paths.get(path);
	        
	        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
	            throw new DomainException(new Exception("invalid path"));
	        }
	        DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath);
	        for (Path filePath : stream) {
	            Map<String, Object> fileInfo = new HashMap<>();
	            BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);   
	            fileInfo.put("filename", filePath.getFileName().toString());
	            fileInfo.put("size", attrs.size());
	            fileInfo.put("isReadable", Files.isReadable(filePath));
	            fileInfo.put("isWritable", Files.isWritable(filePath));
	            fileInfo.put("isExecutable", Files.isExecutable(filePath));
	            fileInfo.put("isFolder", Files.isDirectory(filePath));
	            fileInfo.put("isHidden", Files.isHidden(filePath));
	            fileInfo.put("isSymbolicLink", Files.isSymbolicLink(filePath));
	            returnv.add(fileInfo);
	        }
	    } catch (IOException e) {
	        throw new DomainException(e);
	    }
	    log.debug(this.getClass() + " end " + this.name);
	    return OperatorStage.buildDataFrame(returnv);
	}
	

	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.PathDirOperator");
		metadata.setParameter("path", "text");
		metadata.setType("PROCCESS");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "pathdir.png";
	}
}
