package io.terminus.dalaran.core.resource.oss;

import lombok.Data;

@Data
public class OSSAccount {

    private String endpoint;

    private String accessId;

    private String accessSecret;

    private String bucketName;

    private String rootDir;

    public OSSAccount(String endpoint, String accessId, String accessSecret, String bucketName, String rootDir) {
        this.endpoint = endpoint;
        this.accessId = accessId;
        this.accessSecret = accessSecret;
        this.bucketName = bucketName;
        this.rootDir = rootDir;
    }
}
