package io.terminus.dalaran.core.resource.entity;

import lombok.Data;

import java.util.Date;

@Data
public class NoticeMessage {

    private String flowName;

    private Boolean isTouchFailureAlarm;

    private Long failureCount;

    private Long failureFrequency;

    private Boolean isTouchTimeOutAlarm;

    private Long timeOutFrequency;

    private Long timeOutCount;

    private Date createDate;

    private String []contactWays;
}
