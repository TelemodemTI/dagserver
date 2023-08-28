package main.infra.adapters.input.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class DefaultController {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DefaultController.class);
	
	
	@GetMapping(path="/version")
    public ResponseEntity<String> version(Model model,HttpServletRequest request,HttpServletResponse response) {				
		return new ResponseEntity<>("dagserver is running! v0.1.20230826", HttpStatus.OK);
	}
	@GetMapping(path={"/","/cli"})
    public RedirectView defaultGet(Model model,HttpServletRequest request,HttpServletResponse response) {				
		RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/dagserver/cli/index.html");
        return redirectView;
	}
	@PostMapping(value = "/github-webhook")
	public ResponseEntity<String> githubEvent(Model model,HttpServletRequest request,HttpServletResponse response) throws IOException{
		logger.debug(request);
		String requestData = request.getReader().lines().collect(Collectors.joining());
		JSONObject payload = new JSONObject(requestData);
		var configs = payload.getJSONObject("hook").getJSONObject("config");
		String repourl =configs.getString("url");
		String secret = configs.getString("secret");
		var ndate = new Date();
		var sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
		return new ResponseEntity<>("event raised at "+sdf.format(ndate)+ " for repo "+repourl+" and secret lenght "+secret.length(), HttpStatus.OK);
	}
}