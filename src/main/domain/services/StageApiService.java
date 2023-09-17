package main.domain.services;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import main.application.ports.input.StageApiUsecase;
import main.domain.core.BaseServiceComponent;
import main.domain.annotations.Operator;
import main.domain.core.TemporalDagExecutable;

@Service
public class StageApiService extends BaseServiceComponent implements StageApiUsecase {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(StageApiService.class);
	private static final String PARAMS = "params";
	private static final String VALUE = "value";
	
	@Override
	public JSONObject executeTmp(Integer uncompiled, String dagname, String stepName, String token) {
		tokenEngine.untokenize(token, jwtSecret, jwtSigner);
		String json = repository.getUncompiledBin(uncompiled);
		JSONObject daguncompiled = new JSONObject(json);
		JSONArray dags = daguncompiled.getJSONArray("dags");
		TemporalDagExecutable dagtmp = new TemporalDagExecutable();
		for (int i = 0; i < dags.length(); i++) {
			JSONObject dagobj = dags.getJSONObject(i);
			JSONArray steps = dagobj.getJSONArray("boxes");
			setupDAG(steps,dagtmp);
		}
		return this.generateOutput(dagtmp, dagname, stepName);
	}
	
	private Properties loadProperties(String[] args,JSONObject step) {
		Properties properties = new Properties();
		for (String arg : args) {
            if (step.has(PARAMS)) {
                JSONArray params = step.getJSONArray(PARAMS);
                for (int k = 0; k < params.length(); k++) {
                    JSONObject param = params.getJSONObject(k);
                    if (param.has("key") && param.has(VALUE) && param.getString("key").equals(arg)) {
                        properties.setProperty(arg, param.getString(VALUE));
                    }
                }
            }
        }
		return properties;
	}
	private Properties loadOptionals(String[] optionals,JSONObject step) {
		Properties options = new Properties();
		for (String arg : optionals) {
            if (step.has(PARAMS)) {
                JSONArray params = step.getJSONArray(PARAMS);
                for (int k = 0; k < params.length(); k++) {
                    JSONObject param = params.getJSONObject(k);
                    if (param.has("key") && param.has(VALUE) && param.getString("key").equals(arg) && !param.getString(VALUE).isEmpty()) {
                    	options.setProperty(arg, param.getString(VALUE));
                    }
                }
            }
        }
		return options;
	}
	private void setupDAG(JSONArray steps,TemporalDagExecutable dagtmp) {
		for (int j = 0; j < steps.length(); j++) {
			try {
				JSONObject step = steps.getJSONObject(j);
				Class<?> impl = Class.forName(step.getString("type"));
		        Operator operatorAnnotation = impl.getAnnotation(Operator.class);
		        Properties properties = null;
		        Properties options = new Properties();
		        if (operatorAnnotation != null) {
		            properties = loadProperties(operatorAnnotation.args(),step);
		            options = loadOptionals(operatorAnnotation.optionalv(), step);
		        }
				dagtmp.addOperator(step.getString("id"), impl, properties , options);	
			} catch (Exception e) {
				log.error(e);
			}
		}
		for (int j = 0; j < steps.length(); j++) {
			JSONObject step = steps.getJSONObject(j);		
			if(step.has("source")) {
				String sourceStepname = step.getJSONObject("source").getJSONObject("attrs").getJSONObject("label").getString("text");
				dagtmp.addDependency(sourceStepname, step.getString("id"), step.getString("status"));
			}
		}
	}
	private JSONObject generateOutput(TemporalDagExecutable dagtmp,String dagname,String stepName) {
		JSONObject output = new JSONObject();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
			String result = dagtmp.execute(stepName);
			String locatedAt = repository.createInternalStatus(dagtmp.getXcom());		
			String objetive = (stepName.isEmpty())?"COMPLETE":stepName;
			Map<String,String> parmdata = new HashMap<>(); 
			parmdata.put("evalkey",dagtmp.getEvalstring());
			parmdata.put("dagname",dagname);
			parmdata.put(VALUE,dagtmp.getLogText());
			parmdata.put("xcom",locatedAt);
			parmdata.put("channel","TEST_API");
			parmdata.put("objetive",objetive);
			parmdata.put("sourceType","UNCOMPILED");
			this.repository.setLog(parmdata,dagtmp.getStatus());
			JSONObject xcom = dagtmp.getXcom();
			output.put("xcom", xcom);
			output.put("result", result);
			output.put("dagname", dagname);
			output.put("objetive", stepName);
			output.put("evalDt", sdf.format(dagtmp.getEvalDt()));
			output.put("log", dagtmp.getLogText());
		} catch (Exception e) {
			output.put("result", e.getMessage());
		}
		return output;
	}
}
