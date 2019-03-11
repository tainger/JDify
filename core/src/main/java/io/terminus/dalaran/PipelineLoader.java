package io.terminus.dalaran;

import com.google.gson.Gson;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

public class PipelineLoader {

    // TODO 瞎写一下=.=
    public static void loadMessageFlows() throws Exception {
        DalaranComponentLoader.loadComponents();

        Gson gson = new Gson();
        Gson newGson = gson.newBuilder().
                registerTypeAdapter(Pipeline.Listener.class, new DalaranComponentTypeAdapter(gson, "listener")).
                registerTypeAdapter(Pipeline.Endpoint.class, new DalaranComponentTypeAdapter(gson, "endpoint")).create();

        ModelCamelContext camelContext = new DefaultCamelContext();
        camelContext.start();

        URL in = PipelineLoader.class.getResource("/dalaran");
        File file = new File(in.toURI());
        for (File messageFlowFile : file.listFiles()) {
            Reader reader = new InputStreamReader(new FileInputStream(messageFlowFile));
            Pipeline messageFlow = newGson.fromJson(reader, Pipeline.class);
            CamelRouteBuilder routeBuilder = new CamelRouteBuilder(messageFlow);
            camelContext.addRoutes(routeBuilder);
            System.out.println("load [" + messageFlowFile + "] ...");
        }
    }
}
