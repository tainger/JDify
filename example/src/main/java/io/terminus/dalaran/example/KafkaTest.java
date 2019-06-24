package io.terminus.dalaran.example;

import com.alibaba.fastjson.JSON;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.rabbitmq.RabbitMQProducer;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.commons.collections.CollectionUtils;

import java.io.File;
import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by jingdi on 2019/6/12
 */
public class KafkaTest {

    public static void main(String[] args) {

        String origin = "[{\"user\":{\"id\":2, \"name\":\"momo\", \"phone\":\"10086\", \"address\":\"mmmmmm\", \"wechat\":\"9999\"}, \"order\":{\"id\":\"11001\", \"time\":\"00:00\", \"detail\":\"asdfghjkl\", \"user\":\"momo\", \"address\":[{\"addr1\":\"mmmm\", \"addr2\":\"llllll\", \"list\":[{\"itemA\":\"11111\", \"itemB\":\"2222222\"}]}, {\"addr1\":\"pppppp\"}]}}]";

        Object o = JSON.parse(origin);

//        Object o = (Object) origin;
        System.out.println(o.getClass().getTypeName());

        List list = (List) o;
        if (CollectionUtils.isNotEmpty(list)) {
            Object child = list.get(0);
            System.out.println(child.getClass().getTypeName());
        }

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
