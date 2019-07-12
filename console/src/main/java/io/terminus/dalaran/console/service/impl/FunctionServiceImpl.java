package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.entity.FunctionEntity;
import io.terminus.dalaran.console.model.dto.BasicFunctionInfo;
import io.terminus.dalaran.console.model.dto.FunctionDTO;
import io.terminus.dalaran.console.repository.FunctionRepository;
import io.terminus.dalaran.console.service.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class FunctionServiceImpl implements FunctionService {

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public Long create(FunctionDTO functionDTO) {
        return null;
    }

    @Override
    public FunctionDTO update(FunctionDTO functionDTO) {
        return null;
    }

    @Override
    public void delete(Long functionId) {

    }

    @Override
    public FunctionDTO detail(Long functionId) {
        return null;
    }

    @Override
    public List<BasicFunctionInfo> listBasicInfoByModuleId(Long moduleId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BasicFunctionInfo> criteriaQuery = builder.createQuery(BasicFunctionInfo.class);
        Root<FunctionEntity> root = criteriaQuery.from(FunctionEntity.class);
        criteriaQuery.multiselect(root.get("id"), root.get("moduleId"), root.get("name"), root.get("componentType"),
                root.get("componentName")).where(builder.equal(root.get("moduleId"), moduleId));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
