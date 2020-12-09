package io.terminus.dalaran.component.mail.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import io.terminus.dalaran.core.oss.*;

import java.io.File;

public class OSSUtils {

    public static File getFileFromOss(String fileKey, OSSAccount ossAccount) throws Exception {
        File dir = new File("/var/tmp");
        String fileName = "dalaran-" + fileKey.hashCode();
        File tempFile = File.createTempFile(fileName, "", dir);
        OSS ossClient = new OSSClientBuilder().build(ossAccount.getEndpoint(), ossAccount.getAccessId(), ossAccount.getAccessSecret());
        ossClient.getObject(new GetObjectRequest(ossAccount.getBucketName(), fileKey), tempFile);
        return tempFile;
    }
}
