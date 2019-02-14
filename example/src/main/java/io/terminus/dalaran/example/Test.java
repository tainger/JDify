package io.terminus.dalaran.example;

import com.google.gson.Gson;
import io.terminus.dalaran.*;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Test {

    public static void main(String[] args) throws Exception {
        DalaranComponentLoader.loadComponents();

        Gson gson = new Gson();
        Gson newGson = gson.newBuilder().
                registerTypeAdapter(MessageFlow.Listener.class, new DalaranComponentTypeAdapter(gson, "listener")).
                registerTypeAdapter(MessageFlow.Endpoint.class, new DalaranComponentTypeAdapter(gson, "endpoint")).create();
        InputStream in = Test.class.getResourceAsStream("/example-flow-dubbo.json");

        Reader reader = new InputStreamReader(in);
        MessageFlow messageFlow = newGson.fromJson(reader, MessageFlow.class);
        CamelRouteBuilder routeBuilder = new CamelRouteBuilder(messageFlow);
        ModelCamelContext camelContext = new DefaultCamelContext();
        camelContext.start();
        camelContext.addRoutes(routeBuilder);
        synchronized (Test.class) {
            Test.class.wait();
        }

    }

}
