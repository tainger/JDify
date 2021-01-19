package io.terminus.dalaran.component.elasticjob.trigger;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import lombok.Data;

@Data
public class ElasticJobConfig {

    @ConfigFieldInfo(label = "zookeeper地址", inputType = FieldInputType.String)
    private String serverLists;

    @ConfigFieldInfo(label = "命名空间", inputType = FieldInputType.String)
    private String namespace;

    @ConfigFieldInfo(label = "作业名称", inputType = FieldInputType.String)
    private String jobName;

    @ConfigFieldInfo(label = "cron", inputType = FieldInputType.String)
    private String cron;

}
