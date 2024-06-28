package main.cl.dagserver.infra.adapters.input.controllers;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import main.cl.dagserver.application.ports.input.GitHubWebHookUseCase;
import main.cl.dagserver.application.ports.input.StageApiUsecase;
import main.cl.dagserver.application.ports.input.XcomBrowserUsecase;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.ChannelPropsDTO;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.mockito.Mockito.when;

class DefaultControllerTest {

	private DefaultController controller; 
	
	@Mock
	private GitHubWebHookUseCase handler;
	
	@Mock
	private StageApiUsecase stage;
	
	@Mock
	private XcomBrowserUsecase xcom;
	
	@BeforeEach
    public void init() {
		handler = mock(GitHubWebHookUseCase.class);
		stage = mock(StageApiUsecase.class);
		xcom = mock(XcomBrowserUsecase.class);
		controller = new DefaultController(handler, stage,xcom);
		ReflectionTestUtils.setField(controller, "handler", handler);
		ReflectionTestUtils.setField(controller, "api", stage);
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
    	JSONObject ret = new JSONObject();
    	when(stage.executeTmp(anyInt(),anyString(),anyString(),anyString())).thenReturn(ret);
    	when(httpEntity.getBody()).thenReturn(obj.toString());
    	if(controller.stageApi(httpEntity, response).getStatusCode().is2xxSuccessful()) {
    		assertTrue(true);
    	}
    }
    @Test
    void githubEventTest() throws IOException, DomainException {
    	Model model = mock(Model.class);
    	HttpServletRequest request = mock(HttpServletRequest.class);
    	HttpServletResponse response = mock(HttpServletResponse.class);
    	String inputString = "{\"repository\":{\"html_url\":\"https://github.com/maximolira\"}}";
    	ChannelPropsDTO secretConfigured = new ChannelPropsDTO();
    	secretConfigured.setValue("test");
    	StringReader stringReader = new StringReader(inputString);
    	BufferedReader bf = new BufferedReader(stringReader); 
    	when(request.getReader()).thenReturn(bf);
    	when(request.getHeader(anyString())).thenReturn("sha1=584229ad7081d3270bae94efa0013ba1644b1e61");
    	when(handler.getChannelPropsFromRepo(anyString())).thenReturn(secretConfigured);
    	var rsp = controller.githubEvent(model, request, response);
    	if(rsp.getStatusCode().is2xxSuccessful()) {
    		assertTrue(true);
    	} else {
    		assertTrue(false);
    	}
    }
}