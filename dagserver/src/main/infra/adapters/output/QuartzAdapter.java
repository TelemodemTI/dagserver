package main.infra.adapters.output;

import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import main.application.ports.output.QuartzOutputPort;
import main.infra.adapters.confs.QuartzConfig;

@Component
public class QuartzAdapter implements QuartzOutputPort {

	 @Autowired
	 QuartzConfig quartz;
	 
	 public List<Map<String,Object>> listScheduled() throws Exception {
		 return quartz.listScheduled();
	 }
}
