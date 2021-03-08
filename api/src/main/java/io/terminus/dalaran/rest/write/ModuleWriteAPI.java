package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.CreateResponse;
import io.terminus.dalaran.model.dto.ModuleDTO;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/module", produces = {"application/json; charset=UTF-8"})
public interface ModuleWriteAPI {

    @ApiOperation(value = "创建模块")
    @PostMapping(value = "/create")
    CreateResponse create(@RequestBody ModuleDTO model);

    @ApiOperation(value = "更新模块")
    @PostMapping(value = "/update")
    ModuleDTO update(@RequestBody ModuleDTO model);

    @ApiOperation(value = "删除模块")
    @DeleteMapping(value = "/delete")
    void deleteById(@RequestParam String id);
}
