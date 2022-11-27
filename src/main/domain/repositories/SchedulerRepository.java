package main.domain.repositories;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import main.domain.entities.EventListener;
import main.domain.entities.Log;
import main.domain.entities.User;
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
		dao.execute("del from EventListener where listenerName = :name'",new HashMap<String, Object>(){{
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
		return dao.read(Log.class, "select log from Log as log where log.id = :logid",new HashMap<String,Object>(){{put("logid",logid);}}).get(0);
	}
	
	public void setLog(String dagname,String value) {
		var entry = new Log();
		entry.setDagname(dagname);
		entry.setExecDt(new Date());
		entry.setValue(value);
		dao.save(entry);
	}

	public void deleteLogsBy(Date rolldate) {
		dao.execute("delete from Log where execDt < :rolldate",new HashMap<String,Object>(){{put("rolldate",rolldate);}});
	}
	
	public List<User> findUser(String username) {
		List<User> founded = dao.read(User.class, "select user from User as user where user.username = '"+username+"'");
		return founded;
	}
}
