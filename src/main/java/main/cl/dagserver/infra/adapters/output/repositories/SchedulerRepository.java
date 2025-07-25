package main.cl.dagserver.infra.adapters.output.repositories;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jakarta.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import com.nhl.dflib.DataFrame;
import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;
import main.cl.dagserver.application.ports.output.StorageOutputPort;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.DagExecutable;
import main.cl.dagserver.domain.enums.OperatorStatus;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.AgentDTO;
import main.cl.dagserver.domain.model.EventListenerDTO;
import main.cl.dagserver.domain.model.LogDTO;
import main.cl.dagserver.domain.model.PropertyParameterDTO;
import main.cl.dagserver.domain.model.UncompiledDTO;
import main.cl.dagserver.domain.model.UserDTO;
import main.cl.dagserver.infra.adapters.confs.DAO;
import main.cl.dagserver.infra.adapters.output.repositories.entities.EventListener;
import main.cl.dagserver.infra.adapters.output.repositories.entities.Log;
import main.cl.dagserver.infra.adapters.output.repositories.entities.Metadata;
import main.cl.dagserver.infra.adapters.output.repositories.entities.PropertyParameter;
import main.cl.dagserver.infra.adapters.output.repositories.entities.ScheUncompiledDags;
import main.cl.dagserver.infra.adapters.output.repositories.entities.User;

@Component
@ImportResource("classpath:properties-config.xml")
public class SchedulerRepository implements SchedulerRepositoryOutputPort {

	private static final String QUERYPROPS =  "select props from PropertyParameter as props where props.group = '";
	private static final String VALUE = "value";
	private static final String VALUEP = "value.";
	private static final String OPTEXT = ".opts";
	private ModelMapper modelMapper = new ModelMapper();
	private static final String UNCOMPILEDQUERY = "select uncom from ScheUncompiledDags uncom where uncom.uncompiledId = ";
	
	@Autowired
	private StorageOutputPort storage;

	@Autowired
	private Environment environment;
	
	@Autowired
	private DAO dao;
	
	
	@PostConstruct
    private void loadPropertiesToRepo() {
    	var listp = Arrays.asList(this.environment.getActiveProfiles());
    	for (Iterator<String> iterator = listp.iterator(); iterator.hasNext();) {
			String string =  iterator.next();
			PropertyParameter param = new PropertyParameter();
			param.setDescription("active profile");
	    	param.setName(string);
	    	param.setValue(Boolean.TRUE.toString());
	    	param.setGroup("active_profiles");
	    	dao.save(param);
		}
    }	
	
	
	public void addEventListener(String name,String onstart,String onend,String groupname,String tag, String jarname) {
		var event = new EventListener();
		event.setListenerName(name);
		event.setOnStart(onstart);
		event.setOnEnd(onend);
		event.setGroupName(groupname);
		event.setTag(tag);
		event.setJarname(jarname);
		dao.save(event);
	}
	
	public void removeListener(String name) {
		HashMap<String, Object> param = new HashMap<>();
		param.put("name", name);
		dao.execute("delete from sche_events_listeners where LISTENER_NAME = :name",param);
	}
	
	public List<EventListenerDTO> listEventListeners(){
		var list = dao.read(EventListener.class, "select listener from EventListener as listener");
		return list.stream().map(elt -> modelMapper.map(elt,EventListenerDTO.class)).toList();
	}
	public List<EventListenerDTO> getEventListeners(String listenerName){
		var list = dao.read(EventListener.class, "select listener from EventListener as listener where listener.listenerName = '"+listenerName+"'");
		return list.stream().map(elt -> modelMapper.map(elt,EventListenerDTO.class)).toList();
	}
	
	public List<LogDTO> getLogs(String dagname){
		var list = dao.read(Log.class, "select log from Log as log where log.dagname = '"+dagname+"' order by log.execDt desc");
		return list.stream().map(elt -> modelMapper.map(elt,LogDTO.class)).toList();
	}
	
