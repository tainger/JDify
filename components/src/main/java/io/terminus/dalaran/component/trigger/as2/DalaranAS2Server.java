package io.terminus.dalaran.component.trigger.as2;

import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.Trigger;
import io.terminus.dalaran.core.oss.OSSAccount;
import org.apache.camel.CamelContext;
import org.apache.camel.component.as2.AS2Component;
import org.apache.camel.component.as2.AS2Configuration;
import org.apache.camel.component.as2.api.AS2CompressionAlgorithm;
import org.apache.camel.component.as2.api.AS2EncryptionAlgorithm;
import org.apache.camel.component.as2.api.AS2SignatureAlgorithm;
import org.apache.camel.model.RouteDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.PrivateKey;
import java.security.cert.Certificate;

import static org.apache.camel.builder.Builder.constant;

@Trigger(
        value = "as2-server",
        order = 19,
        configType = AS2ServerConfig.class
)
public class DalaranAS2Server implements DalaranTrigger<AS2ServerConfig> {

    private static final String AS2_SERVER_URI = "as2://server/listen?requestUri=%s&serverPortNumber=%s&requestUriPattern=%s";

    private PrivateKey privateKey;

    @Autowired
    private OSSAccount ossAccount;

    @Autowired
    private CamelContext camelContext;

    @Override
    public void buildFromRoute(RouteDefinition route, AS2ServerConfig config) {
        String uri = String.format(AS2_SERVER_URI, config.getRequestUri(), config.getPort(), config.getUriPattern());
        try {
            init(route, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        route.from(uri);
        route.process(new AS2ServerDataProcessor(privateKey));
    }

    private void init(RouteDefinition route, AS2ServerConfig config) throws Exception {
        CertificateConfig certificateConfig = new CertificateConfig();
        certificateConfig.configCertificates(config, ossAccount);

        privateKey = certificateConfig.getKeyPair().getPrivate();

        String[] signedReceiptMicAlgorithms = new String[]{"sha256"};
        route.setHeader("CamelAS2.signingAlgorithm", constant(AS2SignatureAlgorithm.SHA256WITHRSA));
        route.setHeader("CamelAS2.signingCertificateChain", constant(certificateConfig.getSigningCertificateChain().toArray()));
        route.setHeader("CamelAS2.signingPrivateKey", constant(privateKey));
        route.setHeader("CamelAS2.signedReceiptMicAlgorithms",  constant(signedReceiptMicAlgorithms));
        route.setHeader("CamelAS2.encryptingAlgorithm",  constant(AS2EncryptionAlgorithm.DES_CBC));
        route.setHeader("CamelAS2.encryptingCertificateChain", constant(certificateConfig.getEncryptingCertificateChain().toArray()));
        route.setHeader("CamelAS2.decryptingPrivateKey", constant(privateKey));
        route.setHeader("CamelAS2.compressionAlgorithm", constant(AS2CompressionAlgorithm.ZLIB));
        route.setHeader("CamelAS2.receiptDeliveryOption", constant("http://etransportps-test.ext.pg.com:4080/exchange/PGTEnt"));

        AS2Component as2Component = (AS2Component)camelContext.getComponent("as2");
        AS2Configuration configuration = new AS2Configuration();
        configuration.setDecryptingPrivateKey(privateKey);
        configuration.setEncryptingAlgorithm(AS2EncryptionAlgorithm.DES_CBC);
        configuration.setEncryptingCertificateChain(certificateConfig.getEncryptingCertificateChain().toArray(new Certificate[1]));
        configuration.setSigningAlgorithm(AS2SignatureAlgorithm.SHA256WITHRSA);
        configuration.setSigningCertificateChain(certificateConfig.getSigningCertificateChain().toArray(new Certificate[1]));
        configuration.setSignedReceiptMicAlgorithms(signedReceiptMicAlgorithms);
        configuration.setSigningPrivateKey(privateKey);
        configuration.setServer("JJYTestEnv");
        as2Component.setConfiguration(configuration);
    }
}
