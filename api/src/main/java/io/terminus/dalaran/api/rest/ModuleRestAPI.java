package io.terminus.dalaran.api.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.ModuleDTO;
import io.terminus.dalaran.model.dto.ModuleDetailDTO;
import io.terminus.dalaran.model.query.ModuleQuery;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = "/api/module", produces = {"application/json; charset=UTF-8"})
public interface ModuleRestAPI {

    @ApiOperation(value = "条件查询模块")
    @GetMapping(value = "/query")
    List<ModuleDTO> query(ModuleQuery query);

    @ApiOperation(value = "创建模块")
    @PostMapping(value = "/create")
    Long create(@RequestBody ModuleDTO model);

    @ApiOperation(value = "更新模块")
    @PostMapping(value = "/update")
    ModuleDTO update(@RequestBody ModuleDTO model);

    @ApiOperation(value = "删除模块")
    @DeleteMapping(value = "/delete")
    void delete(@RequestParam Long id);

    @ApiOperation(value = "全量查询模块")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    List<ModuleDTO> list();

    @ApiOperation(value = "获取模块的详情, 包括所有组件的基本信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ModuleDetailDTO listComponent(@PathVariable Long id);
}
