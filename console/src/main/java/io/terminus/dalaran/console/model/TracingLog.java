package io.terminus.dalaran.console.model;

import io.terminus.dalaran.BodyModelType;
import lombok.Data;

import java.util.Date;

@Data
public class TracingLog {

    private Long id;

    private String recordId;

    private Long flowId;

    private String flowName;

    private Long processorId;

    private String processorName;

    private Date timestamp;

    private Long elapsed;

    private BodyModelType inputBodyType;

    private BodyModelType outputBodyType;

    private String inputBody;

    private String outputBody;
}
