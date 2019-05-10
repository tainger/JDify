package io.terminus.dalaran.console.model.query;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class TracingLogQuery {

    private Long moduleId;
    private Long flowId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private Boolean successful;

    private boolean testFlow = false;
}
