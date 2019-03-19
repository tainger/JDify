package io.terminus.dalaran.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DalaranFlow {
    private String id;
    private Trigger trigger;
    private List<Processor> processors;
    private Map<String, String> properties;

    @Data
    public static class Trigger<T> {
        private String type;
        private String configInstanceId;
        private T config;
    }

    @Data
    public static class Processor<T> {
        private String type;
        private String configInstanceId;
        private T config;
    }
}