	public LogDTO getLog(Integer logid){
		HashMap<String, Object> param = new HashMap<>();
		param.put("logid",logid);
		var log = dao.read(Log.class, "select log from Log as log where log.id = :logid",param).get(0);
		return modelMapper.map(log,LogDTO.class);
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
		dao.execute("delete from sche_logs where exec_dt < :rolldate",param);
	}
	
	public List<UserDTO> findUser(String username) {
		List<User> founded = dao.read(User.class, "select user from User as user where user.username = '"+username+"'");
		return founded.stream().map(elt -> modelMapper.map(elt,UserDTO.class)).toList();
	}
	public List<PropertyParameterDTO> getProperties(String groupname) throws DomainException{
		try {
			List<PropertyParameter> founded;
			if(groupname != null) {
				founded = dao.read(PropertyParameter.class,QUERYPROPS+groupname+"'");
			} else {
				founded = dao.read(PropertyParameter.class, "select props from PropertyParameter as props");
			}
			return founded.stream().map(elt -> modelMapper.map(elt,PropertyParameterDTO.class)).toList();
		} catch (Exception e) {
			throw new DomainException(e);
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
	    
	    List<String> keys = new ArrayList<>();
	    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
	        String key = (String) entry.getKey();
	        if(key.startsWith(VALUEP)) {
		    	String real = key.replace(VALUEP, "");
		    	if(!keys.contains(real)) {
		    		keys.add(real);
		    	}
		    }
	        
	    }
	     
	    for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			String value = properties.getProperty(VALUEP+key);
			String descr = properties.getProperty("desc."+key);
			String group = properties.getProperty("group."+key);
			List<PropertyParameter> existingProperties = dao.read(PropertyParameter.class,QUERYPROPS + group + "'");
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
	            newProperty.setGroup(group);
	            newProperty.setName(key);
	            newProperty.setValue(value);
	            newProperty.setDescription(descr);
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
			throw new DomainException(new Exception("jarname already exists"));
		}
	}
	
	public void updateUncompiled(Integer uncompiled,JSONObject json) throws DomainException {
		var list = dao.read(ScheUncompiledDags.class, "select uncom from ScheUncompiledDags uncom where uncom.id = "+uncompiled);
		if(!list.isEmpty()) {
			ScheUncompiledDags existingProperties = list.get(0);
			existingProperties.setBin(json.toString());
			dao.save(existingProperties);	
		} else {
			throw new DomainException(new Exception("uncompiled not exists"));
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
	public List<String> createParams(String jarname,String bin) throws DomainException {
		try {
			JSONObject def = new JSONObject(bin);
			List<String> groupprops = new ArrayList<>();
			for (int i = 0; i < def.getJSONArray("dags").length(); i++) {
				JSONObject dag = def.getJSONArray("dags").getJSONObject(i);
				JSONArray boxes = dag.getJSONArray("boxes");
				var groups = this.processBoxes(boxes, jarname);
				groupprops.addAll(groups);
			}	
			return groupprops;
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	
	private List<String> processBoxes(JSONArray boxes,String jarname) throws ClassNotFoundException {
		List<String> arr = new ArrayList<>();
		for (int j = 0; j < boxes.length(); j++) {
			for (int k = 0; k < boxes.length(); k++) {
				var box = boxes.getJSONObject(k);
				String typeope = box.getString("type");
				String idope = box.getString("id");
				String group = jarname+"."+idope+"."+typeope+".props";
				String optns = jarname+"."+idope+"."+typeope+OPTEXT;
				Class<?> clazz = Class.forName(typeope);
				Operator annotation = clazz.getAnnotation(Operator.class);
				this.delGroupProperty(group);
				this.boxHasParams(box, annotation, group, jarname);
				arr.add(group);
				arr.add(optns);
			}
		}
		return arr;
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
			String groupo = jarname+"."+idope+"."+typeope+OPTEXT;
			this.delGroupProperty(groupo);
			for (int l = 0; l < box.getJSONArray(params).length(); l++) {
				JSONObject parm = box.getJSONArray(params).getJSONObject(l);
				if(this.searchValue(annotation.optionalv(), parm.getString(key))) {
					this.setProperty(parm.getString(key), "generated optional from editor",parm.optString(value, ""), groupo);	
				}
			}	
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

	private boolean searchValue(String[] array, String value) {
        for (String element : array) {
            if (element.equals(value)) {
                return true;
            }
        }
        return false;
    }
	
	@Override
	public String createInternalStatus(Map<String,DataFrame> data) throws DomainException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
			var key = sdf.format(new Date());
			storage.putEntry(key,data);
			return key;	
		} catch (Exception e) {
			throw new DomainException(e);
		}
		
	}

	@Override
	public Map<String, DataFrame> readXcom(String locatedAt) throws DomainException {
		try {
			return storage.getEntry(locatedAt);
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}

	

	@Override
	public List<UserDTO> getUsers() {
		List<UserDTO> list = new ArrayList<>();
		var users = dao.read(User.class, "select creds from User as creds");
		for (Iterator<User> iterator = users.iterator(); iterator.hasNext();) {
			User user = iterator.next();
			list.add(modelMapper.map(user,UserDTO.class));
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
		this.updateItems(jarname+"."+idope+"."+typeope+OPTEXT, bindata);
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
		dao.execute("delete from sche_logs where log_id = :logid",param);
	}

	@Override
	public void deleteAllLogs(String dagname) {
		HashMap<String, Object> param = new HashMap<>();
		param.put("dagname",dagname);
		dao.execute("delete from sche_logs where dagname = :dagname",param);
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

	@Override
	public List<LogDTO> getLastLogs() {
		List<Log> list = dao.read(Log.class,"select log from Log as log order by log.execDt desc",new HashMap<>(),0,5);
		return list.stream().map(elt -> modelMapper.map(elt,LogDTO.class)).toList();
	}
	
	@Override
	public List<LogDTO> getAllLogs() {
		List<Log> list = dao.read(Log.class,"select log from Log as log order by log.execDt desc",new HashMap<>());
		return list.stream().map(elt -> modelMapper.map(elt,LogDTO.class)).toList();
	}

	@Override
	public String getUncompiledBinByName(String jarname) {
		var list = dao.read(ScheUncompiledDags.class, "select uncom from ScheUncompiledDags uncom where uncom.name = '"+jarname+"'");
		return list.get(0).getBin();
	}

	@Override
	public Map<String, List<DagExecutable>> getListeners(DagExecutable dag,Map<String,DagExecutable> events) {
		var listener = this.listEventListeners();
		Map<String,List<DagExecutable>> returned = new HashMap<>();
        List<DagExecutable> onEndListeners = new ArrayList<>();
        List<DagExecutable> onStartListeners = new ArrayList<>();
		for (Iterator<EventListenerDTO> iterator = listener.iterator(); iterator.hasNext();) {
			EventListenerDTO eventListenerDTO = iterator.next();
			if(eventListenerDTO.getTag().equals("DAG")){
				if(dag.getDagname().equals(eventListenerDTO.getOnEnd())) {
					onEndListeners.add(events.get(eventListenerDTO.getListenerName()));
				} else if(dag.getDagname().equals(eventListenerDTO.getOnStart())) {
					onStartListeners.add(events.get(eventListenerDTO.getListenerName()));
				}
			} else {
				if(dag.getGroup().equals(eventListenerDTO.getOnEnd())) {
					onEndListeners.add(events.get(eventListenerDTO.getListenerName()));
				} else if(dag.getGroup().equals(eventListenerDTO.getOnStart())) {
					onStartListeners.add(events.get(eventListenerDTO.getListenerName()));
				}
			}
		}
		returned.put("onStart", onStartListeners);
		returned.put("onEnd", onEndListeners);
		return returned;
	}
}
