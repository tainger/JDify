package io.terminus.dalaran.console.rest;

import com.google.gson.Gson;
import io.terminus.dalaran.console.entity.FlowEntity;
import io.terminus.dalaran.console.entity.ProcessorEntity;
import io.terminus.dalaran.console.entity.PropertyEntity;
import io.terminus.dalaran.console.entity.TriggerEntity;
import io.terminus.dalaran.console.model.FlowModel;
import io.terminus.dalaran.console.service.FlowManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/management/flow")
public class FlowManagementRest {

    @Autowired
    private FlowManagementService flowManagementService;

    private Gson gson = new Gson();

    @PutMapping
    public void saveFlow(@RequestBody FlowModel flowModel) {
        FlowEntity flowEntity = new FlowEntity();
        TriggerEntity triggerEntity = new TriggerEntity();

        Set<ProcessorEntity> processorEntitySet = flowModel.getProcessors().stream().map(processorModel -> {
            ProcessorEntity processorEntity = new ProcessorEntity();
            processorEntity.setType(processorModel.getType());
            processorEntity.setConfig(gson.toJson(processorModel.getConfig()));
            return processorEntity;
        }).collect(Collectors.toSet());

        Set<PropertyEntity> propertyEntitySet = flowModel.getProperties().entrySet().stream().map(entry -> {
            PropertyEntity propertyEntity = new PropertyEntity();
            propertyEntity.setName(entry.getKey());
            propertyEntity.setValue(entry.getValue());
            return propertyEntity;
        }).collect(Collectors.toSet());

        flowEntity.setTrigger(triggerEntity);
        flowEntity.setProcessors(processorEntitySet);
        flowEntity.setProperties(propertyEntitySet);

        flowEntity.setId(flowModel.getId());
        flowEntity.setName(flowModel.getName());
        flowEntity.setDescription(flowModel.getDescription());

        triggerEntity.setType(flowModel.getTrigger().getType());
        triggerEntity.setConfig(gson.toJson(flowModel.getTrigger().getConfig()));

        flowManagementService.saveFlow(flowEntity);
    }

    @PostMapping("/publish")
    void publish() {
        flowManagementService.publish();
    }

    @PostMapping("/saveAndPublish")
    void publish(@RequestBody FlowModel flowModel) {
        saveFlow(flowModel);
        flowManagementService.publish();
    }
}
