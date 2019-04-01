package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.model.ModuleModel;
import io.terminus.dalaran.console.model.TriggerModel;
import io.terminus.dalaran.console.model.query.ModuleQuery;
import io.terminus.dalaran.console.model.query.TriggerQuery;
import io.terminus.dalaran.console.service.ModuleManagementService;
import io.terminus.dalaran.console.service.TriggerManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
@RestController
@RequestMapping("__/dalaran_management/module")
public class ModuleManagementRest {
    @Autowired
    private ModuleManagementService moduleManagementService;

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public List<ModuleModel> query(@RequestParam ModuleQuery query) {
        return moduleManagementService.queryModules(query);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void create(@RequestBody ModuleModel model) {
        moduleManagementService.createModule(model);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public void update(@RequestBody ModuleModel model) {
        moduleManagementService.updateModule(model);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public void delete(Long id) {
        moduleManagementService.deleteModule(id);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<ModuleModel> list() {
        return moduleManagementService.list();
    }
}
