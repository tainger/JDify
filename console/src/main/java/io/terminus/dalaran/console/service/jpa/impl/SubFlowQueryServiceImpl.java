package io.terminus.dalaran.console.service.jpa.impl;

import io.terminus.dalaran.console.entity.SubFlowEntity;
import io.terminus.dalaran.console.repository.SubFlowRepository;
import io.terminus.dalaran.console.service.jpa.SubFlowQueryService;
import io.terminus.dalaran.model.dto.basic.BasicFlowInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
public class SubFlowQueryServiceImpl implements SubFlowQueryService {

    @Autowired
    private SubFlowRepository flowQueryRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<BasicFlowInfo> listBasicInfoByModuleId(Long moduleId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BasicFlowInfo> criteriaQuery = builder.createQuery(BasicFlowInfo.class);
        Root<SubFlowEntity> root = criteriaQuery.from(SubFlowEntity.class);
        criteriaQuery.multiselect(root.get("id"), root.get("moduleId"), root.get("name"), root.get("status")).where(builder.equal(root.get("moduleId"), moduleId));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
