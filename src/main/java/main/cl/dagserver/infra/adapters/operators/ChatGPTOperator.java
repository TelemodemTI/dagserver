package main.cl.dagserver.infra.adapters.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.row.RowProxy;
import com.plexpt.chatgpt.ChatGPT;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;


@Operator(args={"apiKey","prompt"},optionalv = {"xcom"})
public class ChatGPTOperator extends OperatorStage {

	@Override
	public DataFrame call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		String xcomname = this.optionals.getProperty("xcom");
		ChatGPT chatGPT = ChatGPT.builder()
                .apiKey(this.args.getProperty("akiKey"))
                .build()
                .init();
		
		
		if(xcomname != null && !xcomname.isEmpty()) {
			if(!this.xcom.containsKey(xcomname)) {
				throw new DomainException(new Exception("xcom not exist for dagname::"+xcomname));
			}
			DataFrame df = this.xcom.get(xcomname);
			List<String> promptList = new ArrayList<>();
	        List<String> resultList = new ArrayList<>();
			for (Iterator<RowProxy> iterator = df.iterator() ; iterator.hasNext();) {
				var row = iterator.next();			
				String prompt = namedParameter(this.args.getProperty("prompt"),row);
				log.debug("prompt for chatGPT::"+prompt);
				String res = chatGPT.chat(prompt);
				log.debug("response from chatGPT::"+res);
				promptList.add(prompt);
	            resultList.add(res);
			}
			Series<String> promptSeries = Series.of(promptList.toArray(new String[0]));
	        Series<String> resultSeries = Series.of(resultList.toArray(new String[0]));
	        df = df.addColumn("prompt", promptSeries);
	        df = df.addColumn("result", resultSeries);
	        log.debug(this.args);
			log.debug(this.getClass()+" end "+this.name);
			return df;
		} else {
			String prompt = this.args.getProperty("prompt");
			log.debug("prompt for chatGPT::"+prompt);
			String res = chatGPT.chat(prompt);
			log.debug("response from chatGPT::"+res);
			log.debug(this.args);
			log.debug(this.getClass()+" end "+this.name);
			return DataFrame.byArrayRow("prompt","result").appender().append(prompt,res).toDataFrame();
		}
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
	private String namedParameter(String prompt,RowProxy map) {
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
