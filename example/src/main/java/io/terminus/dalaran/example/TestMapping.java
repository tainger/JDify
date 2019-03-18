package io.terminus.dalaran.example;

import com.google.gson.Gson;
import io.terminus.dalaran.*;
import io.terminus.dalaran.message.FieldMapping;
import io.terminus.dalaran.message.MessageMapping;
import io.terminus.dalaran.message.ModelType;
import io.terminus.dalaran.utils.XmlUtils;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

/**
 * Created by jingdi on 2019/3/13
 */
public class TestMapping {

    public static void main(String[] args) throws Exception {

        DalaranComponentLoader.loadComponents();
        ModelCamelContext camelContext = new DefaultCamelContext();
        camelContext.start();
        RouteBuilder route = buildRoute("/dalaran/example-flow-mapper.json");
        camelContext.addRoutes(route);

        synchronized (Test.class) {
            Test.class.wait();
        }
    }

    private static RouteBuilder buildRoute(String json) {
        Gson gson = new Gson();
        Gson newGson = gson.newBuilder().
                registerTypeAdapter(DalaranTriggerConfig.class, new DalaranComponentTypeAdapter(gson, "trigger")).
                registerTypeAdapter(DalaranProcessorConfig.class, new DalaranComponentTypeAdapter(gson, "processors")).create();
        InputStream in = Test.class.getResourceAsStream(json);

        Reader reader = new InputStreamReader(in);
        DalaranFlow messageFlow = newGson.fromJson(reader, DalaranFlow.class);
        return new CamelRouteBuilder(messageFlow);
    }
}
