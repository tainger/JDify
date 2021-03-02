package io.terminus.dalaran.console;

import lombok.Data;

import java.util.Date;

@Data
public class NoticeMessage {

    private String flowName;

    private Boolean isTouchFailureAlarm;

    private Long FailureCount;

    private Long FailureFrequency;

    private Boolean isTouchTimeOutAlarm;

    private Long TimeOutFrequency;

    private Long TimeOutCount;

    private Date createDate;

    private String []contactWays;
}
