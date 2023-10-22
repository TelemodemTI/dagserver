package main.cl.dagserver.domain.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.TYPE)
public @interface Dag {
	String name();
	String cronExpr() default "";
	String group();
	String onEnd() default "";
	String onStart() default "";
}
