package main.infra.adapters.input.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DefaultController {
	
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(DefaultController.class);
	
	@RequestMapping(value="/version",method = RequestMethod.GET)
    public ResponseEntity<?> version(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {		
		
		return new ResponseEntity<String>("dagserver is running!", HttpStatus.OK);
	}

}