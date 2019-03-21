package io.terminus.dalaran.convert;

import com.google.gson.Gson;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.XmlJsonDataFormat;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.Map;

/**
 * Created by jingdi on 2019/3/21
 */
public class TestConvert extends CamelTestSupport {

    @Test
    public void convert2XML() {
        Gson gson = new Gson();
        String str = "{\"mappings\":{\"@xmlns\":\"http://dozermapper.github.io/schema/bean-mapping\",\"@xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\",\"@xsi:schemaLocation\":\"http://dozermapper.github.io/schema/bean-mapping http://dozermapper.github.io/schema/bean-mapping.xsd\",\"mapping\":{\"class-a\":\"org.apache.camel.component.dozer.\",\"class-b\":\"io.terminus.dalaran.example.ExtOrderItem\",\"field\":[{\"@custom-converter-id\":\"_expressionMapping\",\"@custom-converter-param\":\"simple:\\\\${header.name}\",\"a\":\"expression\",\"b\":\"itemName\"},{\"@custom-converter-id\":\"_expressionMapping\",\"@custom-converter-param\":\"simple:\\\\${body.price}\",\"a\":\"expression\",\"b\":\"itemPrice\"},{\"@custom-converter-id\":\"_expressionMapping\",\"@custom-converter-param\":\"simple:\\\\${header.Content-Type}\",\"a\":\"expression\",\"b\":\"test\"}]}}}";
        template.sendBody("direct:start", gson.fromJson(str, Map.class));
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                context.setTracing(true);
                String out = "/Users/jingdi/work/terminus-work/dalaran-2.0/develop/dalaran/example/src/test/resources/out";
//                XmlJsonDataFormat xmlJsonFormat = new XmlJsonDataFormat();
//                xmlJsonFormat.setForceTopLevelObject(true);
//                xmlJsonFormat.setEncoding("UTF-8");
//                xmlJsonFormat.setForceTopLevelObject(true);
//                xmlJsonFormat.setTrimSpaces(true);
//                xmlJsonFormat.setRootName("mappings");
//                xmlJsonFormat.setSkipNamespaces(true);
//                xmlJsonFormat.setRemoveNamespacePrefixes(true);
//                from("direct:start").unmarshal(xmlJsonFormat).to("file:" + out);
                from("direct:start").unmarshal().xmlBeans().convertBodyTo(String.class).to("file:" + out);

            }
        };
    }
}
