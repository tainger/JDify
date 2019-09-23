package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.console.TestFlowInitializer;
import io.terminus.dalaran.console.convertor.FlowConvertor;
import io.terminus.dalaran.console.entity.ModelEntity;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.console.model.ModelImportMode;
import io.terminus.dalaran.console.model.dto.*;
import io.terminus.dalaran.console.model.dto.basic.BasicFlowInfo;
import io.terminus.dalaran.console.model.dto.flow.ImportFlowDTO;
import io.terminus.dalaran.console.model.dto.flow.TriggerFlowDTO;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.repository.ModelRepository;
import io.terminus.dalaran.console.repository.PropertyRepository;
import io.terminus.dalaran.console.repository.TriggerFlowRepository;
import io.terminus.dalaran.console.service.FlowManagementService;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.console.service.jpa.FlowQueryService;
import io.terminus.dalaran.core.context.DalaranContext;
import io.terminus.dalaran.core.flow.DalaranFlowBuilder;
import io.terminus.dalaran.core.resource.DalaranResourceBuilder;
import io.terminus.dalaran.core.resource.entity.common.ProcessorEntity;
import io.terminus.dalaran.core.resource.repository.ModuleRepository;
import io.terminus.dalaran.model.flow.FlowStatus;
import io.terminus.dalaran.model.flow.FlowValidation;
import io.terminus.dalaran.model.flow.TriggerFlow;
import io.terminus.dalaran.model.flow.ValidateMessageLevel;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static io.terminus.dalaran.console.model.ModelImportMode.Overwrite;
import static io.terminus.dalaran.console.model.ModelImportMode.Rename;

@Service
@Transactional
public class FlowManagementServiceImpl implements FlowManagementService {

    @Autowired
    private TriggerFlowRepository flowRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private DalaranContext dalaranContext;

    @Autowired
    private FlowQueryService flowQueryService;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private TestFlowInitializer testFlowInitializer;

    @Autowired
    private DalaranFlowBuilder flowBuilder;

    @Autowired
    private DalaranResourceBuilder resourceBuilder;

    @Autowired
    private ModelManagementService modelService;

    private final FlowConvertor flowConvertor = new FlowConvertor();

    @Override
    public List<TriggerFlowDTO> queryFlows(FlowQuery query) {
        List<TriggerFlowEntity> entities = flowQueryService.query(query);
        List<TriggerFlowDTO> models = new LinkedList<>();
        for (TriggerFlowEntity entity : entities) {
            models.add(flowConvertor.toDTO(entity));
        }
        return models;
    }

    @Override
    public Long saveFlow(TriggerFlowEntity flowEntity) {
        setFlowStatus(flowEntity);
        Long id = flowRepository.save(flowEntity).getId();
        // TODO 这里依赖 loader 有点怪 而且可以异步
        if (flowEntity.getStatus() != FlowStatus.Error) {
            testFlowInitializer.reloadTestTriggerFlow(flowEntity.getId());
        }
        return id;
    }

    @Override
    public Long createFlow(TriggerFlowDTO flowModel) {
        TriggerFlowEntity flowEntity = buildEntity(flowModel);
        setFlowStatus(flowEntity);
        Long id = flowRepository.save(flowEntity).getId();
        // TODO 这里依赖 loader 有点怪 而且可以异步
        if (flowEntity.getStatus() != FlowStatus.Error) {
            testFlowInitializer.reloadTestTriggerFlow(flowEntity.getId());
        }
        return id;
    }

