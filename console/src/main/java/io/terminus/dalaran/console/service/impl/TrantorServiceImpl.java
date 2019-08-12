package io.terminus.dalaran.console.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.terminus.dalaran.console.entity.ModelEntity;
import io.terminus.dalaran.console.entity.TrantorEntity;
import io.terminus.dalaran.console.model.dto.trantor.IntegrationInfoDTO;
import io.terminus.dalaran.console.model.dto.trantor.IntegrationPointDTO;
import io.terminus.dalaran.console.model.dto.trantor.TrantorModuleDTO;
import io.terminus.dalaran.console.repository.ModelRepository;
import io.terminus.dalaran.console.repository.TrantorRepository;
import io.terminus.dalaran.console.service.TrantorService;
import io.terminus.dalaran.model.BodyType;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.model.trantor.DalaranIntegrationInfo;
import io.terminus.dalaran.model.trantor.DalaranTrantorModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrantorServiceImpl implements TrantorService {

    @Autowired
    private TrantorRepository trantorRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Override
    public void saveTrantorIntegrationInfo(DalaranTrantorModule trantorModule) {
        TrantorEntity entity = trantorRepository.findByModuleKey(trantorModule.getKey());
        if (entity == null) {
            entity = new TrantorEntity();
        }
        entity.setModuleKey(trantorModule.getKey());
        entity.setName(trantorModule.getName());
        entity.setIntegrationInfos(buildIntegrations(trantorModule.getKey(), trantorModule.getIntegrations()));
        trantorRepository.save(entity);
    }

    @Override
    public List<TrantorModuleDTO> getAllModule() {
        return trantorRepository.findAll().stream().map(module -> {
            TrantorModuleDTO dto = new TrantorModuleDTO();
            dto.setKey(module.getModuleKey());
            dto.setName(module.getName());
            dto.setIntegrations(JSON.parseObject(module.getIntegrationInfos(), new TypeReference<List<IntegrationInfoDTO>>() {
            }));
            return dto;
        }).collect(Collectors.toList());
    }

    private String buildIntegrations(String moduleKey, List<DalaranIntegrationInfo> integrationInfos) {
        List<IntegrationInfoDTO> integrations = integrationInfos.stream().map(integration -> {
            IntegrationInfoDTO infoDTO = new IntegrationInfoDTO();
            infoDTO.setKey(integration.getKey());
            infoDTO.setName(integration.getName());
            List<IntegrationPointDTO> integrationPoints = integration.getIntegrationPoints().stream().map(point -> {
                IntegrationPointDTO pointDTO = new IntegrationPointDTO();
                String pointName = "_" + moduleKey + "_" + integration.getKey() + "_" + point.getKey();
                pointDTO.setKey(point.getKey());
                pointDTO.setName(point.getName());
                pointDTO.setInModelId(createOrUpdateModule(pointName + "_in", point.getParamType()));
                pointDTO.setOutModelId(createOrUpdateModule(pointName + "out", point.getReturnType()));
                return pointDTO;
            }).collect(Collectors.toList());
            infoDTO.setIntegrationPoints(integrationPoints);
            return infoDTO;
        }).collect(Collectors.toList());
        return JSON.toJSONString(integrations);
    }

    private Long createOrUpdateModule(String name, JsonSchema schema) {
        ModelEntity entity = modelRepository.findByNameAndModuleIdIsNull(name);
        if (entity == null) {
            entity = new ModelEntity();
        }
        entity.setName(name);
        entity.setType(BodyType.JSON);
        entity.setModelSchema(JSON.toJSONString(schema));
        modelRepository.save(entity);
        return entity.getId();
    }
}
