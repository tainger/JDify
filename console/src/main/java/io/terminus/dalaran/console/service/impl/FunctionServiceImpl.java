package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.entity.ClientEntity;
import io.terminus.dalaran.console.entity.FunctionEntity;
import io.terminus.dalaran.console.repository.FunctionRepository;
import io.terminus.dalaran.console.service.FunctionService;
import io.terminus.dalaran.core.context.DalaranFunctionContext;
import io.terminus.dalaran.model.dto.FunctionDTO;
import io.terminus.dalaran.model.dto.basic.BasicFunctionInfo;
import io.terminus.draco.web.autoconfig.context.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
    public Long create(FunctionDTO functionDTO) {
        FunctionEntity entity = toEntity(functionDTO);
        setCreatedBy(entity);
        repository.save(entity);
        functionContext.addCustomFunction(entity.getId(), entity.getType(), entity.getScript(), entity.getParams());
        return entity.getId();
    }

    @Override
    public FunctionDTO update(FunctionDTO functionDTO) {
        FunctionEntity entity = toEntity(functionDTO);
        setUpdatedBy(entity);
        repository.save(entity);
        functionContext.addCustomFunction(entity.getId(), entity.getType(), entity.getScript(), entity.getParams());
        return toDTO(entity);
    }

    @Override
    public void delete(Long functionId) {
        repository.deleteById(functionId);
    }

    @Override
    public FunctionDTO detail(Long functionId) {
        FunctionEntity entity = repository.findById(functionId).get();
        return toDTO(entity);
    }

    @Override
    public List<BasicFunctionInfo> listBasicInfoByModuleId(Long moduleId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BasicFunctionInfo> criteriaQuery = builder.createQuery(BasicFunctionInfo.class);
        Root<FunctionEntity> root = criteriaQuery.from(FunctionEntity.class);
        criteriaQuery.multiselect(root.get("id"), root.get("moduleId"), root.get("name"), root.get("description"),
                root.get("type"), root.get("params")).where(builder.equal(root.get("moduleId"), moduleId));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    private FunctionDTO toDTO(FunctionEntity entity) {
        FunctionDTO dto = new FunctionDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private FunctionEntity toEntity(FunctionDTO dto) {
        FunctionEntity entity = new FunctionEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    private void setCreatedBy(FunctionEntity functionEntity){
        if(UserContext.getUserInfo().getUsername()!=null){
            functionEntity.setCreatedBy(UserContext.getUserInfo().getUsername());
        }
    }

    private void setUpdatedBy(FunctionEntity functionEntity){
        if(UserContext.getUserInfo().getUsername()!=null){
            functionEntity.setUpdatedBy(UserContext.getUserInfo().getUsername());
        }
    }
}
