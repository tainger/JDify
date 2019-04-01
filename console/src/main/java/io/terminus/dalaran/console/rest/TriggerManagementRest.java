package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.model.ProcessorModel;
import io.terminus.dalaran.console.model.TriggerModel;
import io.terminus.dalaran.console.model.query.ProcessorQuery;
import io.terminus.dalaran.console.model.query.TriggerQuery;
import io.terminus.dalaran.console.service.ProcessorManagementService;
import io.terminus.dalaran.console.service.TriggerManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
@RestController
@RequestMapping("__/dalaran_management/trigger")
public class TriggerManagementRest {

    @Autowired
    private TriggerManagementService triggerManagementService;

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public List<TriggerModel> query(@RequestParam TriggerQuery query) {
        return triggerManagementService.queryTriggers(query);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void create(@RequestBody TriggerModel model) {
        triggerManagementService.createTrigger(model);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public void update(@RequestBody TriggerModel model) {
        triggerManagementService.updateTrigger(model);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public void delete(Long id) {
        triggerManagementService.deleteTrigger(id);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<TriggerModel> list() {
        return triggerManagementService.list();
    }
}
