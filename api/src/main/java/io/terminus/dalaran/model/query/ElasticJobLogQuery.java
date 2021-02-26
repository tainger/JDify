package io.terminus.dalaran.model.query;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class ElasticJobLogQuery {

    private String jobName;

    private String ip;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTimeBegin;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTimeEnd;

    private Boolean isSuccess;

}
