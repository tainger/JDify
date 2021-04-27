package io.terminus.dalaran.model.dto.flow;

import lombok.Data;

@Data
public class BindAlarmRuleDTO {

    private boolean isMonitor;

    private String flowId;

    private String alarmRuleId;

    public boolean getIsMonitor() {
        return isMonitor;
    }

    public void setIsMonitor(boolean monitor) {
        isMonitor = monitor;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getAlarmRuleId() {
        return alarmRuleId;
    }

    public void setAlarmRuleId(String alarmRuleId) {
        this.alarmRuleId = alarmRuleId;
    }
}
