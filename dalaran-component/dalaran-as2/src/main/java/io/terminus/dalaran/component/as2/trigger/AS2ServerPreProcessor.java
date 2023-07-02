//package io.terminus.dalaran.component.as2.trigger;
//
//import org.apache.camel.Exchange;
//import org.apache.camel.Processor;
//import org.apache.camel.component.as2.api.util.AS2Utils;
//import org.apache.http.message.BasicHttpRequest;
//import org.apache.http.protocol.BasicHttpContext;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Arrays;
//
//public class AS2ServerPreProcessor implements Processor {
//
//    private Logger logger = LoggerFactory.getLogger(AS2ServerPreProcessor.class);
//
//    @Override
//    public void process(Exchange exchange) throws Exception {
//        logger.info(exchange.getIn().getBody().getClass().getName());
//        BasicHttpContext httpContext = exchange.getIn().getBody(BasicHttpContext.class);
//        logger.info("context: " + httpContext.toString());
//        logger.info(httpContext.getAttribute("http.request").getClass().getName());
//
////        BasicHttpEntityEnclosingRequest request = (BasicHttpEntityEnclosingRequest) httpContext.getAttribute("http.request");
//
//        BasicHttpRequest request = (BasicHttpRequest) httpContext.getAttribute("http.request");
//
//        logger.info("headers: " + Arrays.toString(request.getAllHeaders()));
//        logger.info("request line: " + request.getRequestLine().toString());
//        logger.info("params: " + request.getParams().toString());
//
//        AS2Utils.printRequest(request);
//        logger.info("====================");
////        String body = IOUtils.toString(request.getEntity().getContent());
//        String body = request.toString();
//        logger.info(body);
//    }
//}
