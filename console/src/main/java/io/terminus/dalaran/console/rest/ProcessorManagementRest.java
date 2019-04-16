package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.console.model.ProcessorModel;
import io.terminus.dalaran.console.model.query.ProcessorQuery;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.console.service.ProcessorManagementService;
import io.terminus.dalaran.model.config.ProcessorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;
import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
@RestController
@RequestMapping("dalaran_management/processor")
public class ProcessorManagementRest {

    @Autowired
    private ProcessorManagementService processorManagementService;

    @Autowired
    private FlowManagementService flowManagementService;

    @ApiOperation(value = "条件查询处理器")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public List<ProcessorModel> query(ProcessorQuery query) {
        return processorManagementService.queryProcessors(query);
    }

    @ApiOperation(value = "创建处理器")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void create(@RequestBody ProcessorModel model) {
        processorManagementService.createProcessor(model);
    }

    @ApiOperation(value = "更新处理器")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public void update(@RequestBody ProcessorModel model) {
        processorManagementService.updateProcessor(model);
    }

    @ApiOperation(value = "删除处理器")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public void delete(@RequestParam Long id) {
        processorManagementService.deleteProcessor(id);
    }

    @ApiOperation(value = "全量查询处理器")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<ProcessorModel> list() {
        return processorManagementService.list();
    }

    @ApiOperation(value = "获取所有可用的处理器")
    @RequestMapping(value = "/list/processors", method = RequestMethod.GET)
    public Collection<ProcessorInfo> listProcessors() {
        return processorManagementService.listProcessors();
    }

    @ApiOperation(value = "获取处理器初始化结构")
    @RequestMapping(value = "/get/config", method = RequestMethod.GET)
    public ProcessorInfo getProcessorInfo(@RequestParam String type) {
        return processorManagementService.getProcessorInfo(type);
    }
}
