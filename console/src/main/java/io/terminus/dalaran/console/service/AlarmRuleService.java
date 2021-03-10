package io.terminus.dalaran.console.service;


import io.terminus.dalaran.model.dto.AlarmRuleDTO;
import io.terminus.dalaran.model.dto.basic.BasicAlarmInfo;
import io.terminus.dalaran.model.query.AlarmRuleQuery;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AlarmRuleService {

    String create(AlarmRuleDTO alarmRuleDTO);

    AlarmRuleDTO update(AlarmRuleDTO alarmRuleDTO);

    void delete(String id);

    AlarmRuleDTO detail(String id);

    Page<AlarmRuleDTO> queryPageable(AlarmRuleQuery query, Integer pageNumber, Integer pageSize);

    List<BasicAlarmInfo> listAlarmRuleByModuleId(String moduleId);
}
