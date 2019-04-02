package io.terminus.dalaran.console.rest;

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
@RequestMapping("__/dalaran_management/structure")
public class StructureManagementRest {

    @Autowired
    private StructureManagementService structureManagementService;

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public List<StructureModel> query(StructureQuery query) {
        return structureManagementService.queryStructures(query);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void create(@RequestBody StructureModel model) {
        structureManagementService.createStructure(model);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public void update(@RequestBody StructureModel model) {
        structureManagementService.updateStructure(model);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public void delete(Long id) {
        structureManagementService.deleteStructure(id);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<StructureModel> list() {
        return structureManagementService.list();
    }
}
