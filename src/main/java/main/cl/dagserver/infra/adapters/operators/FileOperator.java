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
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.row.RowProxy;

import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.DataFrameUtils;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;

@Operator(args={"mode","filepath","firstRowTitles"},optionalv = {"xcom","rowDelimiter"})
public class FileOperator extends OperatorStage {

	private SecureRandom random = new SecureRandom();
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	@Override
	public DataFrame call() throws DomainException {		
		try {
			List<Map<String, Object>> returnv = new ArrayList<>();
			log.debug(this.getClass()+" init "+this.name);
			log.debug("args");
			log.debug(this.args);
			log.debug(this.getClass()+" end "+this.name);
			Integer mode = this.getMode(this.args.getProperty("mode"));
			String xcomname = this.optionals.getProperty("xcom");
			String filepath = this.getInputProperty("filepath");
			Boolean firstrow = Boolean.valueOf(this.args.getProperty("firstRowTitles"));
			String rowDelimiter = (this.optionals.getProperty("rowDelimiter") == null || this.optionals.getProperty("rowDelimiter").isEmpty())?"":this.optionals.getProperty("rowDelimiter"); 
			if(mode.equals(0)) {
				 log.debug("mode:read");
				 if(rowDelimiter.trim().isEmpty()) {
					String content = FileUtils.readFileToString(new File(filepath), "UTF-8");
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("content", content);
					returnv.add(map);
				 } else {
					 this.read(filepath, rowDelimiter, firstrow, returnv);	 
				 }
			} else {
				log.debug("mode:write");
				DataFrame outget =  (DataFrame) this.xcom.get(xcomname);				
				if(rowDelimiter.trim().isEmpty()) {
					FileWriter writer = new FileWriter(filepath);
					StringBuilder sb = new StringBuilder();
					String title = outget.getColumnsIndex().getLabel(0);
					String firstValue = outget.getColumn(0).get(0).toString();
					if(firstrow) {
						sb.append(title);
						sb.append(firstValue);
					} else {	
						sb.append(firstValue);
					}
					writer.write( sb.toString() );
			        writer.close();
				} else {
					this.write(filepath, rowDelimiter,firstrow, outget);	
				}

			}
			return DataFrameUtils.buildDataFrameFromMap(returnv);	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	
	private void write(String filepath,String rowDelimiter,Boolean firstrow,DataFrame data) {
		Integer lines = 0;
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));) {
	        if(firstrow) {
	        	List<String> keys = new ArrayList<>();
	        	for (String columnName : data.getColumnsIndex()) {
	        		keys.add(columnName);
            	}
	        	String titles = String.join(rowDelimiter, keys);
	        	writer.write(titles);
	        	writer.newLine();
	        }
	        
	        for (Iterator<RowProxy> iterator = data.iterator(); iterator.hasNext();) {
	        	var row =   iterator.next();
	        	List<String> values = new ArrayList<>();
	        	for (String columnName : data.getColumnsIndex()) {
	        		values.add(row.get(columnName).toString());
            	}
				String resultLine = String.join(rowDelimiter, values);
				writer.write(resultLine);
                writer.newLine();
                lines ++;
			}	
		} catch (Exception e) {
			log.error(e);
		}
		log.debug("write "+filepath+"--lines:"+lines);
	}
	
	private void read(String filepath,String rowDelimiter,Boolean firstrow,List<Map<String, Object>> result) {
		String line;
		Integer lineNumber = 0;
		List<String> titles = new ArrayList<>();
		try(
       		 FileReader fileReader = new FileReader(filepath);
       		 BufferedReader bufferedReader = new BufferedReader(fileReader);
       	) {
       	 while ((line = bufferedReader.readLine()) != null) {
       		 	Map<String,Object> row = new HashMap<String,Object>();	 
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
		metadata.setType("PROCCESS");
		metadata.setParameter("mode", "list", Arrays.asList("read","write"));
		metadata.setParameter("filepath", "text");
		metadata.setParameter("firstRowTitles", "boolean");
		metadata.setOpts("xcom", "xcom");
		metadata.setOpts("rowDelimiter", "text");
		return metadata.generate();
	}
}
