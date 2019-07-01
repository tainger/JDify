package io.terminus.dalaran.example;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * Created by jingdi on 2019/6/12
 */
public class DalaranDerializer implements Deserializer {

    @Override
    public void configure(Map map, boolean b) {

    }

    @Override
    public Object deserialize(String s, byte[] bytes) {
        System.out.println(JSON.parseObject(bytes, Object.class).toString());
        return JSON.parseObject(bytes, Object.class);
    }

    @Override
    public void close() {

    }
}
