package io.terminus.dalaran.console.model.query;

import lombok.Data;

import java.util.Date;

@Data
public class TracingLogQuery {

    private Long moduleId;
    private Long triggerId;
    private Long flowId;

    private Date startTime;
    private Date endTime;

    private boolean testFlow = false;

    // TODO successful

}
