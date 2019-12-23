package io.terminus.dalaran.component.processor.as2;

import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Data
public class ProvisionAS2ComponentCrypto {

    private static final String RSA = "RSA";

    private static final String BC = "BC";

    private KeyPair issueKP;

    private X509Certificate issueCert;

    private KeyPair signingKP;

    private KeyPair decryptingKP;

    private X509Certificate clientCert;

    private List<Certificate> signingCertificateChain;

    private List<Certificate> encryptingCertificateChain;

    public void configure(AS2ClientConfig config) throws Exception {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        Certificate sslCer = factory.generateCertificate(IOUtils.toInputStream(config.getSslCer()));
        signingCertificateChain.add(sslCer);
        Certificate sslPb = factory.generateCertificate(IOUtils.toInputStream(config.getSslPb()));
        signingCertificateChain.add(sslPb);

        Certificate encryptionCer = factory.generateCertificate(IOUtils.toInputStream(config.getEncryptionCer()));
        encryptingCertificateChain.add(encryptionCer);
        Certificate encryptionPb = factory.generateCertificate(IOUtils.toInputStream(config.getEncryptionPb()));
        encryptingCertificateChain.add(encryptionPb);
    }

    private PrivateKey buildPrivateKey(String key) throws Exception {
        byte[] buffer = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(buffer);
        KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
        return factory.generatePrivate(pkcs8EncodedKeySpec);
    }

    private PublicKey buildPublicKey(String key) throws Exception {
        byte[] buffer = Base64.getDecoder().decode(key);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(buffer);
        KeyFactory factory = KeyFactory.getInstance(RSA, BC);
        return factory.generatePublic(x509EncodedKeySpec);
    }

    private AuthorityKeyIdentifier createAuthorityKeyId(PublicKey pub) {
        SubjectPublicKeyInfo keyInfo = SubjectPublicKeyInfo.getInstance(pub.getEncoded());
        return new BcX509ExtensionUtils().createAuthorityKeyIdentifier(keyInfo);
    }

    private SubjectKeyIdentifier createSubjectKeyId(PublicKey pub) {
        SubjectPublicKeyInfo keyInfo = SubjectPublicKeyInfo.getInstance(pub.getEncoded());
        return new BcX509ExtensionUtils().createSubjectKeyIdentifier(keyInfo);
    }

    private X509Certificate makeCertificate(KeyPair subPair, KeyPair issPair, String subDN, String issDN) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        PublicKey subPub = subPair.getPublic();
        PrivateKey issPriv = issPair.getPrivate();
        PublicKey issPub = issPair.getPublic();
        JcaX509v3CertificateBuilder jcaX509v3CertificateBuilder = new JcaX509v3CertificateBuilder(new X500Name(issDN),
                BigInteger.valueOf(1L),
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 100),
                new X500Name(subDN), subPub);

        jcaX509v3CertificateBuilder.addExtension(Extension.subjectKeyIdentifier, false, createSubjectKeyId(subPub));
        jcaX509v3CertificateBuilder.addExtension(Extension.authorityKeyIdentifier, false, createAuthorityKeyId(issPub));
        return new JcaX509CertificateConverter().setProvider(BC).getCertificate(jcaX509v3CertificateBuilder
                .build(new JcaContentSignerBuilder("MD5withRSA").setProvider(BC).build(issPriv)));
    }
}
