package io.terminus.dalaran.console.service.jpa.impl;

import io.terminus.dalaran.console.model.query.ModuleQuery;
import io.terminus.dalaran.console.service.jpa.ModuleQueryService;
import io.terminus.dalaran.entity.ModuleEntity;
import io.terminus.dalaran.repository.ModuleRepository;
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
public class ModuleQueryServiceImpl implements ModuleQueryService {

    @Autowired
    private ModuleRepository moduleQueryRepository;

    @Override
    public List<ModuleEntity> query(ModuleQuery query) {
        Specification<ModuleEntity> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(query.getModuleIds())) {
                Predicate processorIds = criteriaBuilder.and(root.get("id").in(query.getModuleIds()));
                predicates.add(processorIds);
            }

            if (StringUtils.isNoneBlank(query.getName())) {
                Predicate name = criteriaBuilder.like(root.get("name"), "%" + query.getName() + "%");
                predicates.add(name);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        return moduleQueryRepository.findAll(specification);
    }
}
