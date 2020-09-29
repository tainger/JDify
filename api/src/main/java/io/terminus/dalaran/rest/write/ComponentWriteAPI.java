package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.common.BasicComponentType;
import io.terminus.dalaran.model.dto.ComponentDTO;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/component", produces = {"application/json; charset=UTF-8"})
public interface ComponentWriteAPI {
    @PostMapping
    @ApiOperation("新增连接器")
    Long create(@RequestBody ComponentDTO componentDTO);

    @PutMapping
    @ApiOperation("更新连接器")
    Object update(@RequestBody ComponentDTO componentDTO);

    @DeleteMapping("/{type}/{id}")
    @ApiOperation("删除连接器")
    void deleteById(@PathVariable BasicComponentType type,  @PathVariable Long id);
}
