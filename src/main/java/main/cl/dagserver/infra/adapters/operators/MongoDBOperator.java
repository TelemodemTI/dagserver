package main.cl.dagserver.infra.adapters.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.bson.Document;
import org.json.JSONObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.row.RowProxy;

import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.DataFrameUtils;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;


@Operator(args={"hostname","port","mode","database","collection","timeout"},optionalv = {"username","password","filter","xcom"})
public class MongoDBOperator extends OperatorStage {

	@Override
	public DataFrame call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		List<Map<String,Object>> list = new ArrayList<>();
		String conUrl = "";
		if(this.optionals.containsKey("username")) {
			conUrl = "mongodb://"+this.optionals.getProperty("username")+":"+this.optionals.getProperty("password")+"@"+this.args.getProperty("hostname")+":"+this.args.getProperty("port")+"/?connectTimeoutMS="+this.args.getProperty("timeout");	
		} else {
			conUrl = "mongodb://"+this.args.getProperty("hostname")+":"+this.args.getProperty("port")+"/?connectTimeoutMS="+this.args.getProperty("timeout");
		}
		MongoClient mongoClient = MongoClients.create(
				MongoClientSettings.builder().applyConnectionString(new ConnectionString(conUrl))
				      .applyToSocketSettings(builder ->
				      builder.connectTimeout(5, TimeUnit.SECONDS))
				      .build());
		String mode = this.args.getProperty("mode");
		if(mode.equals("READ")) {
			list = this.read(mongoClient);
		} else if(mode.equals("INSERT")) {
			list = this.insert(mongoClient);
		} else {
			list = this.delete(mongoClient);
		}
		log.debug(this.getClass()+" end "+this.name);
		return DataFrameUtils.buildDataFrameFromMap(list);
	}
	
	private List<Map<String,Object>> read(MongoClient mongoClient) throws DomainException {
		  MongoDatabase database = mongoClient.getDatabase(this.args.getProperty("database"));
		  MongoCollection<Document> collection = database.getCollection(this.args.getProperty("collection"));
		  FindIterable<Document> cursor = null;
		  if(this.optionals.containsKey("filter")) {
    		  String totalFilter = this.optionals.getProperty("filter");
    		  Document filter = Document.parse(totalFilter);
    		  cursor = collection.find(filter);
    	  } else {
    		  cursor = collection.find();
    	  }
		  List<Map<String,Object>> list = new ArrayList<>();
		  try (final MongoCursor<Document> cursorIterator = cursor.cursor()) {
			    while (cursorIterator.hasNext()) {
			    	Map<String,Object> row = new HashMap<String,Object>();
			        Document doc = cursorIterator.next();
			        for (String key : doc.keySet()) {
			            Object value = doc.get(key);
			            row.put(key, value);
			        }
			        list.add(row);
			    }
			    return list;
		  } catch (Exception e) {
			  throw new DomainException(e);
		}
	}
	
	
	
	private List<Map<String,Object>> insert(MongoClient mongoClient) throws DomainException {
		  MongoDatabase database = mongoClient.getDatabase(this.args.getProperty("database"));
		  MongoCollection<Document> collection = database.getCollection(this.args.getProperty("collection"));
		  if(this.optionals.getProperty("xcom") != null && !this.optionals.getProperty("xcom").isEmpty()) {
	    	  String xcomname = this.optionals.getProperty("xcom");
	    	  if(!this.xcom.containsKey(xcomname)) {
					throw new DomainException(new Exception("xcom not exist for dagname::"+xcomname));
	    	  }
	    	  List<Map<String,Object>> rv = new ArrayList<>();
	    	  DataFrame df = (DataFrame) this.xcom.get(xcomname);
	    	  List<Document> collectionData = new ArrayList<>();
	    	  Integer position = 0;
	    	  for (Iterator<RowProxy> iterator = df.iterator(); iterator.hasNext();) {
				RowProxy map = iterator.next();
				Document document = new Document();
				for (String columnName : df.getColumnsIndex()) {
		            Object valor = map.get(columnName);
		            document.put(columnName, valor);
				}
				collectionData.add(document);
				Map<String,Object> status = new HashMap<String,Object>();
				status.put("rownumber", position);
				rv.add(status);
				position++;
	    	  }
	    	  collection.insertMany(collectionData);
	    	  return rv;
		  } else {
			  throw new DomainException(new Exception("no xcom selected for write?"));
		  }
	}


	private List<Map<String,Object>> delete(MongoClient mongoClient) throws DomainException {
		  MongoDatabase database = mongoClient.getDatabase(this.args.getProperty("database"));
		  MongoCollection<Document> collection = database.getCollection(this.args.getProperty("collection"));
		  if(this.optionals.getProperty("xcom") != null && !this.optionals.getProperty("xcom").isEmpty()) {
	    	  String xcomname = this.optionals.getProperty("xcom");
	    	  if(!this.xcom.containsKey(xcomname)) {
					throw new DomainException(new Exception("xcom not exist for dagname::"+xcomname));
	    	  }
	    	  List<Map<String,Object>> rv = new ArrayList<>();
	    	  DataFrame df = (DataFrame) this.xcom.get(xcomname);
	    	  Integer position = 0;	    	  
	    	  for (Iterator<RowProxy> iterator = df.iterator(); iterator.hasNext();) {
					RowProxy map = iterator.next();
					Document document = new Document();
					for (String columnName : df.getColumnsIndex()) {
						Object valor = map.get(columnName);
						document.put(columnName, valor);
					}
					Map<String,Object> status = new HashMap<String,Object>();
					collection.deleteOne(document);
					status.put("rownumber", position);
					rv.add(status);
					position++;
	    	  }
	    	  return rv;
		  } else {
			  throw new DomainException(new Exception("no xcom selected for delete?"));
		  }
	}
	
	
	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.MongoDBOperator");
		metadata.setType("EXTERNAL");
		metadata.setParameter("hostname", "text");
		metadata.setParameter("mode", "list", Arrays.asList("READ","INSERT","DELETE"));
		metadata.setParameter("port", "number");
		metadata.setParameter("database", "text");
		metadata.setParameter("collection", "text");
		metadata.setParameter("timeout", "number");
		metadata.setOpts("xcom", "xcom");
		metadata.setOpts("username", "text");
		metadata.setOpts("password", "password");
		metadata.setOpts("filter", "sourcecode",Arrays.asList("application/json"));
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "mongodb.png";
	}
}
