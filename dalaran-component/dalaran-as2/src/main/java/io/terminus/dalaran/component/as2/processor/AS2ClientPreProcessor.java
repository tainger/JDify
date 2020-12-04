package io.terminus.dalaran.component.as2.processor;

import io.terminus.dalaran.component.as2.trigger.CertificateConfig;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.as2.api.AS2CompressionAlgorithm;
import org.apache.camel.component.as2.api.AS2EncryptionAlgorithm;
import org.apache.camel.component.as2.api.AS2SignatureAlgorithm;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class AS2ClientPreProcessor implements Processor {

    private CertificateConfig certificateConfig;

    public AS2ClientPreProcessor(CertificateConfig certificateConfig) {
        this.certificateConfig = certificateConfig;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String[] signedReceiptMicAlgorithms = new String[]{"sha256"};
        exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        exchange.getOut().setHeader("CamelAS2.signingAlgorithm", AS2SignatureAlgorithm.SHA256WITHRSA);
        exchange.getOut().setHeader("CamelAS2.signingCertificateChain", certificateConfig.getSigningCertificateChain());
        exchange.getOut().setHeader("CamelAS2.signingPrivateKey", certificateConfig.getKeyPair().getPrivate());
        exchange.getOut().setHeader("CamelAS2.signedReceiptMicAlgorithms", signedReceiptMicAlgorithms);
        exchange.getOut().setHeader("CamelAS2.encryptingAlgorithm",  AS2EncryptionAlgorithm.DES_CBC);
        exchange.getOut().setHeader("CamelAS2.encryptingCertificateChain", certificateConfig.getEncryptingCertificateChain());
        exchange.getOut().setHeader("CamelAS2.decryptingPrivateKey", certificateConfig.getKeyPair().getPrivate());
        exchange.getOut().setHeader("CamelAS2.compressionAlgorithm", AS2CompressionAlgorithm.ZLIB);
        exchange.getOut().setHeader(exchange.getExchangeId(), exchange.getIn().getHeader(exchange.getExchangeId()));

        Object inBody = exchange.getIn().getBody();
        if (!(inBody instanceof byte[])) {
//            byte[] bytes = JSONPath.eval(JSON.parseObject(inBody.toString()), "$.data").toString().getBytes();
//            File temp = File.createTempFile("dalaran-as2-" + System.currentTimeMillis(), ".edi");
//            System.out.println(temp.getName());
//            FileUtils.writeByteArrayToFile(temp, bytes);
//            String message = JSONPath.eval(JSON.parseObject(inBody.toString()), "$.data").toString();
            String message = (String) inBody;
            System.out.println(message);
            exchange.getOut().setBody(message);
//            System.out.println(inBody);
//            System.out.println(inBody.toString());
//            exchange.getOut().setBody(inBody.toString());
        } else {
            String message = IOUtils.toString((byte[]) inBody, "UTF-8");
            if (StringUtils.startsWithIgnoreCase(message, "\"")) {
                message = StringUtils.removeFirst(message, "\"");
            }
            if (StringUtils.endsWithIgnoreCase(message, "\"")) {
                message = StringUtils.removeEnd(message, "\"");
            }
            System.out.println(message);
            exchange.getOut().setBody(message);
        }
    }
}
