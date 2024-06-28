package main.cl.dagserver.infra.adapters.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.row.RowProxy;

import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
 
@Operator(args={"qualityjson","xcom"})
public class QualityOperator extends OperatorStage {

	@SuppressWarnings("unused")
	@Override
    public DataFrame call() throws DomainException {        
        log.debug(this.getClass() + " init " + this.name);
        log.debug("args");
        log.debug(this.args);
        
        String fiends = this.args.getProperty("qualityjson");
        JSONObject dq = new JSONObject(fiends);
        String xcomname = this.args.getProperty("xcom");
        
        if (!this.xcom.containsKey(xcomname)) {
            throw new DomainException(new Exception("xcom not exist for dagname::" + xcomname));
        }
        
        DataFrame df = (DataFrame) this.xcom.get(xcomname);
        List<Map<String, Object>> returningMap = new ArrayList<>();
        List<String> quality_fieldList = new ArrayList<>();
    	List<String> quality_typeTargetList = new ArrayList<>();
    	List<String> quality_statusList = new ArrayList<>();
    	List<String> quality_msgList = new ArrayList<>();
        for (Iterator<RowProxy> iterator = df.iterator(); iterator.hasNext();) {
        	RowProxy map = iterator.next();
        	
            for (String key : dq.keySet()) {
                Object value = map.get(key);
                String dataTypeTarget = dq.getString(key);    
                quality_fieldList.add(key);
                quality_typeTargetList.add(dataTypeTarget);
                try {
                	Object newValue = castValue(value, dataTypeTarget);
                	quality_statusList.add("ok");
                	quality_msgList.add("");
				} catch (Exception e) {
					quality_statusList.add("error");
                	quality_msgList.add(e.getMessage());
				}
            }
        }
        Series<String> quality_fieldSeries = Series.of(quality_fieldList.toArray(new String[0]));
        Series<String> quality_typeTargetSeries = Series.of(quality_typeTargetList.toArray(new String[0]));
        Series<String> quality_statusSeries = Series.of(quality_statusList.toArray(new String[0]));
        Series<String> quality_msgListSeries = Series.of(quality_msgList.toArray(new String[0]));
        df = df.addColumn("quality_field", quality_fieldSeries);
        df = df.addColumn("quality_typeTarget", quality_typeTargetSeries);
        df = df.addColumn("quality_status", quality_statusSeries);
        df = df.addColumn("quality_msgList", quality_msgListSeries);
        log.debug(this.getClass() + " end " + this.name);
        return OperatorStage.buildDataFrame(returningMap);
    }

    private Object castValue(Object value, String dataTypeTarget) throws DomainException {
        try {
            switch (dataTypeTarget) {
                case "String":
                case "Char":
                    return value.toString();
                case "Integer":
                    return Integer.parseInt(value.toString());
                case "Float":
                    return Float.parseFloat(value.toString());
                case "Double":
                    return Double.parseDouble(value.toString());
                case "Long":
                    return Long.parseLong(value.toString());
                case "Date":
                    // Assuming date format is yyyy-MM-dd
                    return java.sql.Date.valueOf(value.toString());
                case "Timestamp":
                    // Assuming timestamp format is yyyy-MM-dd HH:mm:ss
                    return java.sql.Timestamp.valueOf(value.toString());
                case "Boolean":
                    return Boolean.parseBoolean(value.toString());
                case "BigInt":
                    return new java.math.BigInteger(value.toString());
                case "byte":
                    return Byte.parseByte(value.toString());
                default:
                    throw new DomainException(new Exception("Unsupported data type: " + dataTypeTarget));
            }
        } catch (Exception e) {
            throw new DomainException(new Exception("Error casting value: " + value + " to type: " + dataTypeTarget, e));
        }
    }

    @Override
    public JSONObject getMetadataOperator() {
        MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.QualityOperator");
        metadata.setParameter("qualityjson", "sourcecode");
        metadata.setOpts("xcom","xcom");
        return metadata.generate();
    }

    @Override
    public String getIconImage() {
        return "dataquality.png";
    }
}
