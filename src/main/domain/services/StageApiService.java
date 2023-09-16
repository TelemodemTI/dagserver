package main.domain.services;
import java.text.SimpleDateFormat;
import java.util.Properties;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import main.application.ports.input.StageApiUsecase;
import main.domain.core.BaseServiceComponent;
import main.domain.annotations.Operator;
import main.domain.core.TemporalDagExecutable;

@Service
public class StageApiService extends BaseServiceComponent implements StageApiUsecase {

	
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
			for (int j = 0; j < steps.length(); j++) {
				try {
					JSONObject step = steps.getJSONObject(j);
					Class<?> impl = Class.forName(step.getString("type"));
			        Operator operatorAnnotation = impl.getAnnotation(Operator.class);
			        Properties properties = new Properties();
			        Properties options = new Properties();
			        if (operatorAnnotation != null) {
			            String[] args = operatorAnnotation.args();
			            String[] optionals = operatorAnnotation.optionalv();
			            for (String arg : args) {
			                if (step.has("params")) {
			                    JSONArray params = step.getJSONArray("params");
			                    for (int k = 0; k < params.length(); k++) {
			                        JSONObject param = params.getJSONObject(k);
			                        if (param.has("key") && param.has("value") && param.getString("key").equals(arg)) {
			                            properties.setProperty(arg, param.getString("value"));
			                        }
			                    }
			                }
			            }
			            for (String arg : optionals) {
			                if (step.has("params")) {
			                    JSONArray params = step.getJSONArray("params");
			                    for (int k = 0; k < params.length(); k++) {
			                        JSONObject param = params.getJSONObject(k);
			                        if (param.has("key") && param.has("value") && param.getString("key").equals(arg) && !param.getString("value").isEmpty()) {
			                        	options.setProperty(arg, param.getString("value"));
			                        }
			                    }
			                }
			            }
			        }
					dagtmp.addOperator(step.getString("id"), impl, properties , options);	
				} catch (Exception e) {
					
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
		JSONObject output = new JSONObject();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
			String result = dagtmp.execute(stepName);
			String locatedAt = repository.createInternalStatus(dagtmp.getXcom());		
			String objetive = (stepName.isEmpty())?"COMPLETE":"INCOMPLETE";
			this.repository.setLog(dagtmp.getEvalstring(),dagname,dagtmp.getLogText(), locatedAt,dagtmp.getStatus(),"STAGE_API",objetive,"UNCOMPILED");
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
