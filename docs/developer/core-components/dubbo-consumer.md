##Dubbo扩展——Consumer

如果Camel库中没有我们需要的触发器/处理器，那么就要自己去扩展。

简单来说就是重写Camel默认实现的相关基类方法以满足我们的业务需求。



###DubboCamelConsumer

```java
public class DubboCamelConsumer extends DefaultConsumer {
    
      private final DubboEndpoint endpoint;
    
        private final ServiceConfig provider;
    
        public DubboCamelConsumer(DubboEndpoint endpoint, Processor processor) {
            super(endpoint, processor);
            this.endpoint = endpoint;
            this.provider = createProvider();
        }
    
        @Override
        public void doStart() throws Exception {
            super.doStart();
            provider.export();
        }
    
        @Override
        protected void doStop() throws Exception {
            super.doStop();
            provider.unexport();
        }
}

```

###DubboCamelProcessor
```java
public class DubboCamelProcessor extends DefaultProducer {
       private final GenericService genericService;
    
        private final String method;
        private final String[] parameterTypes;
    
        public DubboCamelProcessor(DubboEndpoint endpoint) {
            super(endpoint);
            this.genericService = endpoint.getGenericService();
            this.method = endpoint.getMethod();
            this.parameterTypes = endpoint.getParameterTypes().toArray(new String[0]);
        }
    
        @Override
        public void process(Exchange exchange) {
            Object[] args = null;
            try {
                args = exchange.getIn().getBody(Object[].class);
            } catch (ClassCastException e) {
            }
            Object result = genericService.$invoke(method, parameterTypes, args);
            exchange.getOut().setBody(result);
        }
      
}
```

###DubboComponent
```java
public class DubboComponent extends DefaultComponent {

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) {
        System.out.println("createEndpoint start");
        return new DubboEndpoint();
    }

    @Override
    protected void doStart() throws Exception {
        System.out.println("DubboComponent start");
        super.doStart();
        System.out.println("DubboComponent done");
    }
}
```

###DubboEndpoint

```java
public class DubboEndpoint extends ProcessorEndpoint {
     @Override
     protected Processor createProcessor() {
         return new DubboCamelProcessor(this); 
     }
    
     @Override
     public Consumer createConsumer(Processor processor) throws Exception {
         System.out.println("createConsumer start");
         return new DubboCamelConsumer(this, processor); 
     }
        
     @Override
     protected String createEndpointUri() {
         return "dubbo://" + serviceId + "." + method; 
     }
     @Override
     public String getVersion() {
         return version; 
     }

}
```

###DubboMessage

```java
public class DubboMessage extends DefaultMessage {
}
```