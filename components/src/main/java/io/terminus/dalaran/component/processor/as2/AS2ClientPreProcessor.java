package io.terminus.dalaran.component.processor.as2;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.as2.api.AS2CompressionAlgorithm;
import org.apache.camel.component.as2.api.AS2EncryptionAlgorithm;
import org.apache.camel.component.as2.api.AS2SignatureAlgorithm;

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
        exchange.getIn().setHeader("CamelAS2.signingCertificateChain", provisionAS2ComponentCrypto.getSigningCertificateChain().toArray());
//        exchange.getIn().setHeader("CamelAS2.signingPrivateKey", provisionAS2ComponentCrypto.getSigningKP().getPrivate());
        exchange.getIn().setHeader("CamelAS2.signedReceiptMicAlgorithms", Arrays.asList("sha1", "md5").toArray());
        exchange.getIn().setHeader("CamelAS2.encryptingAlgorithm",  AS2EncryptionAlgorithm.AES128_CBC);
        exchange.getIn().setHeader("CamelAS2.encryptingCertificateChain", provisionAS2ComponentCrypto.getEncryptingCertificateChain().toArray());
//        exchange.getIn().setHeader("CamelAS2.decryptingPrivateKey", provisionAS2ComponentCrypto.getDecryptingKP().getPrivate());
        exchange.getIn().setHeader("CamelAS2.compressionAlgorithm", AS2CompressionAlgorithm.ZLIB);
    }
}
