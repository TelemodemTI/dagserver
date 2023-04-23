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
import main.domain.entities.User;
import main.domain.types.Agent;
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
	
	public void setLog(String dagname,String value,Map<String,Object> xcom) {
		var entry = new Log();
		entry.setDagname(dagname);
		entry.setExecDt(new Date());
		entry.setValue(value);
		JSONObject nuevo = new JSONObject(xcom);
		entry.setOutputxcom(nuevo.toString());
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
}
