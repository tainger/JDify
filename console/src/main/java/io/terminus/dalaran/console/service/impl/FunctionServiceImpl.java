package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.entity.FunctionEntity;
import io.terminus.dalaran.console.model.dto.BasicFunctionInfo;
import io.terminus.dalaran.console.model.dto.FunctionDTO;
import io.terminus.dalaran.console.repository.FunctionRepository;
import io.terminus.dalaran.console.service.FunctionService;
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

    @Override
    public Long create(FunctionDTO functionDTO) {
        return repository.save(toEntity(functionDTO)).getId();
    }

    @Override
    public FunctionDTO update(FunctionDTO functionDTO) {
        FunctionEntity entity = toEntity(functionDTO);
        repository.save(entity);
        return toDTO(entity);
    }

    @Override
    public void delete(Long functionId) {
        repository.delete(functionId);
    }

    @Override
    public FunctionDTO detail(Long functionId) {
        FunctionEntity entity = repository.findOne(functionId);
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
}
