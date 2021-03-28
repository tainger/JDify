package io.terminus.dalaran.config;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class GroupProcessorInfo {

    private Map<String, Map<String, Map<String, ProcessorInfo>>> processors = new ConcurrentHashMap<>();
}
