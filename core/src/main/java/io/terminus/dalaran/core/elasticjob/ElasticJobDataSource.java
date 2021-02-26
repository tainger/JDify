package io.terminus.dalaran.core.elasticjob;

import lombok.Data;

@Data
public class ElasticJobDataSource {

    private String url;

    private String username;

    private String password;

    public ElasticJobDataSource(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
}
