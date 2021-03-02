package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.OnException;
import io.terminus.dalaran.console.service.AlarmRuleService;
import io.terminus.dalaran.model.dto.AlarmRuleDTO;
import io.terminus.dalaran.model.dto.log.MainLogDTO;
import io.terminus.dalaran.model.query.AlarmRuleQuery;
import io.terminus.dalaran.rest.read.AlarmRuleReadAPI;
import io.terminus.dalaran.rest.write.AlarmRuleWriteAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AlarmRuleManagementRest implements AlarmRuleReadAPI, AlarmRuleWriteAPI {


    @Autowired
    private AlarmRuleService alarmRuleService;


    @Override
    @OnException(code = ResponseMessage.ALARM_RULE_CREATE_ERROR)
    public Long create(AlarmRuleDTO alarmRuleDTO) {
       return alarmRuleService.create(alarmRuleDTO);
    }

    @Override
    @OnException(code = ResponseMessage.ALARM_RULE_UPDATE_ERROR)
    public AlarmRuleDTO update(AlarmRuleDTO alarmRuleDTO) {
        return alarmRuleService.update(alarmRuleDTO);
    }

    @Override
    @OnException(code = ResponseMessage.ALARM_RULE_DELETE_ERROR)
    public void deleteById(Long id) {
         alarmRuleService.delete(id);
    }

    @Override
    @OnException(code = ResponseMessage.ALARM_RULE_QUERY_ERROR)
    public AlarmRuleDTO detail(Long id) {
        return alarmRuleService.detail(id);
    }

    @Override
    @OnException(code = ResponseMessage.ALARM_RULE_QUERY_ERROR)
    public Page<AlarmRuleDTO> queryPageable(AlarmRuleQuery query, Integer pageNumber, Integer pageSize) {
        return alarmRuleService.queryPageable(query, pageNumber, pageSize);
    }
}
