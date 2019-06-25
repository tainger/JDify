package io.terminus.dalaran.example;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;

/**
 * Created by jingdi on 2019/6/12
 */
public class KafkaTest {

    public static void main(String[] args) {
        CamelContext context = new DefaultCamelContext();
        RouteDefinition route = new RouteDefinition();

        String listener = "netty4-http:http" +
                "://0.0.0.0:8082/kafka/send"+
                "?httpMethodRestrict=POST";

        String producer = "kafka:dalaran"
                + "?brokers=127.0.0.1:9092&serializerClass=io.terminus.dalaran.example.DalaranSerializer";

        String consumer = "kafka:dalaran"
                + "?brokers=127.0.0.1:9092"
                + "&groupId=dalaran&valueDeserializer=io.terminus.dalaran.example.DalaranDerializer";

        route.from(listener).unmarshal().json(JsonLibrary.Fastjson).to("log:from===========").process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Object body = exchange.getIn().getBody();
                System.out.println(body);
            }
        }).to(producer).process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Object body = exchange.getIn().getBody();
                System.out.println(body);
            }
        }).convertBodyTo(String.class).to("log:showBody?showAll=true");

        try {
            context.addRouteDefinition(route);
            context.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        route.from(consumer).to("log:consumer");

    }
}
