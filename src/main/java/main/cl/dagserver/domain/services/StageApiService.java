package main.cl.dagserver.domain.services;
import java.io.File;
import java.nio.file.Path;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.nhl.dflib.DataFrame;

import main.cl.dagserver.application.ports.input.StageApiUsecase;
import main.cl.dagserver.domain.core.BaseServiceComponent;
import main.cl.dagserver.domain.core.DataFrameUtils;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.TemporalDagExecutable;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.PropertyParameterDTO;

@Service
public class StageApiService extends BaseServiceComponent implements StageApiUsecase {

	private static final String PARAMS = "params";
	private static final String VALUE = "value";
	
	@Override
	public JSONObject executeTmp(Integer uncompiled, String dagname, String stepName, String token) throws DomainException {
		auth.untokenize(token);
		String json = repository.getUncompiledBin(uncompiled);
		JSONObject daguncompiled = new JSONObject(json);
		JSONArray dags = daguncompiled.getJSONArray("dags");
		TemporalDagExecutable dagtmp = new TemporalDagExecutable();
		for (int i = 0; i < dags.length(); i++) {
			JSONObject dagobj = dags.getJSONObject(i);
			if(dagobj.getString("name").equals(dagname)) {
				JSONArray steps = dagobj.getJSONArray("boxes");
				try {
					setupDAG(steps,dagtmp);
				} catch (Exception e) {
					throw new DomainException(e);
				}	
			}
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
                    if (param.has("key") && param.has(VALUE) && !param.isNull(VALUE) && param.getString("key").equals(arg) && !param.getString(VALUE).isEmpty()) {
                       options.setProperty(arg, param.getString(VALUE));
                    }	
                }
            }
        }
		return options;
	}
	private void setupDAG(JSONArray steps,TemporalDagExecutable dagtmp) throws ClassNotFoundException, JSONException, DomainException {
		for (int j = 0; j < steps.length(); j++) {
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
			
		}
		for (int j = 0; j < steps.length(); j++) {
			JSONObject step = steps.getJSONObject(j);		
			if(step.has("source")) {
				String sourceStepname = step.getJSONObject("source").getJSONObject("attrs").getJSONObject("label").getString("text");
				String status = step.getString("status").isEmpty() ? "ANY":step.getString("status");
				dagtmp.addDependency(sourceStepname, step.getString("id"),status );
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
			List<String> timestamps = new ArrayList<>();
			this.repository.setLog(parmdata,dagtmp.getStatus(),timestamps);
			var xcom = dagtmp.getXcom();
			JSONObject wrapper = new JSONObject();
			var keys = xcom.keySet();
			for (Iterator<String> iterator2 = keys.iterator(); iterator2.hasNext();) {
				 var string = iterator2.next();
				 wrapper.put(string, DataFrameUtils.dataFrameToJson(xcom.get(string)));
			}
			
			output.put("xcom", wrapper);
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

	@Override
	public void uploadFile(Path tempFile,String uploadPath, String realname, String token) throws DomainException {
		auth.untokenize(token);
		this.fileSystem.upload(tempFile,uploadPath,realname);
	}

	@Override
	public Path getFilePath(String folderPath,String filename, String token) throws DomainException {
		auth.untokenize(token);
		return this.fileSystem.getFilePath(folderPath,filename);
	}

	@Override
	public Map<String, DataFrame> executeDag(String token, String jarname, String dagname, Map<String, String> args) throws DomainException {
		var list = this.repository.getProperties("HTTP_CHANNEL_API_KEY");
		Boolean rv = Boolean.FALSE;
		for (Iterator<PropertyParameterDTO> iterator = list.iterator(); iterator.hasNext();) {
			PropertyParameterDTO propertyParameterDTO = iterator.next();
			if(propertyParameterDTO.getValue().equals(token)) {
				rv = true;
				break;
			}
		}
		try {
			if(Boolean.TRUE.equals(rv)) {
				var completable = scanner.init().execute(jarname, dagname,"HTTP API Endpoint",new JSONObject(args).toString());
				return completable.get();
			} else {
				throw new DomainException(new Exception("Unauthorized"));
			}	
		} catch (Exception e) {
			throw new DomainException(e);
		}
		
	}

	@Override
	public Map<String, DataFrame> executeDag(String jarname, String dagname, JSONObject args) throws DomainException {
		var completable = scanner.init().execute(jarname, dagname,"Calcite Driver",args.toString());
		try {
			return completable.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new DomainException(e);
		}
	}

	@Override
	public File exportKeystore(String token) throws DomainException {
		auth.untokenize(token);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		return keystore.generateKeystoreFile("LOCAL_KEYSTORE_"+sdf.format(new Date()));
	}

	@Override
	public void uploadKeystore(Path tempFile, String originalFilename, String token) throws DomainException {
		auth.untokenize(token);
		this.keystore.importKeystore(tempFile,originalFilename);
	}
}
