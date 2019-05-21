package io.terminus.dalaran.component.trigger.mq.kafka;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.annotation.ConfigFieldInfo;
import io.terminus.dalaran.config.AllModelConfig;
import lombok.Data;

/**
 * Created by jingdi on 2019/5/20
 */
@Data
public class DalaranKafkaConsumerConfig extends AllModelConfig {

    @ConfigFieldInfo(label = "集群地址", inputType = FieldInputType.String)
    private String brokers;

//    @ConfigFieldInfo(label = "服务地址", inputType = FieldInputType.String)
//    private String host;
//
//    @ConfigFieldInfo(label = "端口", inputType = FieldInputType.Integer)
//    private Integer port;

    @ConfigFieldInfo(label = "主题", inputType = FieldInputType.String)
    private String topic;

//    @ConfigFieldInfo(label = "zk地址", inputType = FieldInputType.String)
//    private String zkHost;
//
//    @ConfigFieldInfo(label = "zk端口", inputType = FieldInputType.Integer)
//    private Integer zkPort;

    @ConfigFieldInfo(label = "消费组id", inputType = FieldInputType.String)
    private String groupId;

    @ConfigFieldInfo(label = "自动提交", inputType = FieldInputType.Select)
    private String autocommit = "false";

//    @ConfigFieldInfo(label = "自动提交", inputType = FieldInputType.Select, defaultValue = "false")
//    private boolean autoCommitEnable = false;
//
//    @ConfigFieldInfo(label = "开启手动提交", inputType = FieldInputType.Select, defaultValue = "true")
//    private boolean allowManualCommit = true;
//
//    @ConfigFieldInfo(label = "异常中断", inputType = FieldInputType.Select, defaultValue = "true")
//    private boolean breakOnFirstError = true;

}
