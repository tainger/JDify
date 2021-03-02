package io.terminus.dalaran.model.dto;

import io.terminus.dalaran.model.dto.basic.BasicAlarmInfo;
import lombok.Data;

import java.util.Map;


@Data
public class AlarmRuleDTO extends BasicAlarmInfo {
    private Map<String, Object> config;
}
