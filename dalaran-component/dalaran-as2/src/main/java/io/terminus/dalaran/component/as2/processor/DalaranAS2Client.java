//package io.terminus.dalaran.component.as2.processor;
//
//import io.terminus.dalaran.DalaranConstants;
//import io.terminus.dalaran.component.as2.trigger.CertificateConfig;
//import io.terminus.dalaran.core.component.DalaranProcessor;
//import io.terminus.dalaran.core.component.annotation.Processor;
//import io.terminus.dalaran.core.oss.OSSAccount;
//import org.apache.camel.CamelContext;
//import org.apache.camel.builder.Builder;
//import org.apache.camel.component.as2.AS2Component;
//import org.apache.camel.component.as2.AS2Configuration;
//import org.apache.camel.component.as2.api.*;
//import org.apache.camel.model.ProcessorDefinition;
//import org.apache.http.entity.ContentType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import java.nio.charset.StandardCharsets;
//import java.security.PrivateKey;
//import java.security.cert.Certificate;
//
//@Processor(
//        value = "as2-client",
//        order = 18,
//        configType = AS2ClientConfig.class,
//        bodyType = "JSON",
//        developer = DalaranConstants.DALARAN
//)
//public class DalaranAS2Client implements DalaranProcessor<AS2ClientConfig> {
//
//    private Logger logger = LoggerFactory.getLogger(DalaranAS2Client.class);
//
//    private PrivateKey privateKey;
//
//    @Autowired
//    private OSSAccount ossAccount;
//
//    @Autowired
//    private CamelContext camelContext;
//
//    private static final String AS2_CLIENT_URI = "as2://client/send?inBody=%s&targetHostname=%s&requestUri=%s&targetPortNumber=%s&synchronous=true";
//
//    @Override
//    public void configure(ProcessorDefinition route, AS2ClientConfig config) {
//        CertificateConfig certificateConfig = new CertificateConfig();
//        try {
//            certificateConfig.configCertificates(config.getConnector(), ossAccount);
//            init(certificateConfig);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String uri = String.format(AS2_CLIENT_URI, config.getBodyType(), config.getConnector().getHost(), config.getRequestUri(), config.getConnector().getPort());
//        route.setHeader("CamelAS2.ediMessageContentType", Builder.constant(ContentType.create(AS2MediaType.APPLICATION_EDIFACT, StandardCharsets.UTF_8)));
//        route.setHeader("CamelAS2.ediMessageTransferEncoding", Builder.constant("7bit"));
//        route.setHeader("CamelAS2.requestUri", Builder.constant(config.getRequestUri()));
//        route.setHeader("CamelAS2.from", Builder.constant("mrAS@terminus.org"));
//        route.setHeader("CamelAS2.as2From", Builder.constant("JJYTestEnv"));
//        route.setHeader("CamelAS2.as2To", Builder.constant("PGTEnt"));
//        route.setHeader("CamelAS2.dispositionNotificationTo", Builder.constant("mrAS@terminus.org"));
//        route.setHeader("CamelAS2.subject", Builder.constant("Signed AS2 Message"));
//        route.setHeader("CamelAS2.as2MessageStructure", Builder.constant(AS2MessageStructure.SIGNED_ENCRYPTED));
//        route.setHeader("CamelAS2.synchronous", Builder.constant(true));
//
//        String[] signedReceiptMicAlgorithms = new String[]{"sha-256"};
//        route.setHeader("CamelAS2.signingAlgorithm", Builder.constant(AS2SignatureAlgorithm.SHA256WITHRSA));
//        route.setHeader("CamelAS2.signingCertificateChain", Builder.constant(certificateConfig.getSigningCertificateChain()));
//        route.setHeader("CamelAS2.signingPrivateKey", Builder.constant(certificateConfig.getKeyPair().getPrivate()));
//        route.setHeader("CamelAS2.signedReceiptMicAlgorithms", Builder.constant(signedReceiptMicAlgorithms));
//        route.setHeader("CamelAS2.encryptingAlgorithm",  Builder.constant(AS2EncryptionAlgorithm.DES_CBC));
//        route.setHeader("CamelAS2.encryptingCertificateChain", Builder.constant(certificateConfig.getEncryptingCertificateChain()));
//        route.setHeader("CamelAS2.decryptingPrivateKey", Builder.constant(certificateConfig.getKeyPair().getPrivate()));
//        route.setHeader("CamelAS2.compressionAlgorithm", Builder.constant(AS2CompressionAlgorithm.ZLIB));
//
//        route.process(new AS2ClientPreProcessor(certificateConfig));
//        route.to(uri);
//        route.process(new AS2ClientDataProcessor());
//    }
//
//    private void init(CertificateConfig certificateConfig) {
//        privateKey = certificateConfig.getKeyPair().getPrivate();
//        String[] signedReceiptMicAlgorithms = new String[]{"sha256"};
//
//        AS2Component as2Component = (AS2Component)camelContext.getComponent("as2");
//        AS2Configuration configuration = new AS2Configuration();
//        configuration.setDecryptingPrivateKey(privateKey);
//        configuration.setEncryptingAlgorithm(AS2EncryptionAlgorithm.DES_CBC);
//        configuration.setEncryptingCertificateChain(certificateConfig.getEncryptingCertificateChain().toArray(new Certificate[1]));
//        configuration.setSigningAlgorithm(AS2SignatureAlgorithm.SHA256WITHRSA);
//        configuration.setSigningCertificateChain(certificateConfig.getSigningCertificateChain().toArray(new Certificate[1]));
//        configuration.setSignedReceiptMicAlgorithms(signedReceiptMicAlgorithms);
//        configuration.setSigningPrivateKey(privateKey);
//        configuration.setServerFqdn("JJYTestEnv");
//        configuration.setServer("JJYTestEnv");
//        as2Component.setConfiguration(configuration);
//    }
//}