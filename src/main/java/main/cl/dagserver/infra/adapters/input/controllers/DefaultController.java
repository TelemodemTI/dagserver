package main.cl.dagserver.infra.adapters.input.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
import fr.brouillard.oss.security.xhub.XHub;
import fr.brouillard.oss.security.xhub.XHub.XHubConverter;
import fr.brouillard.oss.security.xhub.XHub.XHubDigest;
import main.cl.dagserver.application.ports.input.GitHubWebHookUseCase;
import main.cl.dagserver.application.ports.input.StageApiUsecase;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.ChannelPropsDTO;
import main.cl.dagserver.infra.adapters.input.controllers.types.ExecuteDagRequest;

@Controller
@CrossOrigin(origins = "*",methods={RequestMethod.GET,RequestMethod.POST})
public class DefaultController {
	
	private GitHubWebHookUseCase handler;
	private StageApiUsecase api;
	
	@Autowired
	public DefaultController(GitHubWebHookUseCase handler,StageApiUsecase api) {
		this.handler = handler;
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
	
	@PostMapping(value = "/github-webhook")
	public ResponseEntity<String> githubEvent(Model model,HttpServletRequest request,HttpServletResponse response) throws IOException, DomainException{
		StringBuilder builder = new StringBuilder();
		String requestData = request.getReader().lines().collect(Collectors.joining());
		JSONObject payload = new JSONObject(requestData);	
		String repourl = payload.getJSONObject("repository").getString("html_url");
		String secret = request.getHeader("X-Hub-Signature");
		ChannelPropsDTO secretConfigured = handler.getChannelPropsFromRepo(repourl);
		builder.append("repo url::"+repourl+"\n");
		String hashedcomp = this.calculeHashSecret(secretConfigured.getValue(),requestData);
		var ndate = new Date();
		var sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		if(secret.equals(hashedcomp)) {
			handler.raiseEvent(repourl);
			builder.append("event raised at "+sdf.format(ndate)+ " for repo "+repourl);
		}
		return new ResponseEntity<>(builder.toString(), HttpStatus.OK);
	}
	
	@PostMapping(value = "/explorer/upload-file", consumes = {"multipart/form-data"})
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("upload-path") String uploadPath,@RequestParam("token") String token) throws DomainException {
	    try {
	        Path tempFile = Files.createTempFile("uploaded-", file.getOriginalFilename());
	        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);	        
	        api.uploadFile(tempFile,uploadPath,file.getOriginalFilename(),token);
	        JSONObject response = new JSONObject();
	        response.put("status", "ok");

	        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	    } catch (IOException e) {
	        return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	
	@GetMapping(value = "/explorer/download-file")
	public ResponseEntity<byte[]> downloadFile(@RequestParam("folder") String folderPath,@RequestParam("file") String filePath, @RequestParam("token") String token) throws DomainException {
	    try {
	        Path file = api.getFilePath(folderPath,filePath, token);
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
	
	
	private String calculeHashSecret(String xhubsignature,String requestData) {
		return XHub.generateHeaderXHubToken(XHubConverter.HEXA_LOWERCASE, XHubDigest.SHA1, xhubsignature, requestData.getBytes());
	}
	
}