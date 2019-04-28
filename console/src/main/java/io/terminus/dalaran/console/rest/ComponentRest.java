package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.model.config.ProcessorInfo;
import io.terminus.dalaran.model.config.TriggerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class ComponentRest {

    @Autowired
    private DalaranContext dalaranContext;

    @ApiOperation(value = "获取处理器初始化结构")
    @RequestMapping(value = "/processor/get/config", method = RequestMethod.GET)
    public ProcessorInfo getProcessorInfo(@RequestParam String type) {
        return dalaranContext.getDalaranComponentContext().getProcessorInfo(type);
    }

    @ApiOperation(value = "获取触发器初始化结构")
    @RequestMapping(value = "/trigger/get/config", method = RequestMethod.GET)
    public TriggerInfo getTriggerInfo(@RequestParam String type) {
        return dalaranContext.getDalaranComponentContext().getTriggerInfo(type);
    }
}
