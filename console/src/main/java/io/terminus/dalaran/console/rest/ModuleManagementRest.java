package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.common.model.Response;
import io.terminus.dalaran.console.model.ResponseMessage;
import io.terminus.dalaran.console.model.dto.ModuleDTO;
import io.terminus.dalaran.console.model.query.ModuleQuery;
import io.terminus.dalaran.console.service.ModuleManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by jingdi on 2019/4/1
 */
@RestController
@RequestMapping("/api/module")
public class ModuleManagementRest {

    @Autowired
    private ModuleManagementService moduleManagementService;

    @ApiOperation(value = "条件查询模块")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Response query(ModuleQuery query) {
        try {
            return Response.ok(moduleManagementService.queryModules(query));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.MODULE_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "创建模块")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Response create(@RequestBody ModuleDTO model) {
        try {
            return Response.ok(moduleManagementService.createModule(model));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.MODULE_CREATE_ERROR);
        }
    }

    @ApiOperation(value = "更新模块")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Response update(@RequestBody ModuleDTO model) {
        try {
            return Response.ok(moduleManagementService.updateModule(model));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.MODULE_UPDATE_ERROR);
        }
    }

    @ApiOperation(value = "删除模块")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Response delete(@RequestParam Long id) {
        try {
            moduleManagementService.deleteModule(id);
            return Response.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.MODULE_DELETE_ERROR);
        }
    }

    @ApiOperation(value = "全量查询模块")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Response list() {
        try {
            return Response.ok(moduleManagementService.list());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.MODULE_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "获取模块的详情, 包括所有组件的基本信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response listComponent(@PathVariable Long id) {
        try {
            return Response.ok(moduleManagementService.getModuleDetail(id));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.MODULE_QUERY_ERROR);
        }
    }
}
