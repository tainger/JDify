package io.terminus.dalaran.console.rest;

import com.google.gson.Gson;
import io.terminus.dalaran.console.entity.FlowEntity;
import io.terminus.dalaran.console.entity.ProcessorEntity;
import io.terminus.dalaran.console.entity.PropertyEntity;
import io.terminus.dalaran.console.entity.TriggerEntity;
import io.terminus.dalaran.console.model.FlowModel;
import io.terminus.dalaran.console.repository.ProcessorRepository;
import io.terminus.dalaran.console.repository.PropertyRepository;
import io.terminus.dalaran.console.service.FlowManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("__/dalaran_management/flow")
public class FlowManagementRest {

    // TODO for test
    @Autowired
    private ProcessorRepository processorRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private FlowManagementService flowManagementService;

    private Gson gson = new Gson();

    @PutMapping
    public void saveFlow(@RequestBody FlowModel flowModel) {
        FlowEntity flowEntity = new FlowEntity();
//        TriggerEntity triggerEntity = new TriggerEntity();

        List<Long> processorEntitySet = flowModel.getProcessors().stream().map(processorModel -> {
            ProcessorEntity processorEntity = new ProcessorEntity();
            processorEntity.setType(processorModel.getType());
            processorEntity.setConfig(gson.toJson(processorModel.getConfig()));
            processorRepository.save(processorEntity);
            return processorEntity.getId();
        }).collect(Collectors.toList());


        List<Long> propertyEntitySet = flowModel.getProperties().entrySet().stream().map(entry -> {
            PropertyEntity propertyEntity = new PropertyEntity();
            propertyEntity.setName(entry.getKey());
            propertyEntity.setValue(entry.getValue());
            propertyRepository.save(propertyEntity);
            return propertyEntity.getId();
        }).collect(Collectors.toList());

//        flow.setTrigger(triggerEntity);
        flowEntity.setProcessors(processorEntitySet);
        flowEntity.setProperties(propertyEntitySet);

        flowEntity.setId(flowModel.getId());
        flowEntity.setName(flowModel.getName());
        flowEntity.setDescription(flowModel.getDescription());
        flowEntity.setRetryable(flowModel.getRetryable());
        flowEntity.setMaxRetry(flowModel.getMaxRetry());
        flowEntity.setRetryDelay(flowModel.getRetryDelay());

//        triggerEntity.setType(flowModel.getTrigger().getType());
//        triggerEntity.setConfig(gson.toJson(flowModel.getTrigger().getConfig()));

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

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void create(@RequestBody FlowModel model) {
        flowManagementService.createFlow(model);
    }
}
