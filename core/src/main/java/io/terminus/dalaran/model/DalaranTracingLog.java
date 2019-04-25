package io.terminus.dalaran.model;

import io.terminus.dalaran.BodyModelType;
import io.terminus.dalaran.TracingType;
import lombok.Data;

@Data
public class DalaranTracingLog {
    private Long flowId;

    private Long processorId;

    private Long timestamp;

    private Long elapsed;

    private String recordId;

    private boolean successful;

    private boolean main;

    private TracingType tracingType;

    private String inputBody;

    private BodyModelType inputBodyType;

    private String outputBody;

    private BodyModelType outputBodyType;
}