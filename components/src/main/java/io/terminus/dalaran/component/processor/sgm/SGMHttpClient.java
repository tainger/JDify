package io.terminus.dalaran.component.processor.sgm;

import io.terminus.dalaran.component.processor.sgm.model.SGMSignInfo;
import io.terminus.dalaran.core.component.BodySerializeType;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import io.terminus.dalaran.model.BodyType;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@Processor(
        value = "sgm-http-client",
        name = "深国免 Http 调用器",
        order = 1,
        configType = SGMHttpClientConfig.class,
        allowBodyTypes = {BodyType.JSON},
        inputSerializeType = BodySerializeType.Object,
        outputSerializeType = BodySerializeType.Serialized
)
public class SGMHttpClient implements DalaranProcessor<SGMHttpClientConfig> {
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void configure(ProcessorDefinition route, SGMHttpClientConfig config) {
        String sno = System.currentTimeMillis() + "";
        config.getConnector().setSno(sno);
        SGMHttpProcessor processor = new SGMHttpProcessor(config, redisTemplate);
        route.process(processor);
    }
}
