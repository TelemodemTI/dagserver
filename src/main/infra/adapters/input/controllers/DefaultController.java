package main.infra.adapters.input.controllers;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
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
		return new ResponseEntity<>("dagserver is running! v0.1.20230818", HttpStatus.OK);
	}
	@GetMapping(path={"/","/cli"})
    public RedirectView defaultGet(Model model,HttpServletRequest request,HttpServletResponse response) {				
		RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/dagserver/cli/index.html");
        return redirectView;
	}
	@PostMapping(path = "/github-trigger")
	public ResponseEntity<String> githubEvent(Model model,HttpServletRequest request,HttpServletResponse response){
		logger.debug(request);
		var ndate = new Date();
		var sdf = new SimpleDateFormat("yyyy-MM-ddThh:mm:ss");
		return new ResponseEntity<>("event raised at "+sdf.format(ndate), HttpStatus.OK);
	}
}