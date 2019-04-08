package io.terminus.dalaran.model;

import io.terminus.dalaran.BodyModelType;
import lombok.Data;

import java.util.Map;

@Data
public class DalaranTracingInfo {

    private Long triggerId;

    private Long flowId;

    private Long processorId;

    private Long timestamp;

    private Long elapsed;

    private String inputBody;
    private BodyModelType inputBodyType;
    private Map<String, Object> inputHeaders;

    private String outputBody;
    private BodyModelType outputBodyType;
    private Map<String, Object> outputHeaders;
}
