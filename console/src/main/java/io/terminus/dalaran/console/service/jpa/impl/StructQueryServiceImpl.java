package io.terminus.dalaran.console.service.jpa.impl;

import io.terminus.dalaran.console.entity.StructureEntity;
import io.terminus.dalaran.console.model.query.StructureQuery;
import io.terminus.dalaran.console.repository.specification.StructureQueryRepository;
import io.terminus.dalaran.console.service.jpa.StructQueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
@Service
public class StructQueryServiceImpl implements StructQueryService {

    @Autowired
    private StructureQueryRepository structureQueryRepository;

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
}
