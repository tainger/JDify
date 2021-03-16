package io.terminus.dalaran.console.convertor;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.core.resource.entity.SubFlowAbstractEntity;
import io.terminus.dalaran.core.resource.entity.TriggerFlowAbstractEntity;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import io.terminus.dalaran.core.resource.entity.released.TriggerFlowReleasedEntity;
import io.terminus.dalaran.model.dto.ProcessorDTO;
import io.terminus.dalaran.model.dto.flow.ReleaseFlowDTO;
import io.terminus.dalaran.model.dto.flow.SubFlowDTO;
import io.terminus.dalaran.model.dto.flow.TriggerFlowDTO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlowConvertor {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public TriggerFlowDTO toDTO(TriggerFlowAbstractEntity entity) {
        TriggerFlowDTO flowModel = new TriggerFlowDTO();
        if (entity == null) {
            return flowModel;
        }
        List<ProcessorDTO> pipeline = new ArrayList<>();
        for (ProcessorEntity processorEntity : entity.getPipeline()) {
            ProcessorDTO processor = new ProcessorDTO();
            processor.setId(processorEntity.getId());
            processor.setType(processorEntity.getType());
            processor.setName(processorEntity.getName());
            processor.setConfig(JSON.parseObject(processorEntity.getConfig(), Map.class));
            pipeline.add(processor);
        }

        flowModel.setId(entity.getResourceKey());
        flowModel.setModuleId(entity.getModuleId());
        flowModel.setName(entity.getName());
        flowModel.setDescription(entity.getDescription());
        flowModel.setInModelId(entity.getInModel());
        flowModel.setOutModelId(entity.getOutModel());
        flowModel.setPipeline(pipeline);
        flowModel.setTracing(entity.isTracing());
        flowModel.setTriggerType(entity.getTriggerType());
        flowModel.setTriggerConfig(JSON.parseObject(entity.getTriggerConfig(), Map.class));
        return flowModel;
    }

    public SubFlowDTO toDTO(SubFlowAbstractEntity entity) {
        List<ProcessorDTO> pipeline = new ArrayList<>();
        for (ProcessorEntity processorEntity : entity.getPipeline()) {
            ProcessorDTO processor = new ProcessorDTO();
            processor.setId(processorEntity.getId());
            processor.setType(processorEntity.getType());
            processor.setName(processorEntity.getName());
            processor.setConfig(JSON.parseObject(processorEntity.getConfig(), Map.class));
            pipeline.add(processor);
        }

        SubFlowDTO flowModel = new SubFlowDTO();
        flowModel.setId(entity.getResourceKey());
        flowModel.setModuleId(entity.getModuleId());
        flowModel.setName(entity.getName());
        flowModel.setDescription(entity.getDescription());
        flowModel.setInModelId(entity.getInModel());
        flowModel.setOutModelId(entity.getOutModel());
        flowModel.setPipeline(pipeline);
        return flowModel;
    }

    public TriggerFlowDTO releaseToDTO(TriggerFlowReleasedEntity entity) {
        List<ProcessorDTO> pipeline = new ArrayList<>();
        for (ProcessorEntity processorEntity : entity.getPipeline()) {
            ProcessorDTO processor = new ProcessorDTO();
            processor.setId(processorEntity.getId());
            processor.setType(processorEntity.getType());
            processor.setName(processorEntity.getName());
            processor.setConfig(JSON.parseObject(processorEntity.getConfig(), Map.class));
            pipeline.add(processor);
        }

        TriggerFlowDTO flowModel = new TriggerFlowDTO();
        flowModel.setId(entity.getOriginId());
        flowModel.setModuleId(entity.getModuleId());
        flowModel.setName(entity.getName());
        flowModel.setDescription(entity.getDescription());
        flowModel.setInModelId(entity.getInModel());
        flowModel.setOutModelId(entity.getOutModel());
        flowModel.setPipeline(pipeline);
        flowModel.setTracing(entity.isTracing());
        flowModel.setTriggerType(entity.getTriggerType());
        flowModel.setTriggerConfig(JSON.parseObject(entity.getTriggerConfig(), Map.class));
        return flowModel;
    }

    public ReleaseFlowDTO releaseToDTOAndModuleName(TriggerFlowReleasedEntity entity) {
        List<ProcessorDTO> pipeline = new ArrayList<>();
        for (ProcessorEntity processorEntity : entity.getPipeline()) {
            ProcessorDTO processor = new ProcessorDTO();
            processor.setId(processorEntity.getId());
            processor.setType(processorEntity.getType());
            processor.setName(processorEntity.getName());
            processor.setConfig(JSON.parseObject(processorEntity.getConfig(), Map.class));
            pipeline.add(processor);
        }

        ReleaseFlowDTO flowModel = new ReleaseFlowDTO();
        flowModel.setId(entity.getOriginId());
        flowModel.setModuleId(entity.getModuleId());
        flowModel.setName(entity.getName());
        flowModel.setDescription(entity.getDescription());
        flowModel.setInModelId(entity.getInModel());
        flowModel.setOutModelId(entity.getOutModel());
        flowModel.setPipeline(pipeline);
        flowModel.setTracing(entity.isTracing());
        flowModel.setTriggerType(entity.getTriggerType());
        flowModel.setTriggerConfig(JSON.parseObject(entity.getTriggerConfig(), Map.class));
        flowModel.setCreatedAt(dateFormat.format(entity.getCreatedAt()));
        flowModel.setUpdatedAt(dateFormat.format(entity.getUpdatedAt()));
        return flowModel;
    }
}
