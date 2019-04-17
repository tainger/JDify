package io.terminus.dalaran.console.service.jpa.impl;

import io.terminus.dalaran.console.model.query.PropertyQuery;
import io.terminus.dalaran.console.service.jpa.PropertyQueryService;
import io.terminus.dalaran.entity.ModuleEntity;
import io.terminus.dalaran.entity.PropertyEntity;
import io.terminus.dalaran.repository.PropertyRepository;
import io.terminus.dalaran.repository.specification.PropertyQueryRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jingdi on 2019/4/16
 */
public class PropertyQueryServiceImpl implements PropertyQueryService {

    @Autowired
    private PropertyQueryRepository propertyQueryRepository;

    @Override
    public List<PropertyEntity> query(PropertyQuery query) {
        Specification<PropertyEntity> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(query.getPropertyIds())) {
                Predicate processorIds = criteriaBuilder.and(root.get("id").in(query.getPropertyIds()));
                predicates.add(processorIds);
            }

            if (StringUtils.isNoneBlank(query.getName())) {
                Predicate name = criteriaBuilder.like(root.get("name"), "%" + query.getName() + "%");
                predicates.add(name);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        return propertyQueryRepository.findAll(specification);
    }
}
