package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.console.model.ModuleModel;
import io.terminus.dalaran.console.model.query.ModuleQuery;
import io.terminus.dalaran.console.service.ModuleManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
@RestController
@RequestMapping("dalaran_management/module")
public class ModuleManagementRest {
    @Autowired
    private ModuleManagementService moduleManagementService;

    @ApiOperation(value = "条件查询模块")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public List<ModuleModel> query(ModuleQuery query) {
        return moduleManagementService.queryModules(query);
    }

    @ApiOperation(value = "创建模块")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void create(@RequestBody ModuleModel model) {
        moduleManagementService.createModule(model);
    }

    @ApiOperation(value = "更新模块")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public void update(@RequestBody ModuleModel model) {
        moduleManagementService.updateModule(model);
    }

    @ApiOperation(value = "删除模块")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public void delete(@RequestParam Long id) {
        moduleManagementService.deleteModule(id);
    }

    @ApiOperation(value = "全量查询模块")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<ModuleModel> list() {
        return moduleManagementService.list();
    }
}
