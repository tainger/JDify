package io.terminus.dalaran.console.model;

import io.terminus.dalaran.BodyModelType;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TriggerLog {

    private Long id;

    private String recordId;

    private Long triggerId;

    private String triggerName;

    private Date timestamp;

    private Long elapsed;

    private BodyModelType inputBodyType;

    private BodyModelType outputBodyType;

    private String inputBody;

    private String outputBody;

    private List<TracingLog> tracingLogList;
}
