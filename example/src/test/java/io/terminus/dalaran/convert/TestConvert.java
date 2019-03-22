package io.terminus.dalaran.convert;

import com.google.gson.Gson;
import io.terminus.dalaran.DalaranContext;
import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.dataformat.XmlJsonDataFormat;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/3/21
 */
public class TestConvert extends CamelTestSupport {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                String out = "/Users/jingdi/work/terminus-work/dalaran-2.0/develop/dalaran/example/src/test/resources/out";
                String in = "/Users/jingdi/work/terminus-work/dalaran-2.0/develop/dalaran/example/src/test/resources/in";
                org.apache.camel.dataformat.xmljson.XmlJsonDataFormat xmlJsonFormat = new org.apache.camel.dataformat.xmljson.XmlJsonDataFormat();

                List<org.apache.camel.dataformat.xmljson.XmlJsonDataFormat.NamespacesPerElementMapping> namespaces = new ArrayList<org.apache.camel.dataformat.xmljson.XmlJsonDataFormat.NamespacesPerElementMapping>();
                namespaces.add(new org.apache.camel.dataformat.xmljson.XmlJsonDataFormat.NamespacesPerElementMapping("mappings",
                        "|xsi|http://www.w3.org/2001/XMLSchema-instance||http://dozermapper.github.io/schema/bean-mapping|"));
                xmlJsonFormat.setNamespaceMappings(namespaces);
                xmlJsonFormat.setRootName("mappings");
                xmlJsonFormat.setEncoding("UTF-8");
                xmlJsonFormat.setForceTopLevelObject(true);
                xmlJsonFormat.setTrimSpaces(true);

                from("file:" + in).unmarshal(xmlJsonFormat).convertBodyTo(String.class).to("file:" + out);
            }
        });
        context.start();
        Thread.sleep(3000);
        context.stop();
    }
}
