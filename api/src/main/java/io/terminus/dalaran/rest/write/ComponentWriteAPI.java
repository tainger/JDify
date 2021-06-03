package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.CreateResponse;
import io.terminus.dalaran.model.common.BasicComponentType;
import io.terminus.dalaran.model.dto.ComponentDTO;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/component", produces = {"application/json; charset=UTF-8"})
public interface ComponentWriteAPI {
    @PostMapping
    @ApiOperation("新增")
    CreateResponse create(@RequestBody ComponentDTO componentDTO);

    @PutMapping
    @ApiOperation("更新")
    Object update(@RequestBody ComponentDTO componentDTO);

    @DeleteMapping("/{type}/{id}")
    @ApiOperation("删除")
    void deleteById(@PathVariable BasicComponentType type,  @PathVariable String id);
}
