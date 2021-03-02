package io.terminus.dalaran.runtime;

import java.util.Arrays;
import java.util.Date;

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

    public NoticeMessage() {
        this.isTouchFailureAlarm = false;
        this.isTouchTimeOutAlarm = false;
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

    public Long getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(Long failureCount) {
        this.failureCount = failureCount;
    }

    public Long getFailureFrequency() {
        return failureFrequency;
    }

    public void setFailureFrequency(Long failureFrequency) {
        this.failureFrequency = failureFrequency;
    }

    public Boolean getIsTouchTimeOutAlarm() {
        return isTouchTimeOutAlarm;
    }

    public void setIsTouchTimeOutAlarm(Boolean touchTimeOutAlarm) {
        isTouchTimeOutAlarm = touchTimeOutAlarm;
    }

    public Long getTimeOutFrequency() {
        return timeOutFrequency;
    }

    public void setTimeOutFrequency(Long timeOutFrequency) {
        this.timeOutFrequency = timeOutFrequency;
    }

    public Long getTimeOutCount() {
        return timeOutCount;
    }

    public void setTimeOutCount(Long timeOutCount) {
        this.timeOutCount = timeOutCount;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String[] getContactWays() {
        return contactWays;
    }

    public void setContactWays(String[] contactWays) {
        this.contactWays = contactWays;
    }

    @Override
    public String toString() {
        return "NoticeMessage{" +
                "flowName='" + flowName + '\'' +
                ", isTouchFailureAlarm=" + isTouchFailureAlarm +
                ", failureCount=" + failureCount +
                ", failureFrequency=" + failureFrequency +
                ", isTouchTimeOutAlarm=" + isTouchTimeOutAlarm +
                ", timeOutFrequency=" + timeOutFrequency +
                ", timeOutCount=" + timeOutCount +
                ", createDate=" + createDate +
                ", contactWays=" + Arrays.toString(contactWays) +
                '}';
    }

}
