package io.terminus.dalaran.rest.read;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.AlarmRuleDTO;
import io.terminus.dalaran.model.dto.log.MainLogDTO;
import io.terminus.dalaran.model.query.AlarmRuleQuery;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value = "/api/alarm", produces = {"application/json; charset=UTF-8"})
public interface AlarmRuleReadAPI {

    @GetMapping("/{id}")
    @ApiOperation("获取报警策略详情")
    AlarmRuleDTO detail(@PathVariable Long id);

    @GetMapping("/pageable")
    @ApiOperation("模糊分页查询报警策略")
    Page<AlarmRuleDTO> queryPageable(AlarmRuleQuery query, @RequestParam Integer pageNumber, @RequestParam Integer pageSize);




}
