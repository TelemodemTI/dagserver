package main.cl.dagserver.infra.adapters.input.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.view.RedirectView;
import fr.brouillard.oss.security.xhub.XHub;
import fr.brouillard.oss.security.xhub.XHub.XHubConverter;
import fr.brouillard.oss.security.xhub.XHub.XHubDigest;
import main.cl.dagserver.application.ports.input.GitHubWebHookUseCase;
import main.cl.dagserver.application.ports.input.StageApiUsecase;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.ChannelPropsDTO;


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
	
	@GetMapping(path="/version")
    public ResponseEntity<String> version(Model model,HttpServletRequest request,HttpServletResponse response) {				
		return new ResponseEntity<>("dagserver is running! v0.7.X", HttpStatus.OK);
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
	
	private String calculeHashSecret(String xhubsignature,String requestData) {
		return XHub.generateHeaderXHubToken(XHubConverter.HEXA_LOWERCASE, XHubDigest.SHA1, xhubsignature, requestData.getBytes());
	}
	
}