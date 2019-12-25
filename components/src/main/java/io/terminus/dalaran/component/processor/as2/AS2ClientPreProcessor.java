package io.terminus.dalaran.component.processor.as2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.as2.api.AS2CompressionAlgorithm;
import org.apache.camel.component.as2.api.AS2EncryptionAlgorithm;
import org.apache.camel.component.as2.api.AS2SignatureAlgorithm;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.security.cert.Certificate;
import java.util.Arrays;

public class AS2ClientPreProcessor implements Processor {

    private AS2ClientConfig config;

    public AS2ClientPreProcessor(AS2ClientConfig config) {
        this.config = config;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        ProvisionAS2ComponentCrypto provisionAS2ComponentCrypto = new ProvisionAS2ComponentCrypto();
        provisionAS2ComponentCrypto.configCertificateChain(config);
        exchange.getIn().setHeader("CamelAS2.signingAlgorithm", AS2SignatureAlgorithm.SHA512WITHRSA);
        exchange.getIn().setHeader("CamelAS2.signingCertificateChain", (Certificate[])provisionAS2ComponentCrypto.getSigningCertificateChain());
//        exchange.getIn().setHeader("CamelAS2.signingPrivateKey", provisionAS2ComponentCrypto.getSigningKP().getPrivate());
        exchange.getIn().setHeader("CamelAS2.signedReceiptMicAlgorithms", Arrays.asList("sha1", "md5").toArray());
        exchange.getIn().setHeader("CamelAS2.encryptingAlgorithm",  AS2EncryptionAlgorithm.AES128_CBC);
        exchange.getIn().setHeader("CamelAS2.encryptingCertificateChain", (Certificate[]) provisionAS2ComponentCrypto.getEncryptingCertificateChain());
//        exchange.getIn().setHeader("CamelAS2.decryptingPrivateKey", provisionAS2ComponentCrypto.getDecryptingKP().getPrivate());
        exchange.getIn().setHeader("CamelAS2.compressionAlgorithm", AS2CompressionAlgorithm.ZLIB);

        Object inBody = exchange.getIn().getBody();
        if (!(inBody instanceof byte[])) {
            byte[] bytes = IOUtils.toByteArray(JSON.toJSONString(JSONPath.eval(inBody, "$.data")));
            File temp = File.createTempFile("dalaran-" + System.currentTimeMillis(), ".edi");
            FileUtils.writeByteArrayToFile(temp, bytes);
            exchange.getOut().setBody(new FileInputStream(temp));
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        } else {
            byte[] bytes = IOUtils.toByteArray(JSON.toJSONString(JSONPath.eval(JSON.parseObject(IOUtils.toString((byte[])inBody, "utf-8")), "$.data")));
            File temp = File.createTempFile("dalaran-" + System.currentTimeMillis(), ".edi");
            FileUtils.writeByteArrayToFile(temp, bytes);
            exchange.getOut().setBody(new FileInputStream(temp));
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        }
    }
}
