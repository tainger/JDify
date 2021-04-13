package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.entity.FunctionEntity;
import io.terminus.dalaran.console.repository.FunctionRepository;
import io.terminus.dalaran.console.service.FunctionService;
import io.terminus.dalaran.console.service.jpa.model.QueryFunctionInfo;
import io.terminus.dalaran.console.util.GenerateKeyUtils;
import io.terminus.dalaran.core.context.DalaranFunctionContext;
import io.terminus.dalaran.model.dto.FunctionDTO;
import io.terminus.dalaran.model.dto.basic.BasicFunctionInfo;
import io.terminus.draco.web.autoconfig.context.UserContext;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class FunctionServiceImpl implements FunctionService {

    @Autowired
    private FunctionRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DalaranFunctionContext functionContext;

    @Override
    public String create(FunctionDTO functionDTO) {
        FunctionEntity entity = toEntity(functionDTO);
        setCreatedBy(entity);
        repository.save(entity);
        functionContext.addCustomFunction(String.valueOf(entity.getResourceKey()), entity.getType(), entity.getScript(), entity.getParams());
        return entity.getResourceKey();
    }

    @Override
    public FunctionDTO update(FunctionDTO functionDTO) {
        FunctionEntity functionEntity = buildEntity(functionDTO);
        repository.save(functionEntity);
        functionContext.addCustomFunction(String.valueOf(functionEntity.getResourceKey()), functionEntity.getType(), functionEntity.getScript(), functionEntity.getParams());
        return functionDTO;
    }

    @Override
    public void delete(String functionId) {
        FunctionEntity functionEntity = repository.findByResourceKey(functionId);
        functionEntity.setExist(false);
        repository.save(functionEntity);
    }

    @Override
    public FunctionDTO detail(String functionId) {
        FunctionEntity entity = repository.findByResourceKey(functionId);
        return toDTO(entity);
    }

    @Override
    public List<BasicFunctionInfo> listBasicInfoByModuleId(String moduleId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<QueryFunctionInfo> criteriaQuery = builder.createQuery(QueryFunctionInfo.class);
        Root<FunctionEntity> root = criteriaQuery.from(FunctionEntity.class);
        criteriaQuery.multiselect(root.get("resourceKey"), root.get("moduleId"), root.get("name"), root.get("description"),
                root.get("type"), root.get("params"), root.get("isExist")).where(builder.equal(root.get("moduleId"), moduleId),
                builder.equal(root.get("isExist"),true));
        List<QueryFunctionInfo> functions = entityManager.createQuery(criteriaQuery).getResultList();
        List<BasicFunctionInfo> basicFunctions = new ArrayList<>();
        functions.forEach(function -> {
            BasicFunctionInfo basicFunction = new BasicFunctionInfo();
            try {
                BeanUtils.copyProperties(basicFunction, function);
                basicFunction.setId(function.getResourceKey());
                basicFunctions.add(basicFunction);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return basicFunctions;
    }

    private FunctionDTO toDTO(FunctionEntity entity) {
        FunctionDTO dto = new FunctionDTO();
        try {
            BeanUtils.copyProperties(entity, dto);
            dto.setId(entity.getResourceKey());
            dto.setExist(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }

    private FunctionEntity toEntity(FunctionDTO dto) {
        FunctionEntity entity = new FunctionEntity();
        try {
            BeanUtils.copyProperties(dto, entity);
            entity.setExist(true);
            String resourceKey = dto.getId();
            if (StringUtils.isBlank(resourceKey)) {
                resourceKey = GenerateKeyUtils.resourceKey();
            }
            entity.setResourceKey(resourceKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entity;
    }

    private void setCreatedBy(FunctionEntity functionEntity){
        if(UserContext.getUserInfo() != null && UserContext.getUserInfo().getUsername() != null){
            functionEntity.setCreatedBy(UserContext.getUserInfo().getUsername());
        }
    }

    private FunctionEntity buildEntity(FunctionDTO functionDTO){
        FunctionEntity functionEntity = repository.findByResourceKey(functionDTO.getId());
        functionEntity.setModuleId(functionDTO.getModuleId());
        functionEntity.setName(functionDTO.getName());
        functionEntity.setType(functionDTO.getType());
        functionEntity.setScript(functionDTO.getScript());
        functionEntity.setParams(functionDTO.getParams());
        functionEntity.setDescription(functionDTO.getDescription());
        if(UserContext.getUserInfo() != null && UserContext.getUserInfo().getUsername() != null){
            functionEntity.setUpdatedBy(UserContext.getUserInfo().getUsername());
        }
        return functionEntity;
    }
}
