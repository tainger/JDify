package io.terminus.dalaran.console.convertor;

import com.google.gson.Gson;
import io.terminus.dalaran.console.model.dto.ProcessorDTO;
import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.entity.ProcessorEntity;
import io.terminus.dalaran.entity.flow.TriggerFlowSuperEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlowConvertor {

    private final Gson gson = new Gson();

    public TriggerFlowDTO toDTO(TriggerFlowSuperEntity entity) {
        List<ProcessorDTO> pipeline = new ArrayList<>();
        for (ProcessorEntity processorEntity : entity.getPipeline()) {
            ProcessorDTO processor = new ProcessorDTO();
            processor.setId(processorEntity.getId());
            processor.setType(processorEntity.getType());
            processor.setName(processorEntity.getName());
            processor.setConfig(gson.fromJson(processorEntity.getConfig(), Map.class));
            pipeline.add(processor);
        }

        TriggerFlowDTO flowModel = new TriggerFlowDTO();
        flowModel.setId(entity.getId());
        flowModel.setModuleId(entity.getModuleId());
        flowModel.setName(entity.getName());
        flowModel.setDescription(entity.getDescription());
        flowModel.setInModelId(entity.getInModel());
        flowModel.setOutModelId(entity.getInModel());
        flowModel.setPipeline(pipeline);
        flowModel.setTriggerType(entity.getTriggerType());
        flowModel.setTriggerConfig(gson.fromJson(entity.getTriggerConfig(), Map.class));
        return flowModel;
    }
}
