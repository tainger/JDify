package io.terminus.dalaran.model.alarm;


import lombok.Data;
import java.util.Map;

@Data
public class NoticeMessage {

    private String flowName;

    private String flowId;

    private Boolean isTouchFailureAlarm;

    private Long failureCount;

    private Long failureFrequency;

    private Boolean isTouchTimeOutAlarm;

    private Long timeOutFrequency;

    private Long timeOutCount;

    private String createDate;
    
    private Map<AlarmRuleConfig.ChannelType, String> alarmChannel;


    public NoticeMessage() {
        this.isTouchFailureAlarm = false;
        this.failureCount = 0L;
        this.failureFrequency = 0L;
        this.isTouchTimeOutAlarm = false;
        this.timeOutFrequency = 0L;
        this.timeOutCount = 0L;
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


    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }


    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public Boolean getTouchFailureAlarm() {
        return isTouchFailureAlarm;
    }

    public void setTouchFailureAlarm(Boolean touchFailureAlarm) {
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

    public Boolean getTouchTimeOutAlarm() {
        return isTouchTimeOutAlarm;
    }

    public void setTouchTimeOutAlarm(Boolean touchTimeOutAlarm) {
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

    public Map<AlarmRuleConfig.ChannelType, String> getAlarmChannel() {
        return alarmChannel;
    }

    public void setAlarmChannel(Map<AlarmRuleConfig.ChannelType, String> alarmChannel) {
        this.alarmChannel = alarmChannel;
    }

    @Override
    public String toString() {
        return "NoticeMessage{" +
                "flowName='" + flowName + '\'' +
                ", flowId='" + flowId + '\'' +
                ", isTouchFailureAlarm=" + isTouchFailureAlarm +
                ", failureCount=" + failureCount +
                ", failureFrequency=" + failureFrequency +
                ", isTouchTimeOutAlarm=" + isTouchTimeOutAlarm +
                ", timeOutFrequency=" + timeOutFrequency +
                ", timeOutCount=" + timeOutCount +
                ", createDate='" + createDate + '\'' +
                ", alarmChannel=" + alarmChannel +
                '}';
    }
}

