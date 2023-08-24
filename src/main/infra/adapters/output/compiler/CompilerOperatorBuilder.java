package main.infra.adapters.output.compiler;

import org.json.JSONArray;
import org.springframework.stereotype.Component;

import main.domain.core.DagExecutable;
import main.domain.core.OperatorStage;
import main.domain.exceptions.DomainException;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.Implementation.Composable;

@Component
public class CompilerOperatorBuilder {

	@SuppressWarnings("unchecked")
	public Implementation build(String jarname,JSONArray boxes) throws DomainException {
		try {
			Composable implementation = MethodCall.invoke(DagExecutable.class.getConstructor());
			for (int i = 0; i < boxes.length(); i++) {
				var box = boxes.getJSONObject(i);
				String typeope = box.getString("type");
				String idope = box.getString("id");
				Class<OperatorStage> cls = (Class<OperatorStage>) Class.forName(typeope);	
				Composable composable = (Composable) cls.getDeclaredConstructor().newInstance().getDinamicInvoke(idope,jarname+"."+idope+"."+typeope+".props",jarname+"."+idope+"."+typeope+".opts");
				implementation = implementation.andThen(composable);
			}
			for (int i = 0; i < boxes.length(); i++) {
				var box = boxes.getJSONObject(i);
				String idope = box.getString("id");
				String status = box.getString("status");
				if(box.has("source")) {
					String target = box.getJSONObject("source").getJSONObject("attrs").getJSONObject("label").getString("text");
					Composable composable = this.getDependencies(idope, target, status);
					implementation = implementation.andThen(composable);
				}
			}
			return implementation;	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	
	private Composable getDependencies(String source,String target, String status) throws DomainException {
		try {
			Implementation implementation = MethodCall.invoke(DagExecutable.class.getDeclaredMethod("addDependency", String.class, String.class , String.class )).with(source,target,status);
			return (Composable) implementation;	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
}
