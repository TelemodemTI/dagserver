package main.domain.core;

import org.json.JSONObject;

import main.domain.exceptions.DomainException;
import main.infra.adapters.input.graphql.types.OperatorStage;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;

public abstract class BaseOperator extends OperatorStage {


    @Override
    public Implementation getDinamicInvoke(String stepName, String propkey, String optkey) throws DomainException {
        try {
            return MethodCall.invoke(DagExecutable.class.getDeclaredMethod("addOperator", String.class, Class.class, String.class)).with(stepName, getClass(), propkey);
        } catch (Exception e) {
            throw new DomainException(e.getMessage());
        }
    }

    @Override
    public JSONObject getMetadataOperator() {
        JSONObject par = new JSONObject();
        // Implementar en las subclases
        return generateMetadata(par, getClass().getName());
    }

    @Override
    public abstract String getIconImage();
}