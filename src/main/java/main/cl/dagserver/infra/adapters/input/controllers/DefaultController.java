package main.cl.dagserver.infra.adapters.input.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.view.RedirectView;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.nhl.dflib.DataFrame;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import main.cl.dagserver.application.ports.input.StageApiUsecase;
import main.cl.dagserver.domain.core.DataFrameUtils;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.input.controllers.types.DagResultResponse;
import main.cl.dagserver.infra.adapters.input.controllers.types.ExecuteDagRequest;

@Controller
@CrossOrigin(origins = "*",methods={RequestMethod.GET,RequestMethod.POST})
public class DefaultController {
	private static final String CONTENTDISPOSITION = "Content-Disposition";
	private static final String STATUS = "status";
	
	@Value("${spring.allowed.file.extensions}")
	private String allowedExtensions;
	
	@Value("${param.dagserver.version}")
	private String version;

	private StageApiUsecase api;
	private ApplicationContext applicationContext;
	

	
	@Autowired
	public DefaultController(StageApiUsecase api,ApplicationContext applicationContext) {
		this.api = api;
		this.applicationContext = applicationContext;
	}
	
	@GetMapping(path="/version/")
    public ResponseEntity<String> version(Model model,HttpServletRequest request,HttpServletResponse response) {				
		return new ResponseEntity<>("dagserver is running! v"+this.version, HttpStatus.OK);
	}
	
	@GetMapping(path = "/beans")
	public ResponseEntity<String> getAllBeans() {
		String[] beanNames = applicationContext.getBeanDefinitionNames();
	    JSONArray arr = new JSONArray();
	    for (int i = 0; i < beanNames.length; i++) {
	    	arr.put(beanNames[i]);
		}
	    return new ResponseEntity<>(arr.toString(),HttpStatus.OK);
	}
	
	@GetMapping(path={"/"})
    public RedirectView defaultGet(Model model,HttpServletRequest request,HttpServletResponse response) {		
		String path = request.getContextPath();
		RedirectView redirectView = new RedirectView();
        redirectView.setUrl(path+"/index.html");
        return redirectView;
	}
	
	@PostMapping(path = "/stageApi/",consumes = {"application/json"}, produces= {"application/json"})
	public ResponseEntity<String> stageApi(HttpEntity<String> httpEntity,HttpServletResponse response) throws DomainException {
		JSONObject body = new JSONObject(httpEntity.getBody());
		Integer uncompiled = body.getInt("uncompiled");
		String dagname = body.getString("dagname");
		String stepname = body.getString("stepname");
		String token = body.getString("token");
		String args = body.getString("args");
		JSONObject responsej = api.executeTmp(uncompiled,dagname,stepname,token,args);
		return new ResponseEntity<>(responsej.toString(), HttpStatus.OK);
	}
	
	
	
	
	@ExceptionHandler(NoHandlerFoundException.class)
    public RedirectView handle404(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getContextPath();
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(path + "/index.html");
        return redirectView;
    }
	
