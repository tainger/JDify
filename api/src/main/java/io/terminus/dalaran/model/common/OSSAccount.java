package io.terminus.dalaran.model.common;

import lombok.Data;

@Data
public class OSSAccount {

    private String endpoint;

    private String accessId;

    private String accessSecret;

    private String bucketName;

    private String rootDir;

    public OSSAccount() {
    }

    public OSSAccount(String endpoint, String accessId, String accessSecret, String bucketName, String rootDir) {
        this.endpoint = endpoint;
        this.accessId = accessId;
        this.accessSecret = accessSecret;
        this.bucketName = bucketName;
        this.rootDir = rootDir;
    }
}
