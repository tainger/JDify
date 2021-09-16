package io.terminus.dalaran.component.http.processor.okhttp;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.core.oss.OSSAccount;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Processor(
        value = {"OKHttpClient", "BrotliHttpClient"},
        order = 8,
        configType = OKHttpClientConfig.class,
        bodyType = "JSON",
        developer = DalaranConstants.DALARAN
)
@Slf4j
public class OKHttpClient implements DalaranProcessor<OKHttpClientConfig> {

    @Autowired
    private OSSAccount ossAccount;

    @Override
    public void configure(ProcessorDefinition route, OKHttpClientConfig config) {
        if (config.getConnector().getProtocol().name().equals("HTTPS")) {
            if (config.getCheckCertificate() && StringUtils.isNotBlank(config.getSslCertificate())) {
                File dir = new File("/var/tmp");
                try {
                    X509TrustManager trustManager;
                    SSLSocketFactory sslSocketFactory;
                    trustManager = trustManagerForCertificates(trustedCertificatesInputStream(config.getSslCertificate(), ossAccount, dir));
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, new TrustManager[]{trustManager}, null);
                    sslSocketFactory = sslContext.getSocketFactory();

                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .connectTimeout(config.getConnector().getTimeout(), TimeUnit.MILLISECONDS)
                            .readTimeout(config.getConnector().getTimeout(), TimeUnit.MILLISECONDS)
                            .connectionPool(new ConnectionPool(50, 5, TimeUnit.MINUTES))
                            .sslSocketFactory(sslSocketFactory, trustManager)
                            .connectionSpecs(Arrays.asList(ConnectionSpec.COMPATIBLE_TLS))
                            .build();
                    route.process(new OKHttpProcessor(config, client, ossAccount));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .connectTimeout(config.getConnector().getTimeout(), TimeUnit.MILLISECONDS)
                        .readTimeout(config.getConnector().getTimeout(), TimeUnit.MILLISECONDS)
                        .connectionPool(new ConnectionPool(50, 5, TimeUnit.MINUTES))
                        .sslSocketFactory(createSSLSocketFactory(),new TrustAllCertificates())
                        .connectionSpecs(Arrays.asList(ConnectionSpec.COMPATIBLE_TLS))
                        .hostnameVerifier((s, sslSession) -> true)
                        .build();
                route.process(new OKHttpProcessor(config, client, ossAccount));
            }
        } else {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .connectTimeout(config.getConnector().getTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(config.getConnector().getTimeout(), TimeUnit.MILLISECONDS)
                    .connectionPool(new ConnectionPool(50, 5, TimeUnit.MINUTES))
                    .build();
            route.process(new OKHttpProcessor(config, client, ossAccount));
        }
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new TrustAllCertificates()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssfFactory;
    }

    private InputStream trustedCertificatesInputStream(String object, OSSAccount ossAccount, File dir) throws Exception{
        String fileName = "dalaran-" + object.hashCode();
        File tempFile = File.createTempFile(fileName, ".crt", dir);
        OSS ossClient = new OSSClientBuilder().build(ossAccount.getEndpoint(), ossAccount.getAccessId(), ossAccount.getAccessSecret());
        ossClient.getObject(new GetObjectRequest(ossAccount.getBucketName(), object), tempFile);
        InputStream in = null;
        try {
            in = new FileInputStream(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return in;
    }

    private X509TrustManager trustManagerForCertificates(InputStream in) throws GeneralSecurityException {

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        //通过证书工厂得到自签证书对象集合
        Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("expected non-empty set of trusted certificates");
        }
        //为证书设置一个keyStore
        char[] password = "password".toCharArray(); // Any password will work.
        KeyStore keyStore = newEmptyKeyStore(password);
        int index = 0;
        //将证书放入keystore中
        for (Certificate certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }
        // Use it to build an X509 trust manager.
        //使用包含自签证书信息的keyStore去构建一个X509TrustManager
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }

    private KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
