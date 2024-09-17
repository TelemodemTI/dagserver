package main.cl.dagserver.infra.adapters.input.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
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
import main.cl.dagserver.application.ports.input.StageApiUsecase;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.input.controllers.types.ExecuteDagRequest;

@Controller
@CrossOrigin(origins = "*",methods={RequestMethod.GET,RequestMethod.POST})
public class DefaultController {
	
	@Value("${spring.allowed.file.extensions}")
	private String allowedExtensions;
	
	private StageApiUsecase api;
	
	 @Autowired
	  private ApplicationContext applicationContext;
	
	@Autowired
	public DefaultController(StageApiUsecase api) {
		this.api = api;
	}
	
	@GetMapping(path="/version/")
    public ResponseEntity<String> version(Model model,HttpServletRequest request,HttpServletResponse response) {				
		return new ResponseEntity<>("dagserver is running! v0.8.X", HttpStatus.OK);
	}
	
	@PostMapping(path = "/stageApi/",consumes = {"application/json"}, produces= {"application/json"})
	public ResponseEntity<String> stageApi(HttpEntity<String> httpEntity,HttpServletResponse response) throws DomainException {
		JSONObject body = new JSONObject(httpEntity.getBody());
		Integer uncompiled = body.getInt("uncompiled");
		String dagname = body.getString("dagname");
		String stepname = body.getString("stepname");
		String token = body.getString("token");
		JSONObject responsej = api.executeTmp(uncompiled,dagname,stepname,token);
		return new ResponseEntity<>(responsej.toString(), HttpStatus.OK);
	}
	
	
	@GetMapping(path={"/"})
    public RedirectView defaultGet(Model model,HttpServletRequest request,HttpServletResponse response) {		
		String path = request.getContextPath();
		RedirectView redirectView = new RedirectView();
        redirectView.setUrl(path+"/index.html");
        return redirectView;
	}
	
	@ExceptionHandler(NoHandlerFoundException.class)
    public RedirectView handle404(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getContextPath();
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(path + "/index.html");
        return redirectView;
    }
	
	@PostMapping(value = "/api/execute")
	public ResponseEntity<String> apiChannel(@RequestBody ExecuteDagRequest executeReq, @RequestHeader("Authorization") String authorizationHeader) throws IOException, DomainException{
		if(authorizationHeader.length() > 7) {
			String token = authorizationHeader.substring(7);
			api.executeDag(token,executeReq.getJarname(),executeReq.getDagname(),executeReq.getArgs());
			var status = new JSONObject();
			status.put("status", "OK");
			return new ResponseEntity<>(status.toString(), HttpStatus.OK);	
		} else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}
	
	
	
	@PostMapping(value = "/explorer/upload-file", consumes = {"multipart/form-data"})
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("upload-path") String uploadPath,@RequestParam("token") String token) throws DomainException {
	    try {
	    	String realFilename = file.getOriginalFilename();
	        Path tempFile = Files.createTempFile("uploaded-", this.sanitizeFilename(realFilename));
	        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);	        
	        api.uploadFile(tempFile,uploadPath,file.getOriginalFilename(),token);
	        JSONObject response = new JSONObject();
	        response.put("status", "ok");

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
	
	@GetMapping(value = "/explorer/download-file")
	public ResponseEntity<byte[]> downloadFile(@RequestParam("folder") String folderPath,@RequestParam("file") String filePath, @RequestParam("token") String token) throws DomainException {
	    try {
	    	
	    	
	        Path file = api.getFilePath(folderPath,this.sanitizeFilename(filePath), token);
	        if (!Files.exists(file) || !Files.isReadable(file)) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        
	        byte[] fileContent = Files.readAllBytes(file);
	        String contentType = Files.probeContentType(file);
	        return ResponseEntity.ok()
	                .header("Content-Disposition", "attachment; filename=\"" + file.getFileName().toString() + "\"")
	                .contentType(org.springframework.http.MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
	                .body(fileContent);
	    } catch (IOException e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	 @GetMapping(path = "/beans")
	 public String[] getAllBeans() {
		 String[] beanNames = applicationContext.getBeanDefinitionNames();
	     Arrays.sort(beanNames);
	     return beanNames;
	 }
	
	
}