package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.console.model.TriggerModel;
import io.terminus.dalaran.console.model.query.TriggerQuery;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.console.service.TriggerManagementService;
import io.terminus.dalaran.model.config.TriggerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
@RestController
@RequestMapping("dalaran_management/trigger")
public class TriggerManagementRest {

    @Autowired
    private TriggerManagementService triggerManagementService;

    @Autowired
    private FlowManagementService flowManagementService;

    @ApiOperation(value = "条件查询触发器")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public List<TriggerModel> query(TriggerQuery query) {
        return triggerManagementService.queryTriggers(query);
    }

    @ApiOperation(value = "创建触发器")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void create(@RequestBody TriggerModel model) {
        triggerManagementService.createTrigger(model);
    }

    @ApiOperation(value = "更新触发器")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public void update(@RequestBody TriggerModel model) {
        triggerManagementService.updateTrigger(model);
    }

    @ApiOperation(value = "删除触发器")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public void delete(@RequestParam Long id) {
        triggerManagementService.deleteTrigger(id);
    }

    @ApiOperation(value = "全量查询触发器")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<TriggerModel> list() {
        return triggerManagementService.list();
    }

    @ApiOperation(value = "获取所有可用的触发器")
    @RequestMapping(value = "list/triggers", method = RequestMethod.GET)
    public Collection<TriggerInfo> listTriggers() {
        return triggerManagementService.listTriggers();
    }

    @ApiOperation(value = "获取触发器初始化结构")
    @RequestMapping(value = "/get/config", method = RequestMethod.GET)
    public TriggerInfo getTriggerInfo(String type) {
        return triggerManagementService.getTriggerInfo(type);
    }
}
