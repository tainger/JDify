//package io.terminus.dalaran.component.processor.as2;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONPath;
//import org.apache.camel.Exchange;
//import org.apache.camel.Processor;
//import org.apache.camel.component.as2.api.AS2AsynchronousMDNManager;
//import org.apache.camel.component.as2.api.AS2Header;
//import org.apache.camel.component.as2.api.AS2MediaType;
//import org.apache.camel.component.as2.api.entity.*;
//import org.apache.camel.component.as2.api.util.AS2Utils;
//import org.apache.camel.component.as2.api.util.EntityUtils;
//import org.apache.camel.component.as2.api.util.HttpMessageUtils;
//import org.apache.commons.io.IOUtils;
//import org.apache.http.HttpEntityEnclosingRequest;
//import org.apache.http.HttpRequest;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpVersion;
//import org.apache.http.entity.ContentType;
//import org.apache.http.message.BasicHttpEntityEnclosingRequest;
//import org.apache.http.message.BasicHttpResponse;
//import org.apache.http.protocol.HttpCoreContext;
//import org.apache.http.protocol.HttpDateGenerator;
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
//
//import java.security.Security;
//import java.util.HashMap;
//import java.util.Map;
//
//public class MDNAsyncProcessor implements Processor {
//
//    private AS2ClientConfig config;
//
//    private static final String DISPOSITION_NOTIFICATION_OPTIONS = "signed-receipt-protocol=optional,pkcs7-signature; signed-receipt-micalg=optional,sha1";
//
//    private HttpDateGenerator DATE_GENERATOR = new HttpDateGenerator();
//
//    public MDNAsyncProcessor(AS2ClientConfig config) {
//        this.config = config;
//    }
//
//    @Override
//    public void process(Exchange exchange) throws Exception {
//        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKo++i9J9dzAFtbxwowKDCo2mxi7MXxE8A8VvssaydWjjgmEz/HHMPLOhi1182a1si4pWL0/MizKnquD7T2Bu4jpQbAFnkNYEMEyq/kw904Xl0JCQHYFuvnI99RE8Q3KlTP6kEUGDjV34EL6vBGJcQvArLtj1xoP8y0nIfJ2Pw5TAgMBAAECgYAGGB8IllMwxceLhjf6n1l0IWRH7FuHIUieoZ6k0p6rASHSgWiYNRMxfecbtX8zDAoG0QAWNi7rn40ygpR5gS1fWDAKhmnhKgQIT6wW0VmD4hraaeyP78iy8BLhlvblri2nCPIhDH5+l96v7D47ZZi3ZSOzcj89s1eS/k7/N4peEQJBAPEtGGJY+lBoCxQMhGyzuzDmgcS1Un1ZE2pt+XNCVl2b+T8fxWJH3tRRR8wOY5uvtPiK1HM/IjT0T5qwQeH8Yk0CQQC0tcv3d/bDb7bOe9QzUFDQkUSpTdPWAgMX2OVPxjdq3Sls9oA5+fGNYEy0OgyqTjde0b4iRzlD1O0OhLqPSUMfAkEAh5FIvqezdRU2/PsYSR4yoAdCdLdT+h/jGRVefhqQ/6eYUJJkWp15tTFHQX3pIe9/s6IeT/XyHYAjaxmevxAmlQJBAKSdhvQjf9KAjZKDEsa7vyJ/coCXuQUWSCMNHbcR5aGfXgE4e45UtUoIE1eKGcd6AM6LWhx3rR6xdFDpb9je8BkCQB0SpevGfOQkMk5i8xkEt9eeYP0fi8nv6eOUcK96EXbzs4jV2SAoQJ9oJegPtPROHbhIvVUmNQTbuP10Yjg59+8=";
//        ProvisionAS2ComponentCrypto provisionAS2ComponentCrypto = new ProvisionAS2ComponentCrypto();
//        Security.addProvider(new BouncyCastleProvider());
//        provisionAS2ComponentCrypto.configCertificateChain(config);
//        AS2AsynchronousMDNManager mdnManager = new AS2AsynchronousMDNManager("1.0.0", "Dalaran AS2Client", "client.test",
//                provisionAS2ComponentCrypto.getSigningCertificateChain(), provisionAS2ComponentCrypto.buildPrivateKey(privateKey));
//
//        Object inBody = exchange.getIn().getBody();
//        System.out.println("in body: " + inBody);
//        String ediMessage = JSON.toJSONString(JSONPath.eval(JSON.parseObject(IOUtils.toString((byte[])inBody, "utf-8")), "$.data"));
//        System.out.println("edi message: " + ediMessage);
//        ApplicationEDIEntity ediEntity = EntityUtils.createEDIEntity(ediMessage,
//                ContentType.create(AS2MediaType.APPLICATION_EDIFACT, "utf-8"), null, false);
//
//        HttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest("POST", "/");
//
//        HttpMessageUtils.setHeaderValue(request, AS2Header.SUBJECT, "test");
//        String httpdate = DATE_GENERATOR.getCurrentDate();
//        HttpMessageUtils.setHeaderValue(request, AS2Header.DATE, httpdate);
//        HttpMessageUtils.setHeaderValue(request, AS2Header.AS2_TO, "as2 to");
//        HttpMessageUtils.setHeaderValue(request, AS2Header.AS2_FROM, "as2 from");
//        String originalMessageId = AS2Utils.createMessageId("server.from");
//        HttpMessageUtils.setHeaderValue(request, AS2Header.MESSAGE_ID, originalMessageId);
//        HttpMessageUtils.setHeaderValue(request, AS2Header.DISPOSITION_NOTIFICATION_OPTIONS,
//                DISPOSITION_NOTIFICATION_OPTIONS);
//        EntityUtils.setMessageEntity(request, ediEntity);
//
//        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
//        httpdate = DATE_GENERATOR.getCurrentDate();
//        response.setHeader(AS2Header.DATE, httpdate);
//        response.setHeader(AS2Header.SERVER, "Server Responding with MDN");
//
//        Map<String, String> extensionFields = new HashMap<>();
//        extensionFields.put("Original-Recipient", "rfc822;" + "AS2 Name");
//        AS2DispositionModifier dispositionModifier = AS2DispositionModifier.createWarning("AS2 is cool!");
//        String[] failureFields = new String[] {"failure-field-1"};
//        String[] errorFields = new String[] {"error-field-1"};
//        String[] warningFields = new String[] {"warning-field-1"};
//        DispositionNotificationMultipartReportEntity mdn = new DispositionNotificationMultipartReportEntity(request,
//                response, DispositionMode.AUTOMATIC_ACTION_MDN_SENT_AUTOMATICALLY, AS2DispositionType.PROCESSED,
//                dispositionModifier, failureFields, errorFields, warningFields, extensionFields, null, "boundary",
//                true, provisionAS2ComponentCrypto.buildPrivateKey(privateKey));
//
//        HttpCoreContext httpContext = mdnManager.send(mdn, "http://" + config.getConnector().getHost() + ":" + config.getConnector().getPort() + config.getRequestUri());
//
//        HttpRequest mndRequest = httpContext.getRequest();
//        DispositionNotificationMultipartReportEntity reportEntity = HttpMessageUtils.getEntity(mndRequest,
//                DispositionNotificationMultipartReportEntity.class);
//
//        AS2MessageDispositionNotificationEntity mdnEntity = (AS2MessageDispositionNotificationEntity) reportEntity
//                .getPart(1);
//
//        AS2Utils.printRequest(httpContext.getRequest());
//
//        System.out.println(JSON.toJSONString(httpContext));
//
//        String body = IOUtils.toString(httpContext.getResponse().getEntity().getContent(), "utf-8");
//        System.out.println("=========================");
//        System.out.println("body: " + body);
//        System.out.println("=========================");
//        exchange.getOut().setBody(body);
//    }
//}
