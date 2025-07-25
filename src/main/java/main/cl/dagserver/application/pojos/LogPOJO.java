package main.cl.dagserver.application.pojos;

import java.util.Date;
import java.util.Map;

import com.nhl.dflib.DataFrame;

import lombok.Data;

@Data
public class LogPOJO {
	private Integer id;
	private String dagname;
	private Date execDt;
	private String value;
	private String outputxcom;
	private String xcomkey;
	private Map<String,DataFrame> xcom;
	private String marks;
	private String status;
	private String channel;
	
}
