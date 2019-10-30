package io.terminus.dalaran.config;

import lombok.Data;

@Data
public class TriggerInfo extends AbstractComponentInfo {

    private boolean apiDocs = false;

    private boolean wordDocs = false;
}
