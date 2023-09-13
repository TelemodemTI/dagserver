package main.infra.adapters.operators;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import org.json.JSONObject;
import main.domain.annotations.Operator;
import main.domain.core.MetadataManager;
import main.domain.core.OperatorStage;
import main.domain.exceptions.DomainException;

@Operator(args={"mode","filepath","rowDelimiter","firstRowTitles"},optionalv = {"xcom"})
public class FileOperator extends OperatorStage implements Callable<List<Map<String, String>>> {

	private SecureRandom random = new SecureRandom();
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> call() throws DomainException {		
		try {
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
				 this.read(filepath, rowDelimiter, firstrow, result);
			} else {
				log.debug("mode:write");
				List<Map<String, String>> data = (List<Map<String, String>>) this.xcom.get(xcomname);
				this.write(filepath, rowDelimiter, data);
			}
			return result;	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	
	private void write(String filepath,String rowDelimiter,List<Map<String, String>> data) {
		Integer lines = 0;
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));) {
	        for (Iterator<Map<String, String>> iterator = data.iterator(); iterator.hasNext();) {
				Map<String, String> map =  iterator.next();
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
	
	private void read(String filepath,String rowDelimiter,Boolean firstrow,List<Map<String, String>> result) {
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
		MetadataManager metadata = new MetadataManager("main.infra.adapters.operators.FileOperator");
		metadata.setParameter("mode", "list", Arrays.asList("read","write"));
		metadata.setParameter("filepath", "text");
		metadata.setParameter("rowDelimiter", "text");
		metadata.setParameter("firstRowTitles", "boolean");
		metadata.setOpts("xcom", "text");
		return metadata.generate();
	}
}
