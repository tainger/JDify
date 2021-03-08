package io.terminus.dalaran.core.log;

import io.terminus.dalaran.TracingType;
import lombok.Data;

@Data
public class DalaranTracingLog {

    private String flowId;

    private String processorId;

    private Long timestamp;

    private Long elapsed;

    private String recordId;

    private boolean successful;

    private boolean main;

    private TracingType tracingType;

    private String inputBody;

    private String inputBodyType;

    private String outputBody;

    private String outputBodyType;

    private String moduleId;

    private String version;
}