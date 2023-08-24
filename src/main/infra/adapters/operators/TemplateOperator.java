package main.infra.adapters.operators;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import main.domain.annotations.Operator;
import main.domain.core.DagExecutable;
import main.domain.exceptions.DomainException;
import main.infra.adapters.input.graphql.types.OperatorStage;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;

@Operator(args={"template"})
public class TemplateOperator extends OperatorStage implements Callable<String> {

	@Override
	public String call() throws DomainException {		
		log.debug(this.getClass() + " init " + this.name);
	    log.debug("args");

	    MustacheFactory mf = new DefaultMustacheFactory();
	    Mustache mustache = mf.compile(this.args.getProperty("template"));

	    Map<String, Object> paramMap = new HashMap<>();
	    paramMap.put("args", this.args);
	    paramMap.put("optionals", this.optionals);
	    paramMap.put("xcom", this.xcom);

	    StringWriter writer = new StringWriter();
	    mustache.execute(writer, paramMap);
	    String templateOutput = writer.toString();
	    log.debug(this.args);
	    log.debug(this.getClass() + " end " + this.name);
	    return templateOutput;
	}
	
	@Override
	public Implementation getDinamicInvoke(String stepName,String propkey, String optkey) throws DomainException {
		try {
			return MethodCall.invoke(DagExecutable.class.getDeclaredMethod("addOperator", String.class, Class.class,String.class)).with(stepName, TemplateOperator.class,propkey);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}

	@Override
	public JSONObject getMetadataOperator() {
		JSONArray params = new JSONArray();
		params.put(new JSONObject("{name:\"template\",type:\"sourcecode\"}"));
		
		JSONObject tag = new JSONObject();
		tag.put("class", "main.infra.adapters.operators.TemplateOperator");
		tag.put("name", "TemplateOperator");
		tag.put("params", params);
		return tag;
	}
	@Override
	public String getIconImage() {
		return "template.png";
	}
	
}
