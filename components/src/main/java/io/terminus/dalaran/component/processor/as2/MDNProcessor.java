//package io.terminus.dalaran.component.processor.as2;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONPath;
//import org.apache.camel.Exchange;
//import org.apache.camel.Processor;
//import org.apache.camel.component.as2.api.*;
//import org.apache.camel.component.as2.api.util.AS2Utils;
//import org.apache.commons.io.IOUtils;
//import org.apache.http.entity.ContentType;
//import org.apache.http.protocol.HttpCoreContext;
//
//public class MDNProcessor implements Processor {
//
//    private static final String METHOD = "POST";
//    private static final String TARGET_HOST = "localhost";
//    private static final int TARGET_PORT = 4080;
//    private static final String RECIPIENT_DELIVERY_ADDRESS = "http://localhost:" + TARGET_PORT + "/handle-receipts";
//    private static final String AS2_VERSION = "1.1";
//    private static final String USER_AGENT = "Camel AS2 Endpoint";
//    private static final String REQUEST_URI = "/";
//    private static final String AS2_NAME = "878051556";
//    private static final String SUBJECT = "Test Case";
//    private static final String FROM = "mrAS@example.org";
//    private static final String CLIENT_FQDN = "client.example.org";
//    private static final String SERVER_FQDN = "server.example.org";
//    private static final String REPORTING_UA = "Server Responding with MDN";
//    private static final String DISPOSITION_NOTIFICATION_TO = "mrAS@example.org";
//    private static final String DISPOSITION_NOTIFICATION_OPTIONS = "signed-receipt-protocol=optional,pkcs7-signature; signed-receipt-micalg=optional,sha1";
//    private static final String[] SIGNED_RECEIPT_MIC_ALGORITHMS = new String[] {"sha1", "md5"};
//
//    private AS2ClientConfig config;
//
//    public MDNProcessor(AS2ClientConfig config) {
//        this.config = config;
//    }
//
//    @Override
//    public void process(Exchange exchange) throws Exception {
//        Object inBody = exchange.getIn().getBody();
//        System.out.println("in body: " + inBody);
//        String ediMessage = JSON.toJSONString(JSONPath.eval(JSON.parseObject(IOUtils.toString((byte[])inBody, "utf-8")), "$.data"));
//        System.out.println("edi message: " + ediMessage);
//        AS2ClientConnection clientConnection = new AS2ClientConnection("AS2_VERSION", USER_AGENT, CLIENT_FQDN,
//                config.getConnector().getHost(), config.getConnector().getPort());
//        AS2ClientManager clientManager = new AS2ClientManager(clientConnection);
//
//        HttpCoreContext httpContext = clientManager.send(ediMessage, config.getRequestUri(), SUBJECT, FROM, AS2_NAME, AS2_NAME,
//                AS2MessageStructure.PLAIN, ContentType.create(AS2MediaType.APPLICATION_EDIFACT, "utf-8"),
//                null, null, null, null, null, DISPOSITION_NOTIFICATION_TO, SIGNED_RECEIPT_MIC_ALGORITHMS, null, null);
//        AS2Utils.printRequest(httpContext.getRequest());
//
//
//    }
//}
