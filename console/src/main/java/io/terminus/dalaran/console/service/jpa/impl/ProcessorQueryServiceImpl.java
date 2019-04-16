package io.terminus.dalaran.console.service.jpa.impl;

import io.terminus.dalaran.console.model.query.ProcessorQuery;
import io.terminus.dalaran.console.model.query.rst.ComponentInfo;
import io.terminus.dalaran.console.model.query.rst.ComponentType;
import io.terminus.dalaran.repository.specification.ProcessorQueryRepository;
import io.terminus.dalaran.console.service.jpa.ProcessorQueryService;
import io.terminus.dalaran.entity.ProcessorEntity;
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
 * Created by jingdi on 2019/3/29
 */
@Service
public class ProcessorQueryServiceImpl implements ProcessorQueryService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ProcessorQueryRepository processorQueryRepository;

    @Override
    public List<ProcessorEntity> query(ProcessorQuery query) {

        Specification<ProcessorEntity> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query.getModuleId() != null) {
                Predicate moduleId = criteriaBuilder.equal(root.get("moduleId"), query.getModuleId());
                predicates.add(moduleId);
            }

            if (StringUtils.isNoneBlank(query.getType())) {
                Predicate type = criteriaBuilder.equal(root.get("type"), query.getType());
                predicates.add(type);
            }

            if (CollectionUtils.isNotEmpty(query.getProcessorIds())) {
                Predicate processorIds = criteriaBuilder.and(root.get("id").in(query.getProcessorIds()));
                predicates.add(processorIds);
            }

            if (StringUtils.isNoneBlank(query.getName())) {
                Predicate name = criteriaBuilder.like(root.get("name"), "%" + query.getName() + "%");
                predicates.add(name);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return processorQueryRepository.findAll(specification);
    }

    public List<ProcessorEntity> query() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProcessorEntity> criteriaQuery = criteriaBuilder.createQuery(ProcessorEntity.class);
        Root<ProcessorEntity> root = criteriaQuery.from(ProcessorEntity.class);
        return new ArrayList<>();
    }

    @Override
    public List<ComponentType> getTypes(Long moduleId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ComponentType> criteriaQuery = builder.createQuery(ComponentType.class);
        Root<ProcessorEntity> root = criteriaQuery.from(ProcessorEntity.class);
        criteriaQuery.multiselect(root.get("type")).where(builder.equal(root.get("moduleId"), moduleId)).groupBy(root.get("type"));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<ComponentInfo> getBasicInfo(String type) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ComponentInfo> criteriaQuery = builder.createQuery(ComponentInfo.class);
        Root<ProcessorEntity> root = criteriaQuery.from(ProcessorEntity.class);
        criteriaQuery.multiselect(root.get("name"), root.get("status")).where(builder.equal(root.get("type"), type));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
