package main.cl.dagserver.infra.adapters.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import joinery.DataFrame;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;

@Operator(args={"qualityjson","xcom"})
public class QualityOperator extends OperatorStage {

    @SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	@Override
    public DataFrame call() throws DomainException {        
        log.debug(this.getClass() + " init " + this.name);
        log.debug("args");
        log.debug(this.args);
        
        String fiends = this.args.getProperty("qualityjson");
        JSONObject dq = new JSONObject(fiends);
        String xcomname = this.args.getProperty("xcom");
        
        if (!this.xcom.has(xcomname)) {
            throw new DomainException(new Exception("xcom not exist for dagname::" + xcomname));
        }
        
        DataFrame df = (DataFrame) this.xcom.get(xcomname);
        List<Map<String, Object>> returningMap = new ArrayList<>();
        for (Iterator<Map<String, Object>> iterator = df.iterrows(); iterator.hasNext();) {
        	Map<String, Object> map = iterator.next();
            
            for (String key : dq.keySet()) {
                Object value = map.get(key);
                String dataTypeTarget = dq.getString(key);    
                map.put("quality_field", key);
                map.put("quality_typeTarget", dataTypeTarget);
                try {
                	Object newValue = castValue(value, dataTypeTarget);
                	map.put("quality_status", "ok");
                	map.put("quality_msg", "");
				} catch (Exception e) {
                	map.put("quality_status", "ok");
                	map.put("quality_msg", e.getMessage());
				}
            }
            returningMap.add(map);
        }
        
        log.debug(this.getClass() + " end " + this.name);
        return this.buildDataFrame(returningMap);
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
