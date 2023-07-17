package main.infra.adapters.operators;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONObject;

import main.domain.annotations.Operator;
import main.domain.core.DagExecutable;
import main.infra.adapters.input.graphql.types.OperatorStage;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;

@Operator(args={"mode","filepath","rowDelimiter","firstRowTitles"},optionalv = {"xcom"})
public class FileOperator extends OperatorStage implements Callable<List<Map<String, String>>> {

	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> call() throws Exception {		
		List<Map<String, String>> result = new ArrayList<>();
		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		log.debug(this.getClass()+" end "+this.name);
		
		Integer mode = this.getMode(this.args.getProperty("mode"));
		String xcomname = this.args.getProperty("xcom");
		String filepath = this.args.getProperty("filepath");
		Boolean firstrow = Boolean.valueOf(this.args.getProperty("firstRowTitles"));
		String rowDelimiter = this.args.getProperty("rowDelimiter");
		
		if(mode.equals(0)) {
			 log.debug("mode:read");
	         FileReader fileReader = new FileReader(filepath);
	         BufferedReader bufferedReader = new BufferedReader(fileReader);
	         String line;
	         Integer lineNumber = 0;
	         List<String> titles = new ArrayList<>();
	         
	         while ((line = bufferedReader.readLine()) != null) {
	        	 Map<String, String> row = new HashMap<String,String>();
	        	 String[] fields = line.split(rowDelimiter);
	        	 if(lineNumber.equals(0)) {
	        		 if(firstrow) {
	        			 titles = Arrays.asList(fields);	 
	        		 } else {
	        			 titles = this.generateTitleList(fields.length);
	        		 }
	        	 }
	        	 for (int i = 0; i < fields.length; i++) {
	 				String string = fields[i];
	 				row.put(titles.get(i), string);
	 			 }	 
	        	 lineNumber++;
	        	 result.add(row);
	         }
	         bufferedReader.close();
	         log.debug("readed "+filepath+"--lines:"+lineNumber);
		} else {
			log.debug("mode:write");
			List<Map<String, String>> data = (List<Map<String, String>>) this.xcom.get(xcomname);
			BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
			Integer lines = 0;
	        for (Iterator<Map<String, String>> iterator = data.iterator(); iterator.hasNext();) {
				Map<String, String> map =  iterator.next();
				String resultLine = String.join(rowDelimiter, map.values());
				writer.write(resultLine);
                writer.newLine();
                lines ++;
			}
	        writer.close();
	        log.debug("write "+filepath+"--lines:"+lines);
		}
		return result;
	}
	
	private Integer getMode(String mode) {
		return mode.equals("read")?0:1;
	}
	private List<String> generateTitleList(Integer length){
		List<String> lista = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			lista.add(this.generateRandomString(10));
		}
		return lista;
	}
	
	private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }
	
	@Override
	public Implementation getDinamicInvoke(String stepName,String propkey, String optkey) throws Exception {
		Implementation implementation = MethodCall.invoke(DagExecutable.class.getDeclaredMethod("addOperator", String.class, Class.class, String.class , String.class)).with(stepName, FileOperator.class,propkey,optkey);
		return implementation;
	}

	
	public String getIconImage() {
		return "file.png";
	}
	@Override
	public JSONObject getMetadataOperator() {
		JSONArray params = new JSONArray();
		params.put(new JSONObject("{name:\"mode\",type:\"list\",opt:[\"read\",\"write\"]}"));
		params.put(new JSONObject("{name:\"filepath\",type:\"text\"}"));
		params.put(new JSONObject("{name:\"rowDelimiter\",type:\"text\"}"));
		params.put(new JSONObject("{name:\"firstRowTitles\",type:\"boolean\"}"));
		params.put(new JSONObject("{name:\"xcom\",type:\"text\"}"));
		JSONObject tag = new JSONObject();
		tag.put("class", "main.infra.adapters.operators.FileOperator");
		tag.put("name", "FileOperator");
		tag.put("params", params);
		return tag;
	}
}
