package io.terminus.dalaran.console.service.jpa.impl;

import io.terminus.dalaran.BodyModelType;
import io.terminus.dalaran.console.model.query.StructureQuery;
import io.terminus.dalaran.console.model.query.rst.ComponentInfo;
import io.terminus.dalaran.console.model.query.rst.type.StructureType;
import io.terminus.dalaran.console.service.jpa.StructQueryService;
import io.terminus.dalaran.entity.StructureEntity;
import io.terminus.dalaran.repository.StructureRepository;
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
public class StructQueryServiceImpl implements StructQueryService {

    @Autowired
    private StructureRepository structureQueryRepository;

    @Autowired
    private EntityManager entityManager;


    @Override
    public List<StructureEntity> query(StructureQuery query) {

        Specification<StructureEntity> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query.getModuleId() != null) {
                Predicate moduleId = criteriaBuilder.equal(root.get("moduleId"), query.getModuleId());
                predicates.add(moduleId);
            }

            if (CollectionUtils.isNotEmpty(query.getStructureIds())) {
                Predicate structureIds = criteriaBuilder.and(root.get("id").in(query.getStructureIds()));
                predicates.add(structureIds);
            }

            if (StringUtils.isNoneBlank(query.getName())) {
                Predicate name = criteriaBuilder.like(root.get("name"), "%" + query.getName() + "%");
                predicates.add(name);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        return structureQueryRepository.findAll(specification);
    }

    @Override
    public List<StructureType> getTypes(Long moduleId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<StructureType> criteriaQuery = builder.createQuery(StructureType.class);
        Root<StructureEntity> root = criteriaQuery.from(StructureEntity.class);
        criteriaQuery.multiselect(root.get("type")).where(builder.equal(root.get("moduleId"), moduleId)).groupBy(root.get("type"));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<ComponentInfo> getBasicInfo(BodyModelType type) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ComponentInfo> criteriaQuery = builder.createQuery(ComponentInfo.class);
        Root<StructureEntity> root = criteriaQuery.from(StructureEntity.class);
        criteriaQuery.multiselect(root.get("id"), root.get("name"), root.get("status")).where(builder.equal(root.get("type"), type));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
