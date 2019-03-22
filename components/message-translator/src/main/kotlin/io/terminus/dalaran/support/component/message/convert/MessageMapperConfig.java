package io.terminus.dalaran.support.component.message.convert;

import lombok.Data;

import java.util.Map;

@Data
public class MessageMapperConfig {
    private String targetModel;
    private String mappingFile;
    private Map<String, String> mappings;
}
