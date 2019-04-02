package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.model.ProcessorModel;
import io.terminus.dalaran.console.model.query.ProcessorQuery;
import io.terminus.dalaran.console.service.ProcessorManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
@RestController
@RequestMapping("__/dalaran_management/processor")
public class ProcessorManagementRest {

    @Autowired
    private ProcessorManagementService processorManagementService;

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public List<ProcessorModel> query(ProcessorQuery query) {
        return processorManagementService.queryProcessors(query);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void create(@RequestBody ProcessorModel model) {
        processorManagementService.createProcessor(model);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public void update(@RequestBody ProcessorModel model) {
        processorManagementService.updateProcessor(model);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public void delete(Long id) {
        processorManagementService.deleteProcessor(id);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<ProcessorModel> list() {
        return processorManagementService.list();
    }
}