    // TODO 很多重复内容 逻辑也比较尴尬, 各种 magic, 先测试一波, 有时间改改
    @Override
    public ImportFlowResult importFlow(ImportFlowDTO importInfo) {
        ImportFlowResult result = new ImportFlowResult();
        Map<String, Object> config = importInfo.getTriggerConfig();
        List<String> existModels = checkExistModels(importInfo);
        if (!existModels.isEmpty()) {
            result.setExistModels(existModels);
            result.setFlowId(-1L);
            return result;
        }
        TriggerFlowDTO triggerFlowDTO = new TriggerFlowDTO();

        ModelDTO inModel = importInfo.getInModel();
        if (inModel != null) {
            inModel.setModuleId(importInfo.getModuleId());
            Long modelId = importModel(inModel, importInfo.getImportMode());
            config.put("inModelId", modelId);
            triggerFlowDTO.setInModelId(modelId);
        }
        ModelDTO outModel = importInfo.getOutModel();
        if (outModel != null) {
            outModel.setModuleId(importInfo.getModuleId());
            Long modelId = importModel(outModel, importInfo.getImportMode());
            config.put("outModelId", modelId);
            triggerFlowDTO.setOutModelId(modelId);
        }

        triggerFlowDTO.setTriggerType(importInfo.getTriggerType());
        triggerFlowDTO.setTriggerConfig(importInfo.getTriggerConfig());
        triggerFlowDTO.setModuleId(importInfo.getModuleId());
        triggerFlowDTO.setName(importInfo.getName());
        triggerFlowDTO.setDescription(importInfo.getDescription());
        List<ProcessorDTO> pipeline = new ArrayList<>();
        if (StringUtils.isNotBlank(importInfo.getProcessorType())) {
            ProcessorDTO processor = new ProcessorDTO();
            processor.setId(RandomStringUtils.randomAlphanumeric(6));
            processor.setType(importInfo.getProcessorType());
            processor.setConfig(importInfo.getProcessorConfig());
            ModelDTO processorInModel = importInfo.getProcessorInModel();
            if (processorInModel != null) {
                processorInModel.setModuleId(importInfo.getModuleId());
                Long modelId = importModel(processorInModel, importInfo.getImportMode());
                processor.getConfig().put("inModelId", modelId);
                if (inModel != null) {
                    ProcessorDTO inMapper = new ProcessorDTO();
                    inMapper.setId(RandomStringUtils.randomAlphanumeric(6));
                    inMapper.setType("mapper-convert");
                    inMapper.setConfig(new HashMap<>());
                    inMapper.getConfig().put("inModelId", inModel.getId());
                    inMapper.getConfig().put("outModelId", modelId);
                    inMapper.getConfig().put("messageMapping", new HashMap<>());
                    pipeline.add(inMapper);
                }
            }
            pipeline.add(processor);
            ModelDTO processorOutModel = importInfo.getProcessorOutModel();
            if (processorOutModel != null) {
                processorOutModel.setModuleId(importInfo.getModuleId());
                Long modelId = importModel(processorOutModel, importInfo.getImportMode());
                processor.getConfig().put("outModelId", modelId);
                if (outModel != null) {
                    ProcessorDTO outMapper = new ProcessorDTO();
                    outMapper.setId(RandomStringUtils.randomAlphanumeric(6));
                    outMapper.setType("mapper-convert");
                    outMapper.setConfig(new HashMap<>());
                    outMapper.getConfig().put("inModelId", modelId);
                    outMapper.getConfig().put("outModelId", outModel.getId());
                    outMapper.getConfig().put("messageMapping", new HashMap<>());
                    pipeline.add(outMapper);
                }
            }
        }
        triggerFlowDTO.setPipeline(pipeline);
        Long flowId = createFlow(triggerFlowDTO);
        result.setFlowId(flowId);
        return result;
    }

    @Override
    public ImportProcessorResult importProcessor(ImportProcessorDTO importInfo) {
        ImportProcessorResult result = new ImportProcessorResult();
        Map<String, Object> config = importInfo.getProcessorConfig();
        List<String> existModels = checkExistModels(importInfo);
        if (!existModels.isEmpty()) {
            result.setExistModels(existModels);
            return result;
        }
        ModelDTO inModel = importInfo.getInModel();
        if (inModel != null) {
            inModel.setModuleId(importInfo.getModuleId());
            Long modelId = importModel(inModel, importInfo.getImportMode());
            config.put("inModelId", modelId);
        }
        ModelDTO outModel = importInfo.getOutModel();
        if (outModel != null) {
            outModel.setModuleId(importInfo.getModuleId());
            Long modelId = importModel(outModel, importInfo.getImportMode());
            config.put("outModelId", modelId);
        }
        ProcessorDTO processor = new ProcessorDTO();
        processor.setConfig(config);
        processor.setType(importInfo.getProcessorType());
        result.setProcessor(processor);
        return result;
    }

    private List<String> checkExistModels(ImportInfo importInfo) {
        List<String> existModels = new ArrayList<>();
        if (importInfo.getImportMode() != null) {
            return existModels;
        }
        ModelDTO inModel = importInfo.getInModel();
        if (inModel != null) {
            inModel.setModuleId(importInfo.getModuleId());
            ModelEntity inModelEntity = modelRepository.findByModuleIdAndModelKey(importInfo.getModuleId(), inModel.getModelKey());
            if (inModelEntity != null) {
                existModels.add(inModelEntity.getModelKey());
            }
        }
        ModelDTO outModel = importInfo.getOutModel();
        if (outModel != null) {
            outModel.setModuleId(importInfo.getModuleId());
            ModelEntity outModelEntity = modelRepository.findByModuleIdAndModelKey(importInfo.getModuleId(), outModel.getModelKey());
            if (outModelEntity != null) {
                existModels.add(outModelEntity.getModelKey());
            }
        }
        return existModels;
    }

    private Long importModel(ModelDTO model, ModelImportMode importMode) {
        ModelEntity outModelEntity = modelRepository.findByModuleIdAndModelKey(model.getModuleId(), model.getModelKey());
        if (outModelEntity == null) {
            return modelService.createModel(model);
        } else if (importMode == Overwrite) {
            model.setId(outModelEntity.getId());
            modelService.updateModel(model);
            return model.getId();
        } else if (importMode == Rename) {
            String randomId = RandomStringUtils.randomAlphanumeric(6);
            model.setName(model.getName() + "-" + randomId);
            model.setModelKey(model.getModelKey() + "-" + randomId);
            return modelService.createModel(model);
        }
        return null;
    }

    @Override
    public void deleteFlow(Long flowId) {
        flowRepository.deleteById(flowId);
    }

