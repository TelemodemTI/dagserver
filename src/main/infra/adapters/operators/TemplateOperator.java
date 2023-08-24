package main.infra.adapters.operators;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.json.JSONObject;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import main.domain.annotations.Operator;
import main.domain.core.MetadataManager;
import main.domain.core.OperatorStage;
import main.domain.exceptions.DomainException;

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
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.infra.adapters.operators.TemplateOperator");
		metadata.setParameter("template", "sourcecode");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "template.png";
	}
	
}
