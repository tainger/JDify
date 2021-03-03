package io.terminus.dalaran.rest.read;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.ModuleDTO;
import io.terminus.dalaran.model.dto.ModuleDetailDTO;
import io.terminus.dalaran.model.query.ModuleQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping(value = "/api/module", produces = {"application/json; charset=UTF-8"})
public interface ModuleReadAPI {

    @ApiOperation(value = "条件查询模块")
    @GetMapping(value = "/query")
    List<ModuleDTO> query(ModuleQuery query);

    @ApiOperation(value = "全量查询模块")
    @GetMapping(value = "/list")
    List<ModuleDTO> list();

    @ApiOperation(value = "获取模块的详情, 包括所有组件的基本信息")
    @GetMapping(value = "/{id}")
    ModuleDetailDTO moduleDetail(@PathVariable String id);
}
