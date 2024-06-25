package main.cl.dagserver.infra.adapters.operators;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import com.github.javafaker.Faker;
import joinery.DataFrame;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;

@Operator(args={"fakerjson","count","locale"})
public class FakerOperator extends OperatorStage {

    @SuppressWarnings({ "rawtypes"})
	@Override
    public DataFrame call() throws DomainException {        
        log.debug(this.getClass() + " init " + this.name);
        log.debug("args");
        log.debug(this.args);

        String fiends = this.args.getProperty("fakerjson");
        
        JSONArray fields = new JSONArray(fiends);
        
        String locale = this.args.getProperty("locale");
        Integer count = Integer.parseInt(this.args.getProperty("count"));
        Faker faker = new Faker(new Locale(locale));
        
        List<String> methodsToInvoke = new ArrayList<>();
        for (int i = 0; i < fields.length(); i++) {
			String key = fields.getString(i);
			methodsToInvoke.add(key);
		}
        List<Map<String, Object>> maps = new ArrayList<>();
        for (int i = 0; i < count; i++) {
        	Map<String, Object> item = new HashMap<>();
            for (String methodName : methodsToInvoke) {
                String result = invokeFakerMethod(faker, methodName);
                if (result != null) {
                    item.put(methodName, result);
                }
            }
            maps.add(item);
		}
        return this.buildDataFrame(maps);
    }

    private String invokeFakerMethod(Faker faker, String methodName) {
        try {
            String[] parts = methodName.split("\\.");
            Object currentObject = faker;
            for (int i = 0; i < parts.length - 1; i++) {
                Method method = currentObject.getClass().getMethod(parts[i]);
                currentObject = method.invoke(currentObject);
            }
            Method method = currentObject.getClass().getMethod(parts[parts.length - 1]);
            return (String) method.invoke(currentObject);
        } catch (Exception e) {
            log.error("Error invoking faker method: " + methodName, e);
            return null;
        }
    }

    @Override
    public JSONObject getMetadataOperator() {
        MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.FakerOperator");
        metadata.setParameter("fakerjson", "sourcecode");
        metadata.setParameter("count", "number");
        metadata.setParameter("locale", "list", Arrays.asList("en-US","en-UG","es","fr"));
        return metadata.generate();
    }

    @Override
    public String getIconImage() {
        return "faker.png";
    }
}
