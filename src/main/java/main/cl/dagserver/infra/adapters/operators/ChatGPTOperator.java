package main.cl.dagserver.infra.adapters.operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;
import com.plexpt.chatgpt.ChatGPT;
import joinery.DataFrame;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;


@Operator(args={"apiKey","prompt"},optionalv = {"xcom"})
public class ChatGPTOperator extends OperatorStage {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public DataFrame call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		String xcomname = this.optionals.getProperty("xcom");
		ChatGPT chatGPT = ChatGPT.builder()
                .apiKey(this.args.getProperty("akiKey"))
                .build()
                .init();
		DataFrame returnv = new DataFrame();
		if(xcomname != null && !xcomname.isEmpty()) {
			if(!this.xcom.has(xcomname)) {
				throw new DomainException(new Exception("xcom not exist for dagname::"+xcomname));
			}
			DataFrame df = (DataFrame) this.xcom.get(xcomname);
			
			//dentro del dataframe viene una lista que correspondera a la variable data
			
			List<Map<String, Object>> rv = new ArrayList<>();
			for (Iterator<Map<String, Object>> iterator = df.iterrows(); iterator.hasNext();) {
				Map<String, Object> map = iterator.next();
				String prompt = namedParameter(this.args.getProperty("prompt"),map);
				log.debug("prompt for chatGPT::"+prompt);
				String res = chatGPT.chat(prompt);
				log.debug("response from chatGPT::"+res);
				map.put("prompt", prompt);
				map.put("result", res);
				rv.add(map);
			}
			returnv.add(rv);
		} else {
			List<Map<String, Object>> rv = new ArrayList<>();
			Map<String, Object> map = new HashMap<>();
			String prompt = this.args.getProperty("prompt");
			log.debug("prompt for chatGPT::"+prompt);
			String res = chatGPT.chat(prompt);
			log.debug("response from chatGPT::"+res);
			map.put("prompt", prompt);
			map.put("result", res);
			rv.add(map);
			returnv.add(rv);
		}
		log.debug(this.args);
		log.debug(this.getClass()+" end "+this.name);
		return returnv;
	}
	

	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.ChatGPTOperator");
		metadata.setParameter("apiKey", "password");
		metadata.setParameter("prompt", "sourcecode");
		metadata.setOpts("xcom", "xcom");	
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "chatGpt.png";
	}
	private String namedParameter(String prompt,Map<String, Object> map) {
		Pattern pattern = Pattern.compile(":\\w+");
	    Matcher matcher = pattern.matcher(prompt);
	    List<String> paramNames = new ArrayList<>();
	    while (matcher.find()) {
	        String paramName = matcher.group().substring(1); 
	        paramNames.add(paramName);
	    }
	    for (int i = 0; i < paramNames.size(); i++) {
	        String paramName = paramNames.get(i);
	        String value = map.get(paramName).toString();
	        prompt = prompt.replaceAll(":\\w+", value);
	    }
	    return prompt;
	}
}
