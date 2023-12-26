package main.cl.dagserver.infra.adapters.confs;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextUtils implements ApplicationContextAware {
     
      private ApplicationContext ctx;
      
      @Override
      public void setApplicationContext(ApplicationContext appContext) {
        ctx = appContext;
      }
     
      public ApplicationContext getApplicationContext() {
        return ctx;
      }

}