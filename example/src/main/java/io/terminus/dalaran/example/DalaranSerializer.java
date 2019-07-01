package io.terminus.dalaran.example;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Created by jingdi on 2019/6/12
 */
public class DalaranSerializer implements Serializer {

    @Override
    public void configure(Map map, boolean b) {
        System.out.println(map);
    }

    @Override
    public byte[] serialize(String s, Object o) {
        System.out.println(o);
        return JSON.toJSON(o).toString().getBytes();
    }

    @Override
    public void close() {

    }
}
