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

@Data
public class ProvisionAS2ComponentCrypto {

    private static final String RSA = "RSA";

    private static final String BC = "BC";

    private KeyPair issueKP;

    private X509Certificate issueCert;

    private KeyPair signingKP;

    private KeyPair decryptingKP;

    private X509Certificate clientCert;

    private Certificate[] signingCertificateChain = new Certificate[1];

    private Certificate signingCertificate;

    private Certificate[] encryptingCertificateChain = new Certificate[1];

    private Certificate encryptingCertificate;


    public void configCertificateChain(AS2ClientConfig config) throws Exception {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        Certificate sslCer = factory.generateCertificate(IOUtils.toInputStream("-----BEGIN CERTIFICATE-----\n" +
                "MIIBtzCCASACCQCuC5mfp897VTANBgkqhkiG9w0BAQsFADAgMQswCQYDVQQGEwJD\n" +
                "TjERMA8GA1UECgwIVGVybWludXMwHhcNMTkxMTI1MDkzMjExWhcNMjAxMTI0MDkz\n" +
                "MjExWjAgMQswCQYDVQQGEwJDTjERMA8GA1UECgwIVGVybWludXMwgZ8wDQYJKoZI\n" +
                "hvcNAQEBBQADgY0AMIGJAoGBAOl8hfO6pqHRmSI6bDQMzg8QgpTw1m3pOBri3RzB\n" +
                "ZgZu+5asK1JrB02BQR0sHEc+BBlvEfbJgIdx1/Mx9OCXinrdm+08kxy8QbxumnjI\n" +
                "Vois/1k1xKpXolIZv2lnNKFsLjEuVJVIfnWVNXadgVsu1/+9lmEn2FuEmORyBwXb\n" +
                "Md7FAgMBAAEwDQYJKoZIhvcNAQELBQADgYEAaX0Nfr2vGZKA99jngzG+/MxpNp13\n" +
                "fFoSaDmjfS3XyAWIskA9Iq82QYtClGNqMJ0LrPbneTW9pyQvxdwiNAYfVA0dgzwu\n" +
                "qwfW52drl697WcC5l4HRpRnaahwyqFdyzzLLIadXlgXW4Q/8phd8+ndt0BnGTjWK\n" +
                "biyyIjAOUK12kuA=\n" +
                "-----END CERTIFICATE-----\n", "utf-8"));
        signingCertificateChain[0] = sslCer;
        signingCertificate = sslCer;
//        Certificate sslPb = factory.generateCertificate(IOUtils.toInputStream("-----BEGIN PKCS7-----\n" +
//                "MIIB6AYJKoZIhvcNAQcCoIIB2TCCAdUCAQExADALBgkqhkiG9w0BBwGgggG7MIIB\n" +
//                "tzCCASACCQCuC5mfp897VTANBgkqhkiG9w0BAQsFADAgMQswCQYDVQQGEwJDTjER\n" +
//                "MA8GA1UECgwIVGVybWludXMwHhcNMTkxMTI1MDkzMjExWhcNMjAxMTI0MDkzMjEx\n" +
//                "WjAgMQswCQYDVQQGEwJDTjERMA8GA1UECgwIVGVybWludXMwgZ8wDQYJKoZIhvcN\n" +
//                "AQEBBQADgY0AMIGJAoGBAOl8hfO6pqHRmSI6bDQMzg8QgpTw1m3pOBri3RzBZgZu\n" +
//                "+5asK1JrB02BQR0sHEc+BBlvEfbJgIdx1/Mx9OCXinrdm+08kxy8QbxumnjIVois\n" +
//                "/1k1xKpXolIZv2lnNKFsLjEuVJVIfnWVNXadgVsu1/+9lmEn2FuEmORyBwXbMd7F\n" +
//                "AgMBAAEwDQYJKoZIhvcNAQELBQADgYEAaX0Nfr2vGZKA99jngzG+/MxpNp13fFoS\n" +
//                "aDmjfS3XyAWIskA9Iq82QYtClGNqMJ0LrPbneTW9pyQvxdwiNAYfVA0dgzwuqwfW\n" +
//                "52drl697WcC5l4HRpRnaahwyqFdyzzLLIadXlgXW4Q/8phd8+ndt0BnGTjWKbiyy\n" +
//                "IjAOUK12kuChADEA\n" +
//                "-----END PKCS7-----\n", "utf-8"));
//        signingCertificateChain.add(sslPb);

        Certificate encryptionCer = factory.generateCertificate(IOUtils.toInputStream("-----BEGIN CERTIFICATE-----\n" +
                "MIIBtzCCASACCQDGlkm3RRXReDANBgkqhkiG9w0BAQsFADAgMQswCQYDVQQGEwJD\n" +
                "TjERMA8GA1UECgwIVGVybWludXMwHhcNMTkxMTI1MDk0MTM1WhcNMjAxMTI0MDk0\n" +
                "MTM1WjAgMQswCQYDVQQGEwJDTjERMA8GA1UECgwIVGVybWludXMwgZ8wDQYJKoZI\n" +
                "hvcNAQEBBQADgY0AMIGJAoGBAN3/t704ErhHSzWmhfStFKGuN+17IHAbVLANiFmv\n" +
                "0ea3GqFzQvwIoZHLwd8Tpaf6GLxyfkcheGZdr00H3pHsyW1yxiwt05acIvguQEib\n" +
                "dM2yw/OJG5JuABtdw0cMw9EoHoZvTMWMe4EYyUniTmMWRIUitc8sT6mRTEDJ51ul\n" +
                "YCTfAgMBAAEwDQYJKoZIhvcNAQELBQADgYEAzUwKa2vzYTZ8WeHOqnCt0ySgpneP\n" +
                "yUPq1RzBovPrtChOPFlJ/snkySdlkWO78+NPie9uieP1pV+T3E4bDvLPwRxSiC7+\n" +
                "PD+8YCywQ+mES+quuIxyl/HMZugEuB6UPoiKP8EYhU829azSeBYZTwV4ZDDhp3CZ\n" +
                "JNfByok8z9OdsTc=\n" +
                "-----END CERTIFICATE-----\n", "utf-8"));
        encryptingCertificateChain[0] = encryptionCer;
        encryptingCertificate = encryptionCer;
//        Certificate encryptionPb = factory.generateCertificate(IOUtils.toInputStream("-----BEGIN PKCS7-----\n" +
//                "MIIB6AYJKoZIhvcNAQcCoIIB2TCCAdUCAQExADALBgkqhkiG9w0BBwGgggG7MIIB\n" +
//                "tzCCASACCQDGlkm3RRXReDANBgkqhkiG9w0BAQsFADAgMQswCQYDVQQGEwJDTjER\n" +
//                "MA8GA1UECgwIVGVybWludXMwHhcNMTkxMTI1MDk0MTM1WhcNMjAxMTI0MDk0MTM1\n" +
//                "WjAgMQswCQYDVQQGEwJDTjERMA8GA1UECgwIVGVybWludXMwgZ8wDQYJKoZIhvcN\n" +
//                "AQEBBQADgY0AMIGJAoGBAN3/t704ErhHSzWmhfStFKGuN+17IHAbVLANiFmv0ea3\n" +
//                "GqFzQvwIoZHLwd8Tpaf6GLxyfkcheGZdr00H3pHsyW1yxiwt05acIvguQEibdM2y\n" +
//                "w/OJG5JuABtdw0cMw9EoHoZvTMWMe4EYyUniTmMWRIUitc8sT6mRTEDJ51ulYCTf\n" +
//                "AgMBAAEwDQYJKoZIhvcNAQELBQADgYEAzUwKa2vzYTZ8WeHOqnCt0ySgpnePyUPq\n" +
//                "1RzBovPrtChOPFlJ/snkySdlkWO78+NPie9uieP1pV+T3E4bDvLPwRxSiC7+PD+8\n" +
//                "YCywQ+mES+quuIxyl/HMZugEuB6UPoiKP8EYhU829azSeBYZTwV4ZDDhp3CZJNfB\n" +
//                "yok8z9OdsTehADEA\n" +
//                "-----END PKCS7-----\n", "utf-8"));
//        encryptingCertificateChain.add(encryptionPb);

    }

    public PrivateKey buildPrivateKey(String key) throws Exception {
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
