package main.infra.adapters.input.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class DefaultController {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DefaultController.class);
	
	
	@GetMapping(path="/version")
    public ResponseEntity<String> version(Model model,HttpServletRequest request,HttpServletResponse response) {				
		return new ResponseEntity<>("dagserver is running!", HttpStatus.OK);
	}
	@GetMapping(path={"/","/cli"})
    public RedirectView defaultGet(Model model,HttpServletRequest request,HttpServletResponse response) {				
		RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/dagserver/cli/index.html");
        return redirectView;
	}
}