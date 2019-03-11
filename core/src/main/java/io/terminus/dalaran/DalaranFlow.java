package io.terminus.dalaran;

import java.util.List;
import java.util.Map;

public class DalaranFlow {

    private DalaranTriggerConfig trigger;

    private List<DalaranProcessorConfig> processors;

    private Map<String, String> properties;

    public DalaranTriggerConfig getTrigger() {
        return trigger;
    }

    public void setTrigger(DalaranTriggerConfig trigger) {
        this.trigger = trigger;
    }

    public List<DalaranProcessorConfig> getProcessors() {
        return processors;
    }

    public void setProcessors(List<DalaranProcessorConfig> processors) {
        this.processors = processors;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
