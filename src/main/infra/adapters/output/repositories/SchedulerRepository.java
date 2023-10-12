package main.infra.adapters.output.repositories;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
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
import main.domain.exceptions.DomainException;
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

	private static final String QUERYPROPS =  "select props from PropertyParameter as props where props.group = '";
	private static final String VALUE = "value";
	private static final String UNCOMPILEDQUERY = "select uncom from ScheUncompiledDags uncom where uncom.uncompiledId = ";
	
	@Autowired
	DAO dao;

	@Autowired
	SchedulerMapper mapper;
	
	@Value("${param.folderpath}")
	private String pathfolder;
	
	@Value("${param.xcompath}")
	private String xcomfolder;
	
	
	
	public void addEventListener(String name,String onstart,String onend,String groupname) {
		var event = new EventListener();
		event.setListenerName(name);
		event.setOnStart(onstart);
		event.setOnEnd(onend);
		event.setGroupName(groupname);
		dao.save(event);
	}
	
	public void removeListener(String name) {
		HashMap<String, Object> param = new HashMap<>();
		param.put("name", name);
		dao.execute("delete from EventListener where listenerName = :name",param);
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
		HashMap<String, Object> param = new HashMap<>();
		param.put("logid",logid);
		var log = dao.read(Log.class, "select log from Log as log where log.id = :logid",param).get(0);
		return mapper.toLogDTO(log);
	}

	public void setLog(Map<String, String> parmdata, Map<String, OperatorStatus> status, List<String> timestamps) {
		String evalkey = parmdata.get("evalkey");
		String dagname  = parmdata.get("dagname");
		String value = parmdata.get(VALUE);
		String xcom = parmdata.get("xcom");
		String channel = parmdata.get("channel");
		String objetive = parmdata.get("objetive");
		String sourceType = parmdata.get("sourceType");
		HashMap<String, Object> param = new HashMap<>();
		JSONArray arr = new JSONArray(timestamps);
		param.put("evalkey",evalkey);
		var founded = dao.read(Log.class, "select log from Log log where log.evalkey = :evalkey",param);
		if(founded.isEmpty()) {
			var entry = new Log();
			entry.setDagname(dagname);
			entry.setEvalkey(evalkey);
			entry.setExecDt(new Date());
			entry.setValue(value);
			entry.setOutputxcom(xcom);
			entry.setChannel(channel);
			entry.setObjetive(objetive);
			entry.setSourceType(sourceType);
			entry.setMarks(arr.toString());
			JSONObject statusObj = new JSONObject(status);
			entry.setStatus(statusObj.toString());
			dao.save(entry);	
		} else {
			JSONObject statusObj = new JSONObject(status);
			var entry = founded.get(0);
			entry.setValue(value);
			entry.setMarks(arr.toString());
			entry.setStatus(statusObj.toString());
			entry.setObjetive(objetive);
			entry.setSourceType(sourceType);
			entry.setOutputxcom(xcom);
			entry.setChannel(channel);
			dao.save(entry);	
		}
	}

	public void deleteLogsBy(Date rolldate) {
		HashMap<String, Object> param = new HashMap<>();
		param.put("rolldate",rolldate);
		dao.execute("delete from Log where execDt < :rolldate",param);
	}
	
	public List<UserDTO> findUser(String username) {
		List<User> founded = dao.read(User.class, "select user from User as user where user.username = '"+username+"'");
		return founded.stream().map(elt -> mapper.toUserDTO(elt)).collect(Collectors.toList()); 
	}
	public List<PropertyParameterDTO> getProperties(String groupname) throws DomainException{
		try {
			List<PropertyParameter> founded;
			if(groupname != null) {
				founded = dao.read(PropertyParameter.class,QUERYPROPS+groupname+"'");
			} else {
				founded = dao.read(PropertyParameter.class, "select props from PropertyParameter as props");
			}
			return founded.stream().map(elt -> mapper.toPropertyParameterDTO(elt)).collect(Collectors.toList());	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
		
	}
	public Properties getPropertiesFromDb(String groupname) throws DomainException {
		Properties nueva = new Properties();
		var founded = this.getProperties(groupname);
		for (Iterator<PropertyParameterDTO> iterator = founded.iterator(); iterator.hasNext();) {
			PropertyParameterDTO propertyParameter = iterator.next();
			nueva.setProperty(propertyParameter.getName(), propertyParameter.getValue());
		}
		return nueva;
	}

	public void setProperty(String name, String description, String value,String group) {
		var founded = dao.read(PropertyParameter.class,QUERYPROPS+group+"' and props.name = '"+name+"'");
		if(founded.isEmpty()) {
			PropertyParameter nuevo = new PropertyParameter();
			nuevo.setDescription(description);
			nuevo.setName(name);
			nuevo.setValue(value);
			nuevo.setGroup(group);
			dao.save(nuevo);	
		} else {
			PropertyParameter nuevo = founded.get(0);
			nuevo.setDescription(description);
			nuevo.setName(name);
			nuevo.setValue(value);
			nuevo.setGroup(group);
			dao.save(nuevo);
		}
	}

	public void delProperty(String name,String group) {
		var founded = dao.read(PropertyParameter.class,QUERYPROPS+group+"' and props.name = '"+name+"'");
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
		List<AgentDTO> res = new ArrayList<>();
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
	    List<PropertyParameter> existingProperties = dao.read(PropertyParameter.class,QUERYPROPS + jarname+"."+propertiesFile + "'");
	    
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

	public void addUncompiled(String name, JSONObject json) throws DomainException {
		var list = dao.read(ScheUncompiledDags.class, "select uncom from ScheUncompiledDags uncom where uncom.name = '"+name+"'");
		if(list.isEmpty()) {
			ScheUncompiledDags existingProperties = new ScheUncompiledDags(); 
			existingProperties.setCreatedDt(new Date());
			existingProperties.setBin(json.toString());
			existingProperties.setName(name);
			dao.save(existingProperties);	
		} else {
			throw new DomainException("jarname already exists");
		}
	}
	
	public void updateUncompiled(Integer uncompiled,JSONObject json) throws DomainException {
		var list = dao.read(ScheUncompiledDags.class, "select uncom from ScheUncompiledDags uncom where uncom.id = "+uncompiled);
		if(!list.isEmpty()) {
			ScheUncompiledDags existingProperties = list.get(0);
			existingProperties.setBin(json.toString());
			dao.save(existingProperties);	
		} else {
			throw new DomainException("uncompiled not exists");
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
		var list = dao.read(ScheUncompiledDags.class, UNCOMPILEDQUERY+uncompiled);
		return list.get(0).getBin();
	}

	@Override
	public void deleteUncompiled(Integer uncompiled) {
		var list = dao.read(ScheUncompiledDags.class, UNCOMPILEDQUERY+uncompiled);
		dao.delete(list.get(0));
	}

	@Override
	public void createParams(String jarname,String bin) throws DomainException {
		try {
			JSONObject def = new JSONObject(bin);
			for (int i = 0; i < def.getJSONArray("dags").length(); i++) {
				JSONObject dag = def.getJSONArray("dags").getJSONObject(i);
				JSONArray boxes = dag.getJSONArray("boxes");
				this.processBoxes(boxes, jarname);
			}	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	
	private void processBoxes(JSONArray boxes,String jarname) throws ClassNotFoundException {
		for (int j = 0; j < boxes.length(); j++) {
			for (int k = 0; k < boxes.length(); k++) {
				var box = boxes.getJSONObject(k);
				String typeope = box.getString("type");
				String idope = box.getString("id");
				String group = jarname+"."+idope+"."+typeope+".props";
				Class<?> clazz = Class.forName(typeope);
				Operator annotation = clazz.getAnnotation(Operator.class);
				this.deletePropsByGroup(group);
				this.boxHasParams(box, annotation, group, jarname);
			}
		}
	}
	
	private void boxHasParams(JSONObject box,Operator annotation,String group,String jarname) {
		String typeope = box.getString("type");
		String idope = box.getString("id");
		String params = "params";
		String value = VALUE;
		String key = "key";
		if(box.has(params)) {
			for (int l = 0; l < box.getJSONArray(params).length(); l++) {
				JSONObject parm = box.getJSONArray(params).getJSONObject(l);
				if(this.searchValue(annotation.args(), parm.getString(key))) {
					this.setProperty(parm.getString(key), "generated parameter from editor", parm.getString(value), group);	
				}
			}
			String groupo = jarname+"."+idope+"."+typeope+".opts";
			this.deletePropsByGroup(groupo);
			for (int l = 0; l < box.getJSONArray(params).length(); l++) {
				JSONObject parm = box.getJSONArray(params).getJSONObject(l);
				if(this.searchValue(annotation.optionalv(), parm.getString(key))) {
					this.setProperty(parm.getString(key), "generated optional from editor", parm.getString(value), groupo);	
				}
			}	
		}
	}
	
	private void deletePropsByGroup(String group) {
		HashMap<String, Object> params = new HashMap<>();
		params.put("group", group);
		dao.execute("delete from PropertyParameter where group = :group",params);
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
	public String createInternalStatus(JSONObject data) throws DomainException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
			String name = sdf.format(new Date())+"_data.json";
			InternalStorage storage = new InternalStorage(xcomfolder+name);
			storage.put(data);
			return storage.getLocatedb();	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
		
	}

	@Override
	public JSONObject readXcom(String locatedAt) throws DomainException {
		try {
			InternalStorage storage = new InternalStorage(locatedAt);
			return storage.get();
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}

	@Override
	public void delGroupProperty(String group) {
		var founded = dao.read(PropertyParameter.class,QUERYPROPS+group+"'");
		for (Iterator<PropertyParameter> iterator = founded.iterator(); iterator.hasNext();) {
			PropertyParameter propertyParameter = iterator.next();
			dao.delete(propertyParameter);
		}
		
	}

	@Override
	public List<UserDTO> getUsers() {
		List<UserDTO> list = new ArrayList<>();
		var users = dao.read(User.class, "select creds from User as creds");
		for (Iterator<User> iterator = users.iterator(); iterator.hasNext();) {
			User user = iterator.next();
			list.add(mapper.toUserDTO(user));
		}
		return list;
	}

	@Override
	public void createAccount(String username, String accountType, String pwdHash) {
		User newuser = new User();
		newuser.setCreatedAt(new Date());
		newuser.setPwdhash(pwdHash);
		newuser.setTypeAccount(accountType);
		newuser.setUsername(username);
		dao.save(newuser);
	}

	@Override
	public void delAccount(String username) {
		var users = dao.read(User.class, "select creds from User as creds where creds.username = '"+username+"'");
		for (Iterator<User> iterator = users.iterator(); iterator.hasNext();) {
			User user = iterator.next();
			dao.delete(user);
		}
		
	}

	@Override
	public void updateParams(String idope,String typeope,String jarname, String bin) {
		byte[] bytesDecodificados = Base64.getDecoder().decode(bin);
        String cadenaDecodificada = new String(bytesDecodificados);
        JSONArray bindata = new JSONArray(cadenaDecodificada);
		this.updateItems(jarname+"."+idope+"."+typeope+".props", bindata);
		this.updateItems(jarname+"."+idope+"."+typeope+".opts", bindata);
	}
	private void updateItems(String groupname,JSONArray bindata) {
		var founded = dao.read(PropertyParameter.class,QUERYPROPS+groupname+"'");
		for (Iterator<PropertyParameter> iterator = founded.iterator(); iterator.hasNext();) {
			PropertyParameter propertyParameter = iterator.next();
			for (int i = 0; i < bindata.length(); i++) {
				JSONObject b = bindata.getJSONObject(i);
				if(b.getString("key").equals(propertyParameter.getName())) {
					propertyParameter.setValue(b.getString(VALUE));
					dao.save(propertyParameter);
				}
			}
		}
	}

	@Override
	public void updateprop(String group, String key, String value) {
		var founded = dao.read(PropertyParameter.class,QUERYPROPS+group+"'");
		for (Iterator<PropertyParameter> iterator = founded.iterator(); iterator.hasNext();) {
			PropertyParameter propertyParameter = iterator.next();
			if(propertyParameter.getName().equals(key)) {
				propertyParameter.setValue(value);
				dao.save(propertyParameter);
				break;
			}
		}
	}

	@Override
	public void deleteLog(Integer logid) {
		HashMap<String, Object> param = new HashMap<>();
		param.put("logid",logid);
		dao.execute("delete from Log where id = :logid",param);
	}

	@Override
	public void deleteAllLogs(String dagname) {
		HashMap<String, Object> param = new HashMap<>();
		param.put("dagname",dagname);
		dao.execute("delete from Log where dagname = :dagname",param);
	}

	@Override
	public void renameUncompiled(Integer uncompiled, String newname) {
		var uncompiledObj = dao.read(ScheUncompiledDags.class, UNCOMPILEDQUERY+uncompiled).get(0);
		uncompiledObj.setName(newname);
		String bin = uncompiledObj.getBin();
		JSONObject binobj = new JSONObject(bin);
		binobj.put("jarname",newname);
		uncompiledObj.setBin(binobj.toString());
		dao.save(uncompiledObj);
	}

	
}
