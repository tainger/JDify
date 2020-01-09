package io.terminus.dalaran.component.trigger.as2;

import io.terminus.dalaran.core.component.DalaranTrigger;
import io.terminus.dalaran.core.component.annotation.Trigger;
import io.terminus.dalaran.core.resource.oss.OSSAccount;
import org.apache.camel.component.as2.api.AS2EncryptionAlgorithm;
import org.apache.camel.component.as2.api.AS2SignatureAlgorithm;
import org.apache.camel.model.RouteDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import static org.apache.camel.builder.Builder.constant;

@Trigger(
        value = "as2-server",
        order = 19,
        configType = AS2ServerConfig.class
)
public class DalaranAS2Server implements DalaranTrigger<AS2ServerConfig> {

    private static final String AS2_SERVER_URI = "as2://server/listen?requestUri=%s&serverPortNumber=%s&requestUriPattern=%s";

    @Autowired
    private OSSAccount ossAccount;

    @Override
    public void buildFromRoute(RouteDefinition route, AS2ServerConfig config) {
        String uri = String.format(AS2_SERVER_URI, config.getRequestUri(), config.getPort(), config.getUriPattern());
        try {
            init(route, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        route.from(uri);
    }

    private void init(RouteDefinition route, AS2ServerConfig config) throws Exception {
        CertificateConfig certificateConfig = new CertificateConfig();
        certificateConfig.configCertificates(config, ossAccount);

        String[] signedReceiptMicAlgorithms = new String[]{"sha256"};
        route.setHeader("CamelAS2.signingAlgorithm", constant(AS2SignatureAlgorithm.SHA256WITHRSA));
        route.setHeader("CamelAS2.signingCertificateChain", constant(certificateConfig.getSigningCertificateChain().toArray()));
        route.setHeader("CamelAS2.signingPrivateKey", constant(certificateConfig.getKeyPair().getPrivate()));
        route.setHeader("CamelAS2.signedReceiptMicAlgorithms",  constant(signedReceiptMicAlgorithms));
        route.setHeader("CamelAS2.encryptingAlgorithm",  constant(AS2EncryptionAlgorithm.DES_CBC));
        route.setHeader("CamelAS2.encryptingCertificateChain", constant(certificateConfig.getEncryptingCertificateChain().toArray()));
        route.setHeader("CamelAS2.decryptingPrivateKey", constant(certificateConfig.getKeyPair().getPrivate()));
//        route.setHeader("CamelAS2.compressionAlgorithm", constant(AS2CompressionAlgorithm.ZLIB))


    }
}
