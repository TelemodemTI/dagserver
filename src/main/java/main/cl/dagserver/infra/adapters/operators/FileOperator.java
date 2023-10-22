package main.cl.dagserver.infra.adapters.operators;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;

@Operator(args={"mode","filepath","rowDelimiter","firstRowTitles"},optionalv = {"xcom"})
public class FileOperator extends OperatorStage implements Callable<List<Object>> {

	private SecureRandom random = new SecureRandom();
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object> call() throws DomainException {		
		try {
			List<Object> result = new ArrayList<>();
			log.debug(this.getClass()+" init "+this.name);
			log.debug("args");
			log.debug(this.args);
			log.debug(this.getClass()+" end "+this.name);
			Integer mode = this.getMode(this.args.getProperty("mode"));
			String xcomname = this.optionals.getProperty("xcom");
			String filepath = this.args.getProperty("filepath");
			Boolean firstrow = Boolean.valueOf(this.args.getProperty("firstRowTitles"));
			String rowDelimiter = this.args.getProperty("rowDelimiter");
			if(mode.equals(0)) {
				 log.debug("mode:read");
				 if(rowDelimiter.trim().isEmpty()) {
					String content = FileUtils.readFileToString(new File(filepath), "UTF-8");
					result.add(content);
				 } else {
					 this.read(filepath, rowDelimiter, firstrow, result);	 
				 }
			} else {
				log.debug("mode:write");
				List<Object> data = (List<Object>) this.xcom.get(xcomname);
				if(rowDelimiter.trim().isEmpty()) {
					FileWriter writer = new FileWriter(filepath);
			        writer.write(data.get(0).toString());
			        writer.close();
				} else {
					this.write(filepath, rowDelimiter,firstrow, data);	
				}

			}
			return result;	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void write(String filepath,String rowDelimiter,Boolean firstrow,List<Object> data) {
		Integer lines = 0;
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));) {
	        if(firstrow) {
	        	var first = data.get(0);
	        	var maps = (Map<String, String>) first;
	        	String titles = String.join(rowDelimiter, maps.keySet());
	        	writer.write(titles);
	        	writer.newLine();
	        }
	        
			for (Iterator<Object> iterator = data.iterator(); iterator.hasNext();) {
				Map<String, String> map =  (Map<String, String>) iterator.next();
				String resultLine = String.join(rowDelimiter, map.values());
				writer.write(resultLine);
                writer.newLine();
                lines ++;
			}	
		} catch (Exception e) {
			log.error(e);
		}
		log.debug("write "+filepath+"--lines:"+lines);
	}
	
	private void read(String filepath,String rowDelimiter,Boolean firstrow,List<Object> result) {
		String line;
		Integer lineNumber = 0;
		List<String> titles = new ArrayList<>();
		try(
       		 FileReader fileReader = new FileReader(filepath);
       		 BufferedReader bufferedReader = new BufferedReader(fileReader);
       	) {
       	 while ((line = bufferedReader.readLine()) != null) {
	        	 Map<String, String> row = new HashMap<>();
	        	 String[] fields = line.split(rowDelimiter);
	        	 if(lineNumber.equals(0)) {
	        		 if(Boolean.TRUE.equals(firstrow)) {
	        			 titles = Arrays.asList(fields);	 
	        		 } else {
	        			 titles = this.generateTitleList(fields.length);
	        		 }
	        	 } else {
	        		 for (int i = 0; i < fields.length; i++) {
	 	 				String string = fields[i];
	 	 				row.put(titles.get(i), string);
	 	 			 }
	        		 result.add(row);
	        	 }
	        	 lineNumber++;
	         }	
        } catch (Exception e) {
			log.error(e);
        }
		log.debug("readed "+filepath+"--lines:"+lineNumber);
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
        for (int i = 0; i < length; i++) {
            int randomIndex = this.random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }
	
	@Override
	public String getIconImage() {
		return "file.png";
	}
	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.FileOperator");
		metadata.setParameter("mode", "list", Arrays.asList("read","write"));
		metadata.setParameter("filepath", "text");
		metadata.setParameter("rowDelimiter", "text");
		metadata.setParameter("firstRowTitles", "boolean");
		metadata.setOpts("xcom", "xcom");
		return metadata.generate();
	}
}
