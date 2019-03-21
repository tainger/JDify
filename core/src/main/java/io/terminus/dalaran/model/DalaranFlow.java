package io.terminus.dalaran.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DalaranFlow {
    private String id;
    private Trigger trigger;
    private Boolean retryable;
    private Integer maxRetry;
    private Integer retryDelay;
    private List<Processor> processors;
    private Map<String, String> properties;

    @Data
    public static class Trigger<T> {
        private Long id;
        private String type;
        private T config;
    }

    @Data
    public static class Processor<T> {
        private Long id;
        private String type;
        private T config;
    }
}
