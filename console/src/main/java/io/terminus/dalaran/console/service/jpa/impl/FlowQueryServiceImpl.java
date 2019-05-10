package io.terminus.dalaran.console.service.jpa.impl;

import io.terminus.dalaran.console.model.dto.flow.BasicFlowInfo;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.service.jpa.FlowQueryService;
import io.terminus.dalaran.entity.manage.TriggerFlowEntity;
import io.terminus.dalaran.repository.TriggerFlowRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
@Service
public class FlowQueryServiceImpl implements FlowQueryService {

    @Autowired
    private TriggerFlowRepository flowQueryRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<TriggerFlowEntity> query(FlowQuery query) {

        Specification<TriggerFlowEntity> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNoneBlank(query.getType())) {
                Predicate type = criteriaBuilder.equal(root.get("type"), query.getType());
                predicates.add(type);
            }

            if (CollectionUtils.isNotEmpty(query.getFlowIds())) {
                Predicate flowIds = criteriaBuilder.and(root.get("id").in(query.getFlowIds()));
                predicates.add(flowIds);
            }

            if (query.getModuleId() != null) {
                Predicate moduleId = criteriaBuilder.equal(root.get("moduleId"), query.getModuleId());
                predicates.add(moduleId);
            }

            if (StringUtils.isNoneBlank(query.getName())) {
                Predicate name = criteriaBuilder.like(root.get("name"), "%" + query.getName() + "%");
                predicates.add(name);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));

        };

        return flowQueryRepository.findAll(specification);
    }

    @Override
    public List<TriggerFlowEntity> queryByProcessorIds(List<Long> processorIds) {
        Specification<TriggerFlowEntity> specification = (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.isNotEmpty(criteriaBuilder.function("JSON_SEARCH", List.class,
                    root.get("processors"), criteriaBuilder.literal(processorIds)));
            return criteriaBuilder.and(predicate);
        };
        return flowQueryRepository.findAll(specification);
    }

    @Override
    public List<BasicFlowInfo> listBasicInfoByModuleId(Long moduleId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BasicFlowInfo> criteriaQuery = builder.createQuery(BasicFlowInfo.class);
        Root<TriggerFlowEntity> root = criteriaQuery.from(TriggerFlowEntity.class);
        criteriaQuery.multiselect(root.get("id"), root.get("moduleId"), root.get("name"), root.get("status"), root.get("triggerType")).where(builder.equal(root.get("moduleId"), moduleId));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