	@Operation(
        summary = "Execute a DAG",
        description = "Execute a DAG"
    )
	@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = DagResultResponse.class)
		)),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
	@PostMapping(value = "/api/execute")
	public ResponseEntity<String> apiChannel(
			@Parameter(description = "Request body") @RequestBody ExecuteDagRequest executeReq, 
			@Parameter(description = "Authorization header") @RequestHeader("Authorization") String authorizationHeader,
			@Parameter(description = "WFR header") @RequestHeader(value = "WFR", required = false) Boolean wfr) throws IOException, DomainException{
		if(authorizationHeader.length() > 7) {
			String token = authorizationHeader.substring(7);
			var xcom = api.executeDag(token,executeReq.getJarname(),executeReq.getDagname(),executeReq.getArgs(),wfr);
			var status = new JSONObject();
			var xcomJson = this.serializeXcom(xcom);
			status.put(STATUS, "OK");
			status.put("xcom", xcomJson);
			return new ResponseEntity<>(status.toString(), HttpStatus.OK);	
		} else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}
	private JSONObject serializeXcom(Map<String, DataFrame> xcom) {
		JSONObject wrapper = new JSONObject();
		var keys = xcom.keySet();
		for (Iterator<String> iterator2 = keys.iterator(); iterator2.hasNext();) {
			 var string = iterator2.next();
			 wrapper.put(string, DataFrameUtils.dataFrameToJson(xcom.get(string)));
		}
		return wrapper;
	}
	
	
	@PostMapping(value = "/explorer/upload-file", consumes = {"multipart/form-data"})
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("upload-path") String uploadPath,@RequestParam("token") String token) throws DomainException {
	    try {
	    	String realFilename = file.getOriginalFilename();
	        Path tempFile = Files.createTempFile("uploaded-", this.sanitizeFilename(realFilename));
	        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);	        
	        api.uploadFile(tempFile,uploadPath,file.getOriginalFilename(),token);
	        JSONObject response = new JSONObject();
	        response.put(STATUS, "ok");

	        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	    } catch (IOException e) {
	        return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	private String sanitizeFilename(String filename) throws DomainException {
	    String[] exts = this.allowedExtensions.split(","); 
	    String fileExtension = FilenameUtils.getExtension(filename).toLowerCase();
	    
		if(
	    		filename != null && 
	    		!filename.contains("..") && 
	    		!filename.startsWith(".") &&
	    		Arrays.asList(exts).contains(fileExtension)
	    		) {
			return filename;
		} else {
			throw new DomainException(new Exception("invalid file"));
		}
	}
	private String sanitizePath(String path) {
	    return path.replaceAll("[^a-zA-Z0-9/._-]", "_");
	}
	
	
	@GetMapping(value = "/explorer/download-file")
	public ResponseEntity<byte[]> downloadFile(@RequestParam("folder") String folderPath,@RequestParam("file") String filePath, @RequestParam("token") String token) throws DomainException {
	    try {
	        Path file1 = api.getFilePath(sanitizePath(folderPath),this.sanitizeFilename(filePath), token);         
	        byte[] fileContent = Files.readAllBytes(file1);
	        String contentType = Files.probeContentType(file1);
	        return ResponseEntity.ok()
	                .header(CONTENTDISPOSITION, "attachment; filename=\"" + file1.getFileName().toString() + "\"")
	                .contentType(org.springframework.http.MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
	                .body(fileContent);
	    } catch (IOException e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	
	 @GetMapping("/download-keystore")
	 public ResponseEntity<byte[]> downloadKeystore(@RequestParam("token") String token) throws DomainException {
		 try {
			 File keystore = this.api.exportKeystore(token);
			 byte[] fileContent = Files.readAllBytes(keystore.toPath());
		        String contentType = Files.probeContentType(keystore.toPath());
		        return ResponseEntity.ok()
		                .header(CONTENTDISPOSITION, "attachment; filename=\"" + keystore.toPath().getFileName().toString() + "\"")
		                .contentType(org.springframework.http.MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
		                .body(fileContent);	 
		 } catch (IOException e) {
		        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		 }
	 }
	 
	@PostMapping(value = "/upload-keystore", consumes = {"multipart/form-data"})
	public ResponseEntity<String> uploadKeystore(@RequestParam("file") MultipartFile file, @RequestParam("token") String token) throws DomainException {
		    try {
		    	String realFilename = file.getOriginalFilename();
		        Path tempFile = Files.createTempFile("uploaded-", this.sanitizeFilename(realFilename));
		        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);	        
		        api.uploadKeystore(tempFile,file.getOriginalFilename(),token);
		        JSONObject response = new JSONObject();
		        response.put(STATUS, "ok");
		        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
		    } catch (IOException e) {
		        return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		    }
	}
}