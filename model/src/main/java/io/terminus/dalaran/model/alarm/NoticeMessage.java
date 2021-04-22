package io.terminus.dalaran.model.alarm;


import lombok.Data;

@Data
public class NoticeMessage {

    private String flowName;

    private Boolean isTouchFailureAlarm;

    private Integer failureCount;

    private Integer failureFrequency;

    private Boolean isTouchTimeOutAlarm;

    private Integer timeOutFrequency;

    private Integer timeOutCount;

    private String createDate;

    private String[] contactWays;

    public NoticeMessage() {
        this.isTouchFailureAlarm = false;
        this.failureCount = 0;
        this.failureFrequency = 0;
        this.isTouchTimeOutAlarm = false;
        this.timeOutFrequency = 0;
        this.timeOutCount = 0;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public Boolean getIsTouchFailureAlarm() {
        return isTouchFailureAlarm;
    }

    public void setIsTouchFailureAlarm(Boolean touchFailureAlarm) {
        isTouchFailureAlarm = touchFailureAlarm;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(Integer failureCount) {
        this.failureCount = failureCount;
    }

    public Integer getFailureFrequency() {
        return failureFrequency;
    }

    public void setFailureFrequency(Integer failureFrequency) {
        this.failureFrequency = failureFrequency;
    }

    public Boolean getIsTouchTimeOutAlarm() {
        return isTouchTimeOutAlarm;
    }

    public void setIsTouchTimeOutAlarm(Boolean touchTimeOutAlarm) {
        isTouchTimeOutAlarm = touchTimeOutAlarm;
    }

    public Integer getTimeOutFrequency() {
        return timeOutFrequency;
    }

    public void setTimeOutFrequency(Integer timeOutFrequency) {
        this.timeOutFrequency = timeOutFrequency;
    }

    public Integer getTimeOutCount() {
        return timeOutCount;
    }

    public void setTimeOutCount(Integer timeOutCount) {
        this.timeOutCount = timeOutCount;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String[] getContactWays() {
        return contactWays;
    }

    public void setContactWays(String[] contactWays) {
        this.contactWays = contactWays;
    }
}

