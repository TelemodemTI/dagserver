package main.cl.dagserver.infra.adapters.input.graphql;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import main.cl.dagserver.application.ports.input.LoginUseCase;
import main.cl.dagserver.application.ports.input.SchedulerQueryUseCase;
import main.cl.dagserver.domain.core.DataFrameUtils;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.DagDTO;
import main.cl.dagserver.domain.model.LogDTO;
import main.cl.dagserver.infra.adapters.input.graphql.mappers.QueryResolverMapper;
import main.cl.dagserver.infra.adapters.input.graphql.types.Account;
import main.cl.dagserver.infra.adapters.input.graphql.types.Agent;
import main.cl.dagserver.infra.adapters.input.graphql.types.Available;
import main.cl.dagserver.infra.adapters.input.graphql.types.Channel;
import main.cl.dagserver.infra.adapters.input.graphql.types.Deps;
import main.cl.dagserver.infra.adapters.input.graphql.types.Detail;
import main.cl.dagserver.infra.adapters.input.graphql.types.DetailStatus;
import main.cl.dagserver.infra.adapters.input.graphql.types.Exceptions;
import main.cl.dagserver.infra.adapters.input.graphql.types.LogEntry;
import main.cl.dagserver.infra.adapters.input.graphql.types.Node;
import main.cl.dagserver.infra.adapters.input.graphql.types.Property;
import main.cl.dagserver.infra.adapters.input.graphql.types.Scheduled;
import main.cl.dagserver.infra.adapters.input.graphql.types.Uncompiled;


@Component
public class QueryResolver implements GraphQLQueryResolver {
	private static final String JOBLISTENER = "JOB LISTENER";
	private SchedulerQueryUseCase handler;
	private LoginUseCase login;
	private QueryResolverMapper mapper;

	@Autowired
	public QueryResolver(SchedulerQueryUseCase handler,LoginUseCase login,QueryResolverMapper mapper) {
		this.handler = handler;
		this.login = login;
		this.mapper = mapper;
	}
	
	public String login(String token) {
		return login.apply(token);
	}
	
	public String operatorsMetadata() throws DomainException {
		return handler.operators().toString();
	}
	
