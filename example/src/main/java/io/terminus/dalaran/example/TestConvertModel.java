package io.terminus.dalaran.example;

import com.google.gson.Gson;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jacksonxml.JacksonXMLDataFormat;
import org.apache.camel.component.jacksonxml.ListJacksonXMLDataFormat;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.dataformat.XmlJsonDataFormat;
import org.apache.camel.spi.DataFormat;
import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jingdi on 2019/3/19
 */
public class TestConvertModel {

    public static void main(String[] args) throws Exception {

//        CamelContext context = new DefaultCamelContext();
//
//        context.addRoutes(new RouteBuilder() {
//            @Override
//            public void configure() throws Exception {
                XmlJsonDataFormat xmlJsonFormat = new XmlJsonDataFormat();
                xmlJsonFormat.setForceTopLevelObject(true);
//                String in = "/Users/jingdi/work/terminus-work/dalaran-2.0/develop/dalaran/example/src/main/resources/in";
//                String out = "/Users/jingdi/work/terminus-work/dalaran-2.0/develop/dalaran/example/src/main/resources/out";
//                from("file:" + in).marshal(xmlJsonFormat).convertBodyTo(String.class).to("file:"  + out + "?autoCreate=true");



//                JacksonXMLDataFormat format = new JacksonXMLDataFormat();
//                format.setXmlMapper();
//                from("file:" + in).unmarshal(format).convertBodyTo(String.class).to("file:"  + out + "?autoCreate=true");



//                DataFormat jaxb =  new JaxbDataFormat();
//                from("file:" + in).unmarshal(jaxb).convertBodyTo(String.class).to("file:"  + out + "?autoCreate=true");


//                from("file:" + in).marshal().xmlBeans().convertBodyTo(String.class).to("file:"  + out + "?autoCreate=true");

//            }
//        });
//        context.start();
//        Thread.sleep(5000);
//        context.stop();

        test();
    }

    public static class MyBeanFactory extends AbstractFactory{
        @Override
        public boolean createObject(JXPathContext context, Pointer pointer,
                                    Object parent, String name, int index){
            if(parent instanceof Map) {
                Map child = new HashMap();
                ((Map<String, Object>)parent).put(name, child);
            }
            return true;
        }
    }

    private static void test() {

//        String s = "{mapping={class-a=org.apache.camel.component.dozer., class-b=io.terminus.dalaran.example.ExtOrderItem, field={custom-converter-id=_expressionMapping, custom-converter-param=simple:\\${header.Content-Type}, a=expression, b=test}}, schemaLocation=http://dozermapper.github.io/schema/bean-mapping http://dozermapper.github.io/schema/bean-mapping.xsd}";
        String s = "{\"mappings\":{\"@xmlns\":\"http://dozermapper.github.io/schema/bean-mapping\",\"@xmlns:xsi\":\"http://www.w3.org/2001/XMLSchema-instance\",\"@xsi:schemaLocation\":\"http://dozermapper.github.io/schema/bean-mapping http://dozermapper.github.io/schema/bean-mapping.xsd\",\"mapping\":{\"class-a\":\"org.apache.camel.component.dozer.\",\"class-b\":\"io.terminus.dalaran.example.ExtOrderItem\",\"field\":[{\"@custom-converter-id\":\"_expressionMapping\",\"@custom-converter-param\":\"simple:\\\\${header.name}\",\"a\":\"expression\",\"b\":\"itemName\"},{\"@custom-converter-id\":\"_expressionMapping\",\"@custom-converter-param\":\"simple:\\\\${body.price}\",\"a\":\"expression\",\"b\":\"itemPrice\"},{\"@custom-converter-id\":\"_expressionMapping\",\"@custom-converter-param\":\"simple:\\\\${header.Content-Type}\",\"a\":\"expression\",\"b\":\"test\"}]}}}";
        Gson gson = new Gson();
        Map<String, Object> targetBody = gson.fromJson(s, Map.class);

        System.out.println(targetBody);

        Map<String, Object> inMessage = new HashMap<>();
        inMessage.put("A", "a");
        inMessage.put("B", "b");
        inMessage.put("C", "c");

        Map<String, Object> sec = new HashMap<>();
        sec.put("name", "momo");
        sec.put("number", 123456);
        sec.put("addr", "baker street");

        Map<String, Object> third = new HashMap<>();
        third.put("sport", "swim");
        third.put("book", "novel");

        sec.put("love", third);

        inMessage.put("FM", sec);

        Map<String, Object> outMessage = new HashMap<>();
//        myMap.setFirst(first);
        JXPathContext context = JXPathContext.newContext(inMessage);


        JXPathContext outContext = JXPathContext.newContext(outMessage);
        outContext.setFactory(new MyBeanFactory());


//        outContext.createPath("a/b");
        outContext.createPathAndSetValue("a/b", context.getValue("FM/love/book"));
        outContext.createPathAndSetValue("a/c", context.getValue("FM/love/sport"));

//       exchange.out.setBody(outContext.getBean())
        System.out.println(outContext.getContextBean());

    }



}
