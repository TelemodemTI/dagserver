package main.infra.adapters.output.repositories;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

import main.application.ports.output.SchedulerRepositoryOutputPort;
import main.domain.annotations.Operator;
import main.domain.enums.OperatorStatus;
import main.domain.model.AgentDTO;
import main.domain.model.EventListenerDTO;
import main.domain.model.LogDTO;
import main.domain.model.PropertyParameterDTO;
import main.domain.model.UncompiledDTO;
import main.domain.model.UserDTO;
import main.infra.adapters.confs.DAO;
import main.infra.adapters.output.repositories.entities.EventListener;
import main.infra.adapters.output.repositories.entities.Log;
import main.infra.adapters.output.repositories.entities.Metadata;
import main.infra.adapters.output.repositories.entities.PropertyParameter;
import main.infra.adapters.output.repositories.entities.ScheUncompiledDags;
import main.infra.adapters.output.repositories.entities.User;
import main.infra.adapters.output.repositories.mappers.SchedulerMapper;


@Component
@ImportResource("classpath:properties-config.xml")
public class SchedulerRepository implements SchedulerRepositoryOutputPort {

	@Autowired
	DAO dao;

	@Autowired
	SchedulerMapper mapper;
	
	@Value("${param.folderpath}")
	private String pathfolder;
	
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
	
	public List<EventListenerDTO> listEventListeners(){
		var list = dao.read(EventListener.class, "select listener from EventListener as listener");
		return list.stream().map(elt -> mapper.toEventListenerDTO(elt)).collect(Collectors.toList());
	}
	public List<EventListenerDTO> getEventListeners(String listenerName){
		var list = dao.read(EventListener.class, "select listener from EventListener as listener where listener.listenerName = '"+listenerName+"'");
		return list.stream().map(elt -> mapper.toEventListenerDTO(elt)).collect(Collectors.toList());
	}
	
	public List<LogDTO> getLogs(String dagname){
		var list = dao.read(Log.class, "select log from Log as log where log.dagname = '"+dagname+"' order by log.execDt desc");
		return list.stream().map(elt -> mapper.toLogDTO(elt)).collect(Collectors.toList()); 
	}
	
	public LogDTO getLog(Integer logid){
		var log = dao.read(Log.class, "select log from Log as log where log.id = :logid",new HashMap<String,Object>(){
			private static final long serialVersionUID = 1L;
		{put("logid",logid);}}).get(0);
		return mapper.toLogDTO(log);
	}
	
	public void setLog(String dagname,String value,String xcom, Map<String, OperatorStatus> status) {
		var entry = new Log();
		entry.setDagname(dagname);
		entry.setExecDt(new Date());
		entry.setValue(value);
		entry.setOutputxcom(xcom);
		JSONObject statusObj = new JSONObject(status);
		entry.setStatus(statusObj.toString());
		dao.save(entry);
	}

	public void deleteLogsBy(Date rolldate) {
		dao.execute("delete from Log where execDt < :rolldate",new HashMap<String,Object>(){
			private static final long serialVersionUID = 1L;
		{put("rolldate",rolldate);}});
	}
	
