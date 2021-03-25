package io.terminus.dalaran.component.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import io.terminus.dalaran.core.oss.OSSAccount;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OSSUtils {

    public static File downloadByPath(String fileKey, OSSAccount ossAccount) throws Exception {
        File dir = new File("/var/tmp");
        String fileName = "dalaran-" + fileKey.hashCode();
        File tempFile = File.createTempFile(fileName, "", dir);
        OSS ossClient = new OSSClientBuilder().build(ossAccount.getEndpoint(), ossAccount.getAccessId(), ossAccount.getAccessSecret());
        ossClient.getObject(new GetObjectRequest(ossAccount.getBucketName(), fileKey), tempFile);
        ossClient.shutdown();
        return tempFile;
    }

    public static String upload(String fileName, InputStream inputStream, OSSAccount ossAccount) {
        OSS client = new OSSClientBuilder().build(ossAccount.getEndpoint(), ossAccount.getAccessId(), ossAccount.getAccessSecret());
        String key = ossAccount.getRootDir() + "/" + fileName + "_" + System.currentTimeMillis();
        client.putObject(ossAccount.getBucketName(), key, inputStream);
        client.shutdown();
        return key;
    }

    public static String upload(File file, OSSAccount ossAccount) {
        OSS client = new OSSClientBuilder().build(ossAccount.getEndpoint(), ossAccount.getAccessId(), ossAccount.getAccessSecret());
        String key = ossAccount.getRootDir() + "/" + file.getName() + "_" + System.currentTimeMillis();
        client.putObject(ossAccount.getBucketName(), key, file);
        client.shutdown();
        return key;
    }

    public static OSSObject downloadByUrl(String url, OSSAccount ossAccount) throws Exception {
        OSS ossClient = new OSSClientBuilder().build(ossAccount.getEndpoint(), ossAccount.getAccessId(), ossAccount.getAccessSecret());
        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("Range", "bytes=100-1000");
        OSSObject ossObject =  ossClient.getObject(new URL(url), customHeaders);
        ossClient.shutdown();
        return ossObject;
    }

    public static String getFileUrl(String fileKey, OSSAccount ossAccount) {
        OSS ossClient = new OSSClientBuilder().build(ossAccount.getEndpoint(), ossAccount.getAccessId(), ossAccount.getAccessSecret());
        Date expiration = new Date(new Date().getTime() + 3600 * 1000);
        URL url = ossClient.generatePresignedUrl(ossAccount.getBucketName(), fileKey, expiration);
        ossClient.shutdown();
        return url.toString();
    }
}
