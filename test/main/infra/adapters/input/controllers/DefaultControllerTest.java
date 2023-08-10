package main.infra.adapters.input.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class DefaultControllerTest {

	private DefaultController controller = new DefaultController(); 
	
    @Test
    void versionTest() {
    	Model model = mock(Model.class);
    	HttpServletRequest request = mock(HttpServletRequest.class);
    	HttpServletResponse response = mock(HttpServletResponse.class);
    	try {
    		if(controller.version(model,request,response).getStatusCode().is2xxSuccessful()) {
        		assertTrue(true); 	
        	} else {
        		assertTrue(false);
        	}	
		} catch (Exception e) {
			assertTrue(false,e.getMessage());
		}
    }
}