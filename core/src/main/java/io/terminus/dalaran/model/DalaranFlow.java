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
    public
    class Trigger {
        private String type;
        private String configInstanceId;
        private Map<String, Object> config;
    }

    @Data
    public
    class Processor {
        private String type;
        private String configInstanceId;
        private Map<String, Object> config;
    }
}
