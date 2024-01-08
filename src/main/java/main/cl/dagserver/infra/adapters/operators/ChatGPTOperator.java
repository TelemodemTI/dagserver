package main.cl.dagserver.infra.adapters.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;
import com.plexpt.chatgpt.ChatGPT;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.Dagmap;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;


@Operator(args={"apiKey","prompt"},optionalv = {"xcom"})
public class ChatGPTOperator extends OperatorStage {

	@SuppressWarnings("unchecked")
	@Override
	public List<Dagmap> call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		String xcomname = this.optionals.getProperty("xcom");
		ChatGPT chatGPT = ChatGPT.builder()
                .apiKey(this.args.getProperty("akiKey"))
                .build()
                .init();
		Dagmap returnv = new Dagmap();
		if(xcomname != null && !xcomname.isEmpty()) {
			if(!this.xcom.has(xcomname)) {
				throw new DomainException(new Exception("xcom not exist for dagname::"+xcomname));
			}
			List<Map<String, Object>> data = (List<Map<String, Object>>) this.xcom.get(xcomname);
			for (Iterator<Map<String, Object>> iterator = data.iterator(); iterator.hasNext();) {
				Map<String, Object> map = iterator.next();
				String prompt = namedParameter(this.args.getProperty("prompt"),map);
				log.debug("prompt for chatGPT::"+prompt);
				String res = chatGPT.chat(prompt);
				log.debug("response from chatGPT::"+res);
				returnv.put("prompt", prompt);
				returnv.put("result", res);
			}
		} else {
			String prompt = this.args.getProperty("prompt");
			log.debug("prompt for chatGPT::"+prompt);
			String res = chatGPT.chat(prompt);
			log.debug("response from chatGPT::"+res);
			returnv.put("prompt", prompt);
			returnv.put("result", res);
		}
		log.debug(this.args);
		log.debug(this.getClass()+" end "+this.name);
		List<Dagmap> list = new ArrayList<>();
		list.add(returnv);
		return list;
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
