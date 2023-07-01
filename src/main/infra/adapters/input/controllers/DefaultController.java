package main.infra.adapters.input.controllers;


import java.util.Iterator;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import main.infra.adapters.input.graphql.types.OperatorStage;



@Controller
public class DefaultController {
	
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(DefaultController.class);
	
	@RequestMapping(value="/version",method = RequestMethod.GET)
    public ResponseEntity<?> version(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {		
		
		return new ResponseEntity<String>("dagserver is running!", HttpStatus.OK);
	}

}