	public List<Scheduled> scheduledJobs() throws DomainException {
        var rv = new ArrayList<Scheduled>();
		var list = handler.listScheduledJobs();
        for (Iterator<Map<String, Object>> iterator = list.iterator(); iterator.hasNext();) {
			Map<String, Object> map = iterator.next();
			var scheduled = new Scheduled();
			scheduled.setGroupname(map.get("jobgroup").toString());
			scheduled.setDagname(map.get("jobname").toString());
			if(map.get("nextFireAt") != null) {
				Date nfat = (Date) map.get("nextFireAt");
				scheduled.setNextFireAt(nfat.getTime());	
			} else {
				scheduled.setNextFireAt(0L);
			}
			scheduled.setEventTrigger(map.get("eventTrigger").toString());
			rv.add(scheduled);
		}
        return rv;
    }	
	public List<Available> availableJobs() throws DomainException {
	    var operators = handler.availableJobs();
	    var rv = new ArrayList<Available>();

	    for (Map.Entry<String, List<Map<String, String>>> entry : operators.entrySet()) {
	        String jarname = entry.getKey();
	        for (Map<String, String> operatormap : entry.getValue()) {
	            var operator = new Available();
	            operator.setJarname(jarname);
	            operator.setClassname(operatormap.get("classname"));
	            operator.setCronExpr(operatormap.get("cronExpr"));
	            operator.setGroupname(operatormap.get("groupname"));
	            operator.setDagname(operatormap.get("dagname"));

	            String cronExpr = operatormap.get("cronExpr");
	            if (!cronExpr.isEmpty()) {
	                operator.setTriggerEvent("CRON STATEMENT");
	            } else {
	                configureListener(operatormap, operator);
	            }

	            rv.add(operator);
	        }
	    }

	    rv.addAll(getDefaults());
	    return rv;
	}
	private void configureListener(Map<String, String> operatormap,Available operator) {
		 String starttr = operatormap.get("onStart");
         String endtr = operatormap.get("onEnd");

         if (!starttr.isEmpty()) {
             operator.setTargetDagname(operatormap.get(starttr));
             operator.setTriggerEvent(JOBLISTENER);
         }
         if (!endtr.isEmpty()) {
             operator.setTargetDagname(operatormap.get(endtr));
             operator.setTriggerEvent(JOBLISTENER);
         }
         if (starttr.isEmpty() && endtr.isEmpty()) {
             operator.setTriggerEvent("NONE");
         }
	}
	private List<Available> getDefaults(){
		var rv = new ArrayList<Available>();
		var bsd = new Available();
		bsd.setJarname("SYSTEM");
		bsd.setClassname("main.domain.dags.BackgroundSystemDag");
		bsd.setCronExpr("0 0/10 * * * ?");
		bsd.setDagname("background_system_dag");
		bsd.setGroupname("system_dags");
		bsd.setTriggerEvent("CRON STATEMENT");
		rv.add(bsd);
		
		var events = new Available();
		events.setJarname("SYSTEM");
		events.setClassname("main.domain.dags.EventSystemDag");
		events.setDagname("event_system_dag");
		events.setGroupname("system_dags");
		events.setTriggerEvent(JOBLISTENER);
		events.setTargetDagname("background_system_dag");
		rv.add(events);
		
		return rv;
	}
	public List<LogEntry> logs(String dagname) throws DomainException{
		var rv = new ArrayList<LogEntry>();
		var arr = handler.getLogs(dagname);
		this.setListLog(rv, arr);	
		return rv;
	}
	private void setListLog(List<LogEntry> rv,List<LogDTO> arr) {
		for (Iterator<LogDTO> iterator = arr.iterator(); iterator.hasNext();) {
			LogDTO log =  iterator.next();
			var entry = new LogEntry();
			entry.setDagname(log.getDagname());
			entry.setExecDt(log.getExecDt());
			entry.setId(log.getId());
			entry.setValue(log.getValue());
			JSONObject wrapper = new JSONObject();
			var mpa = log.getXcom();
			var keys = mpa.keySet();
			for (Iterator<String> iterator2 = keys.iterator(); iterator2.hasNext();) {
				 var string = iterator2.next();
				 wrapper.put(string, DataFrameUtils.dataFrameToJson(mpa.get(string)));
			}
			entry.setOutputxcom(wrapper.toString());
			entry.setXcomkey(log.getXcomkey());
			entry.setChannel(log.getChannel());
			entry.setStatus(log.getStatus());
			entry.setMarks(log.getMarks());
			rv.add(entry);
		}
	}
	public List<LogEntry> last() throws DomainException{
		var rv = new ArrayList<LogEntry>();
		var arr = handler.getLastLogs();
		this.setListLog(rv, arr);
		return rv;
	}
	
	
	public DetailStatus detail(String jarname) throws DomainException{
		
		DetailStatus status = new DetailStatus();
		var rv = new ArrayList<Detail>();
		try {
			var map = handler.getDagDetail(jarname);
			for (Iterator<DagDTO> iterator = map.iterator(); iterator.hasNext();) {
				DagDTO detail = iterator.next();
				Detail det = new Detail();
				det.setDagname(detail.getDagname());
				det.setCronExpr(detail.getCronExpr());
				det.setGroup(detail.getGroup());
				det.setOnEnd(detail.getOnEnd());
				det.setOnStart(detail.getOnStart());
				List<Node> nodes = new ArrayList<>();
				int i = 1;
				for (Iterator<List<String>> iterator2 = detail.getOps().iterator(); iterator2.hasNext();i++) {
					Node node = new Node();
					List<String> ops = iterator2.next();
					node.setOperations(ops);
					node.setIndex(i);
					nodes.add(node);
				}
				det.setNode(nodes);
				rv.add(det);
			}
			status.setStatus("ok");
		} catch (Exception e) {
			status.setStatus(ExceptionUtils.getRootCauseMessage(e));
		}
		status.setDetail(rv);
		return status;
	}
	public List<Property> properties() throws DomainException{
		return handler.properties().stream().map(elt -> mapper.toProperty(elt)).toList();
	}
	public List<Agent> agents(){
		return handler.agents().stream().map(elt -> mapper.toAgent(elt)).toList();
	}
	
	public List<Uncompiled> getUncompileds(String token) throws DomainException{
		return handler.getUncompileds(token).stream().map(elt -> mapper.toUncompiled(elt)).toList();
	}
	public List<Account> credentials(String token) throws DomainException{
		return handler.credentials(token).stream().map(elt -> mapper.toAccount(elt)).toList();
	}
	public String getIcons(String type) throws DomainException {
		return handler.getIcons(type);
	}
	public Deps getDependencies(String jarname,String dagname) throws DomainException {
		var returned = handler.getDependencies(jarname, dagname);
		Deps deps = new Deps();
		deps.setOnStart(returned.get(0));
		deps.setOnEnd(returned.get(1));
		return deps; 
	}
	public List<Channel> channelStatus(String token) throws DomainException {
		return handler.getChannels(token).stream().map(elt -> mapper.toChannel(elt)).toList();
	}
	public String exportUncompiled(String token,Integer uncompiled) throws DomainException {
		return handler.exportUncompiled(token,uncompiled);
	}
	public List<Exceptions> exceptions(String token) {
		return handler.getExceptions(token);
	}
	
	
}
