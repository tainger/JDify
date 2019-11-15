package io.terminus.dalaran.model.query;

import io.terminus.dalaran.TracingType;
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

    private Long timeLt;

    private Long timeGt;

    private TracingType tracingType;

    private Boolean successful;

    private boolean testFlow = false;

    private PageRequest pageRequest;

    @Data
    public class PageRequest {

        private Integer pageSize;

        private Integer pageNumber;

        private Long offset;
    }
}
