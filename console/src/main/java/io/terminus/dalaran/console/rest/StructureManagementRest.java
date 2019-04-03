package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.console.model.StructureModel;
import io.terminus.dalaran.console.model.TriggerModel;
import io.terminus.dalaran.console.model.query.StructureQuery;
import io.terminus.dalaran.console.model.query.TriggerQuery;
import io.terminus.dalaran.console.service.StructureManagementService;
import io.terminus.dalaran.console.service.TriggerManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
@RestController
@RequestMapping("dalaran_management/structure")
public class StructureManagementRest {

    @Autowired
    private StructureManagementService structureManagementService;

    @ApiOperation(value = "条件查询数据模型")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public List<StructureModel> query(StructureQuery query) {
        return structureManagementService.queryStructures(query);
    }

    @ApiOperation(value = "创建数据模型")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void create(@RequestBody StructureModel model) {
        structureManagementService.createStructure(model);
    }

    @ApiOperation(value = "更新数据模型")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public void update(@RequestBody StructureModel model) {
        structureManagementService.updateStructure(model);
    }

    @ApiOperation(value = "删除数据模型")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public void delete(Long id) {
        structureManagementService.deleteStructure(id);
    }

    @ApiOperation(value = "全量查询数据模型")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<StructureModel> list() {
        return structureManagementService.list();
    }
}
