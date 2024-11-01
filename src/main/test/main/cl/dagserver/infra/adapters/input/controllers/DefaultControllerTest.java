package main.cl.dagserver.infra.adapters.input.controllers;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.ui.Model;
import main.cl.dagserver.application.ports.input.StageApiUsecase;
import main.cl.dagserver.domain.exceptions.DomainException;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static org.mockito.Mockito.when;

class DefaultControllerTest {

	private DefaultController controller; 
	
	@Mock
	private StageApiUsecase stage;
	
	@Mock
	private ApplicationContext applicationContext;
	
	@Mock
	private ResourceLoader resourceLoader;
	
	
	@BeforeEach
    public void init() {
		stage = mock(StageApiUsecase.class);
		applicationContext = mock(ApplicationContext.class);
		resourceLoader = mock(ResourceLoader.class);
		controller = new DefaultController(stage, applicationContext,resourceLoader);
	}
	
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
    
    @Test
    void defaultGetTest() {
    	Model model = mock(Model.class);
    	HttpServletRequest request = mock(HttpServletRequest.class);
    	HttpServletResponse response = mock(HttpServletResponse.class);
    	try {
    		if(controller.defaultGet(model, request, response).isRedirectView()) {
        		assertTrue(true); 	
        	} else {
        		assertTrue(false);
        	}	
		} catch (Exception e) {
			assertTrue(false,e.getMessage());
		}
    }
    @SuppressWarnings("unchecked")
	@Test
    void stageApiTest() throws JSONException, DomainException {
    	HttpEntity<String> httpEntity = mock(HttpEntity.class);
    	HttpServletResponse response = mock(HttpServletResponse.class);
    	JSONObject obj = new JSONObject();
    	obj.put("dagname", "test");
    	obj.put("stepname", "step");
    	obj.put("uncompiled", 0);
    	obj.put("token", "token");
    	obj.put("args", "{}");
    	JSONObject ret = new JSONObject();
    	when(stage.executeTmp(anyInt(),anyString(),anyString(),anyString(),anyString())).thenReturn(ret);
    	when(httpEntity.getBody()).thenReturn(obj.toString());
    	if(controller.stageApi(httpEntity, response).getStatusCode().is2xxSuccessful()) {
    		assertTrue(true);
    	}
    }
    
}