	public List<UserDTO> findUser(String username) {
		List<User> founded = dao.read(User.class, "select user from User as user where user.username = '"+username+"'");
		return founded.stream().map(elt -> mapper.toUserDTO(elt)).collect(Collectors.toList()); 
	}
	public List<PropertyParameterDTO> getProperties(String groupname) throws Exception{
		List<PropertyParameter> founded;
		if(groupname != null) {
			founded = dao.read(PropertyParameter.class, "select props from PropertyParameter as props where props.group = '"+groupname+"'");
			if(founded.size() == 0) throw new Exception("DAG properties "+groupname+ " not found");
		} else {
			founded = dao.read(PropertyParameter.class, "select props from PropertyParameter as props");
		}
		return founded.stream().map(elt -> mapper.toPropertyParameterDTO(elt)).collect(Collectors.toList());
	}
	public Properties getPropertiesFromDb(String groupname) throws Exception {
		Properties nueva = new Properties();
		var founded = this.getProperties(groupname);
		for (Iterator<PropertyParameterDTO> iterator = founded.iterator(); iterator.hasNext();) {
			PropertyParameterDTO propertyParameter = iterator.next();
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

	public List<AgentDTO> getAgents() {
		List<Metadata> list = dao.read(Metadata.class, "select meta from Metadata meta");
		List<AgentDTO> res = new ArrayList<AgentDTO>();
		for (Iterator<Metadata> iterator = list.iterator(); iterator.hasNext();) {
			Metadata metadata = iterator.next();
			AgentDTO agent = new AgentDTO();
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

	public void addUncompiled(String name, JSONObject json) {
		var list = dao.read(ScheUncompiledDags.class, "select uncom from ScheUncompiledDags uncom where uncom.name = '"+name+"'");
		if(list.isEmpty()) {
			ScheUncompiledDags existingProperties = new ScheUncompiledDags(); 
			existingProperties.setCreatedDt(new Date());
			existingProperties.setBin(json.toString());
			existingProperties.setName(name);
			dao.save(existingProperties);	
		} else {
			throw new RuntimeException("jarname already exists");
		}
	}
	
	public void updateUncompiled(Integer uncompiled,JSONObject json) {
		var list = dao.read(ScheUncompiledDags.class, "select uncom from ScheUncompiledDags uncom where uncom.id = "+uncompiled);
		if(!list.isEmpty()) {
			ScheUncompiledDags existingProperties = list.get(0);
			existingProperties.setBin(json.toString());
			dao.save(existingProperties);	
		} else {
			throw new RuntimeException("uncompiled not exists");
		}
	}

	public List<UncompiledDTO> getUncompileds() {
		var list = dao.read(ScheUncompiledDags.class, "select uncom from ScheUncompiledDags uncom");
		List<UncompiledDTO> rv = new ArrayList<>();
		for (Iterator<ScheUncompiledDags> iterator = list.iterator(); iterator.hasNext();) {
			ScheUncompiledDags scheUncompiledDags = iterator.next();
			UncompiledDTO item = new UncompiledDTO();
			item.setBin(scheUncompiledDags.getBin());
			item.setCreatedDt(scheUncompiledDags.getCreatedDt().getTime());
			item.setUncompiledId(scheUncompiledDags.getUncompiledId());
			rv.add(item);
		}
		return rv;
	}

	@Override
	public String getUncompiledBin(Integer uncompiled) {
		var list = dao.read(ScheUncompiledDags.class, "select uncom from ScheUncompiledDags uncom where uncom.uncompiledId = "+uncompiled);
		String bin = list.get(0).getBin();
		return bin;
	}

	@Override
	public void deleteUncompiled(Integer uncompiled) {
		var list = dao.read(ScheUncompiledDags.class, "select uncom from ScheUncompiledDags uncom where uncom.uncompiledId = "+uncompiled);
		dao.delete(list.get(0));
	}

	@Override
	public void createParams(String jarname,String bin) throws Exception {
		JSONObject def = new JSONObject(bin);
		for (int i = 0; i < def.getJSONArray("dags").length(); i++) {
			JSONObject dag = def.getJSONArray("dags").getJSONObject(i);
			JSONArray boxes = dag.getJSONArray("boxes");
			for (int j = 0; j < boxes.length(); j++) {
				for (int k = 0; k < boxes.length(); k++) {
					var box = boxes.getJSONObject(k);
					String typeope = box.getString("type");
					String idope = box.getString("id");
					String group = jarname+"."+idope+"."+typeope+".props";
					Class<?> clazz = Class.forName(typeope);
					Operator annotation = clazz.getAnnotation(Operator.class);
					for (int l = 0; l < box.getJSONArray("params").length(); l++) {
						JSONObject parm = box.getJSONArray("params").getJSONObject(l);
						if(this.searchValue(annotation.args(), parm.getString("key"))) {
							this.setProperty(parm.getString("key"), "generated parameter from editor", parm.getString("value"), group);	
						}
					}
					String groupo = jarname+"."+idope+"."+typeope+".opts";
					for (int l = 0; l < box.getJSONArray("params").length(); l++) {
						JSONObject parm = box.getJSONArray("params").getJSONObject(l);
						if(this.searchValue(annotation.optionalv(), parm.getString("key"))) {
							this.setProperty(parm.getString("key"), "generated optional from editor", parm.getString("value"), groupo);	
						}
					}
				}
			}
		}
	}
	private boolean searchValue(String[] array, String value) {
        for (String element : array) {
            if (element.equals(value)) {
                return true;
            }
        }
        return false;
    }
	
	@Override
	public String createInternalStatus(JSONObject data) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		String name = sdf.format(new Date())+"_data.json";
		InternalStorage storage = new InternalStorage(pathfolder+name);
		storage.put(data);
		return storage.getLocatedb();
	}

	@Override
	public JSONObject readXcom(String locatedAt) throws Exception {
		InternalStorage storage = new InternalStorage(locatedAt);
		var rv = storage.get();
		return rv;
	}
	
}