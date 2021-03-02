package io.terminus.dalaran.console.service;


import io.terminus.dalaran.model.dto.AlarmRuleDTO;
import io.terminus.dalaran.model.query.AlarmRuleQuery;
import org.springframework.data.domain.Page;

public interface AlarmRuleService {

    Long create(AlarmRuleDTO alarmRuleDTO);

    AlarmRuleDTO update(AlarmRuleDTO alarmRuleDTO);

    void delete(Long id);

    AlarmRuleDTO detail(Long id);

    Page<AlarmRuleDTO> queryPageable(AlarmRuleQuery query, Integer pageNumber, Integer pageSize);

}
