package main.infra.adapters.input.graphql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import main.application.ports.input.LoginUseCase;
import main.application.ports.input.SchedulerQueryUseCase;
import main.domain.model.DagDTO;
import main.domain.model.LogDTO;
import main.infra.adapters.input.graphql.types.Agent;
import main.infra.adapters.input.graphql.types.Available;
import main.infra.adapters.input.graphql.types.Detail;
import main.infra.adapters.input.graphql.types.DetailStatus;
import main.infra.adapters.input.graphql.types.LogEntry;
import main.infra.adapters.input.graphql.types.Node;
import main.infra.adapters.input.graphql.types.Property;
import main.infra.adapters.input.graphql.types.Scheduled;
import main.infra.adapters.input.graphql.types.Uncompiled;
import main.infra.adapters.input.graphql.mappers.QueryResolverMapper;

@Component
public class QueryResolver implements GraphQLQueryResolver {
	
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(QueryResolver.class);
	
	@Autowired
	SchedulerQueryUseCase handler;
	
	@Autowired
	LoginUseCase login;
	
	@Autowired
	QueryResolverMapper mapper;
	
	public String login(String username,String pwdhash) throws Exception {
		String token = login.apply(Arrays.asList(username, pwdhash));
		return token;
	}
	
	public String operatorsMetadata() throws Exception {
		return handler.operators().toString();
	}
	
	public List<Scheduled> scheduledJobs() throws Exception {
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
	public List<Available> availableJobs() throws Exception{
		var operators = handler.availableJobs();
		var keys = operators.keySet();
		var rv = new ArrayList<Available>();
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			var props = operators.get(string);
			for (Iterator<Map<String,String>> iterator2 = props.iterator(); iterator2.hasNext();) {
				var operatormap = iterator2.next();
				var operator = new Available();
				operator.setJarname(string);
				operator.setClassname(operatormap.get("classname"));
				operator.setCronExpr(operatormap.get("cronExpr"));
				operator.setGroupname(operatormap.get("groupname"));
				operator.setDagname(operatormap.get("dagname"));
				operator.setTriggerEvent("CRON STATEMENT");
				rv.add(operator);	
			}

		}
		rv.addAll(getDefaults());
		return rv;
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
		events.setTriggerEvent("OnEnd");
		events.setTargetDagname("background_system_dag");
		rv.add(events);
		
		return rv;
	}
	public List<LogEntry> logs(String dagname){
		var rv = new ArrayList<LogEntry>();
		try {
			var arr = handler.getLogs(dagname);
			for (Iterator<LogDTO> iterator = arr.iterator(); iterator.hasNext();) {
				LogDTO log =  iterator.next();
				var entry = new LogEntry();
				entry.setDagname(log.getDagname());
				entry.setExecDt(log.getExecDt().getTime());
				entry.setId(log.getId());
				entry.setValue(log.getValue());
				entry.setXcomoutput(log.getOutputxcom());
				entry.setStatus(log.getStatus());
				rv.add(entry);
			}	
		} catch (Exception e) {
			logger.error(e);
		}		
		return rv;
	}
	public DetailStatus detail(String jarname) throws Exception{
		
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
				List<Node> nodes = new ArrayList<Node>();
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
	public List<Property> properties() throws Exception{
		return handler.properties().stream().map(elt -> mapper.toProperty(elt)).collect(Collectors.toList());
	}
	public List<Agent> agents(){
		return handler.agents().stream().map(elt -> mapper.toAgent(elt)).collect(Collectors.toList());
	}
	
	public List<Uncompiled> getUncompileds(String token) throws Exception{
		return handler.getUncompileds(token).stream().map(elt -> mapper.toUncompiled(elt)).collect(Collectors.toList());
	}
}
