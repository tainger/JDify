package io.terminus.dalaran.component.trigger.as2;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import io.terminus.dalaran.core.resource.oss.OSSAccount;
import lombok.Data;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

import java.io.*;
import java.security.KeyPair;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;

@Data
public class CertificateConfig {

    private List<Certificate> signingCertificateChain = new ArrayList<>();

    private List<Certificate> encryptingCertificateChain = new ArrayList<>();

    private KeyPair keyPair;

    private static String DALARAN_FILE = "dalaran-file.txt";

    public void configCertificates(AS2ServerConfig config, OSSAccount ossAccount) throws Exception {

        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        Certificate partnerCer = factory.generateCertificate(new FileInputStream(getFileFromOss(config.getPartnerCertificate(), ossAccount)));
        encryptingCertificateChain.add(partnerCer);

        Certificate stationCer = factory.generateCertificate(new FileInputStream(getFileFromOss(config.getStationCertificate(), ossAccount)));
        signingCertificateChain.add(stationCer);

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(getFileFromOss(config.getStationPem(), ossAccount))));
        PEMParser pemParser = new PEMParser(reader);

        PEMEncryptedKeyPair pemObject = (PEMEncryptedKeyPair)pemParser.readObject();

        PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build("anywhere".toCharArray());

        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

        keyPair = converter.getKeyPair(pemObject.decryptKeyPair(decProv));
    }

    private File getFileFromOss(String filePath, OSSAccount ossAccount) throws Exception {
        File tmpDir = File.createTempFile("dalaran-", System.currentTimeMillis() + "");
        tmpDir.mkdir();
        OSS ossClient = new OSSClient(ossAccount.getEndpoint(), ossAccount.getAccessId(), ossAccount.getAccessSecret());
        File file = new File(tmpDir, DALARAN_FILE);
        ossClient.getObject(new GetObjectRequest(ossAccount.getBucketName(), filePath), file);
        return file;
    }
}
