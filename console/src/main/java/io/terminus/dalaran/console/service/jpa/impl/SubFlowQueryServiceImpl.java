package io.terminus.dalaran.console.service.jpa.impl;

import io.terminus.dalaran.console.entity.SubFlowEntity;
import io.terminus.dalaran.console.repository.SubFlowRepository;
import io.terminus.dalaran.console.service.jpa.SubFlowQueryService;
import io.terminus.dalaran.console.service.jpa.model.QueryFlowInfo;
import io.terminus.dalaran.model.dto.basic.BasicFlowInfo;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubFlowQueryServiceImpl implements SubFlowQueryService {

    @Autowired
    private SubFlowRepository flowQueryRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<BasicFlowInfo> listBasicInfoByModuleId(String moduleId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<QueryFlowInfo> criteriaQuery = builder.createQuery(QueryFlowInfo.class);
        Root<SubFlowEntity> root = criteriaQuery.from(SubFlowEntity.class);
        criteriaQuery.multiselect(root.get("resourceKey"), root.get("moduleId"), root.get("name"), root.get("status"), root.get("isExist")).where(builder.equal(root.get("moduleId"), moduleId), builder.equal(root.get("isExist"),true));
        List<QueryFlowInfo> subFlows = entityManager.createQuery(criteriaQuery).getResultList();
        List<BasicFlowInfo> basicSubFlows = new ArrayList<>();
        subFlows.forEach(subFlow -> {
            BasicFlowInfo basicFlow = new BasicFlowInfo();
            try {
                BeanUtils.copyProperties(basicFlow, subFlow);
                basicFlow.setId(subFlow.getResourceKey());
                basicSubFlows.add(basicFlow);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return basicSubFlows;
    }
}
