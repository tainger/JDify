package io.terminus.dalaran.component.processor.mapper.model;

import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;

@Data
public class TemporarySourcePath {

    private ParamType type;

    private String value;
}
