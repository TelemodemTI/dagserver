package main.domain.repositories;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import main.domain.entities.EventListener;
import main.domain.entities.Log;
import main.domain.entities.Metadata;
import main.domain.entities.PropertyParameter;
import main.domain.entities.ScheUncompiledDags;
import main.domain.entities.User;
import main.domain.enums.OperatorStatus;
import main.domain.types.Agent;
import main.domain.types.Uncompiled;
import main.infra.adapters.confs.DAO;


@Component
public class SchedulerRepository {

	@Autowired
	DAO dao;

	public void addEventListener(String name,String onstart,String onend,String groupname) {
		var event = new EventListener();
		event.setListenerName(name);
		event.setOnStart(onstart);
		event.setOnEnd(onend);
		event.setGroupName(groupname);
		dao.save(event);
	}
	
	public void removeListener(String name) {
		dao.execute("del from EventListener where listenerName = :name'",new HashMap<String, Object>(){
			private static final long serialVersionUID = 1L;
		{
			put("name", name);
		}});
	}
	
	public List<EventListener> listEventListeners(){
		return dao.read(EventListener.class, "select listener from EventListener as listener");
	}
	public List<EventListener> getEventListeners(String listenerName){
		return dao.read(EventListener.class, "select listener from EventListener as listener where listener.listenerName = '"+listenerName+"'");
	}
	
	public List<Log> getLogs(String dagname){
		return dao.read(Log.class, "select log from Log as log where log.dagname = '"+dagname+"' order by log.execDt desc");
	}
	
	public Log getLog(Integer logid){
		return dao.read(Log.class, "select log from Log as log where log.id = :logid",new HashMap<String,Object>(){
			private static final long serialVersionUID = 1L;
		{put("logid",logid);}}).get(0);
	}
	
	public void setLog(String dagname,String value,Map<String,Object> xcom, Map<String, OperatorStatus> status) {
		var entry = new Log();
		entry.setDagname(dagname);
		entry.setExecDt(new Date());
		entry.setValue(value);
		JSONObject nuevo = new JSONObject(xcom);
		entry.setOutputxcom(nuevo.toString());
		JSONObject statusObj = new JSONObject(status);
		entry.setStatus(statusObj.toString());
		dao.save(entry);
	}

	public void deleteLogsBy(Date rolldate) {
		dao.execute("delete from Log where execDt < :rolldate",new HashMap<String,Object>(){
			private static final long serialVersionUID = 1L;
		{put("rolldate",rolldate);}});
	}
	
	public List<User> findUser(String username) {
		List<User> founded = dao.read(User.class, "select user from User as user where user.username = '"+username+"'");
		return founded;
	}
	public List<PropertyParameter> getProperties(String groupname) throws Exception{
		List<PropertyParameter> founded;
		if(groupname != null) {
			founded = dao.read(PropertyParameter.class, "select props from PropertyParameter as props where props.group = '"+groupname+"'");
			if(founded.size() == 0) throw new Exception("DAG properties "+groupname+ " not found");
		} else {
			founded = dao.read(PropertyParameter.class, "select props from PropertyParameter as props");
		}
		return founded;
	}
	public Properties getPropertiesFromDb(String groupname) throws Exception {
		Properties nueva = new Properties();
		var founded = this.getProperties(groupname);
		for (Iterator<PropertyParameter> iterator = founded.iterator(); iterator.hasNext();) {
			PropertyParameter propertyParameter = iterator.next();
			nueva.setProperty(propertyParameter.getName(), propertyParameter.getValue());
		}
		return nueva;
	}

	public void setProperty(String name, String description, String value,String group) {
		PropertyParameter nuevo = new PropertyParameter();
		nuevo.setDescription(description);
		nuevo.setName(name);
		nuevo.setValue(value);
		nuevo.setGroup(group);
		dao.save(nuevo);
	}

	public void delProperty(String name,String group) {
		var founded = dao.read(PropertyParameter.class, "select props from PropertyParameter as props where props.group = '"+group+"' and props.name = '"+name+"'");
		for (Iterator<PropertyParameter> iterator = founded.iterator(); iterator.hasNext();) {
			PropertyParameter propertyParameter = iterator.next();
			dao.delete(propertyParameter);
		}
	}
	
	public void setMetadata(String hostname,String name) {
		List<Metadata> founded = dao.read(Metadata.class, "select meta from Metadata meta where meta.name = '"+name+"'");
		if(founded.isEmpty()) {
			Metadata info = new Metadata();
			info.setHost(hostname);
			info.setName(name);
			info.setLastUpdatedAt(new Date());
			dao.save(info);	
		} else {
			Metadata found = founded.get(0);
			found.setLastUpdatedAt(new Date());
			dao.save(found);
		}
	}

	public List<Agent> getAgents() {
		List<Metadata> list = dao.read(Metadata.class, "select meta from Metadata meta");
		List<Agent> res = new ArrayList<Agent>();
		for (Iterator<Metadata> iterator = list.iterator(); iterator.hasNext();) {
			Metadata metadata = iterator.next();
			Agent agent = new Agent();
			agent.setId(metadata.getId());
			agent.setHostname(metadata.getHost());
			agent.setName(metadata.getName());
			agent.setUpdatedOn(metadata.getLastUpdatedAt().getTime());
			res.add(agent);
		}
		return res;
	}

	
	public void insertIfNotExists(String jarname,String propertiesFile, Properties properties) {
	    List<PropertyParameter> existingProperties = dao.read(PropertyParameter.class, "SELECT props FROM PropertyParameter AS props WHERE props.group = '" + jarname+"."+propertiesFile + "'");
	    
	    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
	        String key = (String) entry.getKey();
	        String value = (String) entry.getValue();
	        
	        boolean found = false;
	        for (PropertyParameter existingProperty : existingProperties) {
	            if (existingProperty.getName().equals(key)) {
	                found = true;
	                break;
	            }
	        }
	        
	        if (!found) {
	            // La propiedad no existe, insertarla
	            PropertyParameter newProperty = new PropertyParameter();
	            newProperty.setGroup(jarname+"."+propertiesFile);
	            newProperty.setName(key);
	            newProperty.setValue(value);
	            newProperty.setDescription("imported by Scheduler.");
	            dao.save(newProperty);
	        }
	    }
	}

	public void addUncompiled(String string,String name, JSONObject json, Integer userid) {
		var list = dao.read(ScheUncompiledDags.class, "select uncom from ScheUncompiledDags uncom where uncom.name = '"+name+"'");
		if(list.isEmpty()) {
			ScheUncompiledDags existingProperties = new ScheUncompiledDags(); 
			existingProperties.setCreatedDt(new Date());
			existingProperties.setBin(json.toString());
			existingProperties.setName(name);
			existingProperties.setUserId(userid);
			dao.save(existingProperties);	
		} else {
			throw new RuntimeException("jarname already exists");
		}
	}

	public List<Uncompiled> getUncompileds(int parseInt) {
		var list = dao.read(ScheUncompiledDags.class, "select uncom from ScheUncompiledDags uncom where uncom.userId = "+parseInt);
		List<Uncompiled> rv = new ArrayList<>();
		for (Iterator<ScheUncompiledDags> iterator = list.iterator(); iterator.hasNext();) {
			ScheUncompiledDags scheUncompiledDags = iterator.next();
			Uncompiled item = new Uncompiled();
			item.setBin(scheUncompiledDags.getBin());
			item.setCreatedDt(scheUncompiledDags.getCreatedDt().getTime());
			item.setUncompiledId(scheUncompiledDags.getUncompiledId());
			rv.add(item);
		}
		return rv;
	}

	
	
}
