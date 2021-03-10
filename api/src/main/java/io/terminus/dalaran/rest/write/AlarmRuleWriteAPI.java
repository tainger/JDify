package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.AlarmRuleDTO;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/alarm", produces = {"application/json; charset=UTF-8"})
public interface AlarmRuleWriteAPI {

    @PostMapping
    @ApiOperation("新增报警策略")
    String create(@RequestBody AlarmRuleDTO AlarmDTO);

    @PutMapping
    @ApiOperation("更新报警策略")
    AlarmRuleDTO update(@RequestBody AlarmRuleDTO AlarmDTO);

    @DeleteMapping("/{id}")
    @ApiOperation("删除报警策略")
    void deleteById(@PathVariable String id);

}
