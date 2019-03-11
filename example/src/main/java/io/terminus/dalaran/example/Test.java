package io.terminus.dalaran.example;

import com.google.gson.Gson;
import io.terminus.dalaran.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Test {

    public static void main(String[] args) throws Exception {
        DalaranComponentLoader.loadComponents();
        ModelCamelContext camelContext = new DefaultCamelContext();
        camelContext.start();
        RouteBuilder route1 = buildRoute("/example-flow.json");
        camelContext.addRoutes(route1);

        Thread.sleep(60000);

        RouteBuilder route2 = buildRoute("/example-flow-other.json");
        camelContext.addRoutes(route2);

        Thread.sleep(60000);
        
        camelContext.stopRoute(route1.getRouteCollection().getRoutes().get(0).getId());

        synchronized (Test.class) {
            Test.class.wait();
        }
    }

    private static RouteBuilder buildRoute(String json) {
        Gson gson = new Gson();
        Gson newGson = gson.newBuilder().
                registerTypeAdapter(Pipeline.Listener.class, new DalaranComponentTypeAdapter(gson, "listener")).
                registerTypeAdapter(Pipeline.Endpoint.class, new DalaranComponentTypeAdapter(gson, "endpoint")).create();
        InputStream in = Test.class.getResourceAsStream(json);

        Reader reader = new InputStreamReader(in);
        Pipeline messageFlow = newGson.fromJson(reader, Pipeline.class);
        return new CamelRouteBuilder(messageFlow);
    }
}
