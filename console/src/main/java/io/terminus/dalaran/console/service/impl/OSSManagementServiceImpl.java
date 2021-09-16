package io.terminus.dalaran.console.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import io.terminus.dalaran.console.service.OSSManagementService;
import io.terminus.dalaran.core.oss.OSSAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class OSSManagementServiceImpl implements OSSManagementService {

    private static final String FILE_ROOT = "dalaran";

    @Autowired
    private OSSAccount ossAccount;

    @Override
    public String upload(MultipartFile file) {
        OSS client = new OSSClientBuilder().build(ossAccount.getEndpoint(), ossAccount.getAccessId(), ossAccount.getAccessSecret());
        String key = ossAccount.getRootDir() + "/" + FILE_ROOT + "/" + System.currentTimeMillis() + "-" + file.getOriginalFilename();
        try {
            client.putObject(ossAccount.getBucketName(), key, file.getInputStream());
            client.shutdown();
            return key;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