    @Override
    public TriggerFlowDTO updateFlow(TriggerFlowDTO flowModel) {
        TriggerFlowEntity flowEntity = buildEntity(flowModel);
        setFlowStatus(flowEntity);
        flowRepository.save(flowEntity);
        // TODO 这里依赖 loader 有点怪 而且可以异步
        testFlowInitializer.reloadTestTriggerFlow(flowEntity.getId());
        return flowModel;
    }

    @Override
    public List<TriggerFlowDTO> list() {
        List<TriggerFlowEntity> entities = flowRepository.findAll();
        List<TriggerFlowDTO> models = new LinkedList<>();
        for (TriggerFlowEntity entity : entities) {
            models.add(flowConvertor.toDTO(entity));
        }

        return models;
    }

    @Override
    public List<TriggerFlowDTO> queryByProcessorIds(List<Long> processorIds) {
        List<TriggerFlowEntity> entities = flowQueryService.queryByProcessorIds(processorIds);
        List<TriggerFlowDTO> models = new LinkedList<>();
        for (TriggerFlowEntity entity : entities) {
            models.add(flowConvertor.toDTO(entity));
        }

        return models;
    }

    @Override
    public List<BasicFlowInfo> listBasicFlowInfoByModuleId(Long moduleId) {
        return flowQueryService.listBasicInfoByModuleId(moduleId);
    }

    @Nullable
    @Override
    public TriggerFlowDTO getById(Long flowId) {
        TriggerFlowEntity flowEntity = flowRepository.findById(flowId).get();
        if (flowEntity == null) {
            return null;
        }
        return flowConvertor.toDTO(flowEntity);
    }

    @Override
    public Long copyFlow(CopyFlow copyFlow) {
        TriggerFlowEntity flowEntity = flowRepository.findById(copyFlow.getId()).get();
        if (flowEntity == null) {
            return null;
        }
        TriggerFlowEntity newFlowEntity = new TriggerFlowEntity();

        BeanUtils.copyProperties(flowEntity, newFlowEntity);
        newFlowEntity.setId(null);
        newFlowEntity.setName(copyFlow.getName());
        flowRepository.save(newFlowEntity);
        return newFlowEntity.getId();
    }

    @Override
    public List<FlowValidation> validateFlow(TriggerFlowDTO model) {
        model.setId(null);
        TriggerFlowEntity entity = buildEntity(model);
        return validateFlow(entity);
    }

    private void setFlowStatus(TriggerFlowEntity flowEntity) {
        FlowStatus flowStatus = null;
        for (FlowValidation flowValidation : validateFlow(flowEntity)) {
            if (flowValidation.getMessage().getLevel() == ValidateMessageLevel.Error) {
                flowEntity.setStatus(FlowStatus.Error);
                return;
            } else {
                flowStatus = FlowStatus.Warning;
            }
        }
        if (flowStatus == null) {
            flowStatus = FlowStatus.Available;
        }
        flowEntity.setStatus(flowStatus);
    }

    private List<FlowValidation> validateFlow(TriggerFlowEntity entity) {
        TriggerFlow triggerFlow = resourceBuilder.buildTriggerFlow(entity);
        return flowBuilder.validateFlow(triggerFlow);
    }

    private TriggerFlowEntity buildEntity(TriggerFlowDTO triggerFlow) {
        TriggerFlowEntity flowEntity;
        Long id = triggerFlow.getId();
        if (id != null) {
            flowEntity = flowRepository.findById(id).get();
        } else {
            flowEntity = new TriggerFlowEntity();
        }

        List<ProcessorEntity> pipeline = triggerFlow.getPipeline().stream().map(processor -> {
            ProcessorEntity processorEntity = new ProcessorEntity();
            processorEntity.setId(processor.getId());
            processorEntity.setType(processor.getType());
            processorEntity.setName(processor.getName());
            processorEntity.setConfig(JSON.toJSONString(processor.getConfig()));
            return processorEntity;
        }).collect(Collectors.toList());

        String name = triggerFlow.getName();
        if (StringUtils.isNoneBlank(name)) {
            flowEntity.setName(name);
        } else {
            flowEntity.setName("Dalaran Flow");
        }
        flowEntity.setTriggerType(triggerFlow.getTriggerType());
        flowEntity.setTriggerConfig(JSON.toJSONString(triggerFlow.getTriggerConfig()));
        flowEntity.setModuleId(triggerFlow.getModuleId());
        flowEntity.setInModel(triggerFlow.getInModelId());
        flowEntity.setOutModel(triggerFlow.getOutModelId());
        flowEntity.setPipeline(pipeline);
        flowEntity.setTracing(triggerFlow.isTracing());
        flowEntity.setDescription(triggerFlow.getDescription());

        return flowEntity;
    }

    private ModelDTO buildModelEntity(ModelEntity entity) {
        ModelDTO model = new ModelDTO();
        model.setId(entity.getId());
        model.setModelType(entity.getType());
        model.setModelSchema(JSON.parseObject(entity.getModelSchema(), Map.class));
        model.setName(entity.getName());
        model.setModuleId(entity.getModuleId());
        model.setDescription(entity.getDescription());
        return model;
    }
}
