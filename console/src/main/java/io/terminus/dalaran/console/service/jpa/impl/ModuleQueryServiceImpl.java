package io.terminus.dalaran.console.service.jpa.impl;

import io.terminus.dalaran.console.entity.ModuleEntity;
import io.terminus.dalaran.console.model.query.ModuleQuery;
import io.terminus.dalaran.console.repository.specification.ModuleQueryRepository;
import io.terminus.dalaran.console.service.jpa.ModuleQueryService;
import org.apache.commons.collections.CollectionUtils;
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
    private ModuleQueryRepository moduleQueryRepository;

    @Override
    public List<ModuleEntity> query(ModuleQuery query) {
        Specification<ModuleEntity> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(query.getModuleIds())) {
                Predicate processorIds = criteriaBuilder.equal(root.get("id"), query.getModuleIds());
                predicates.add(processorIds);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        return moduleQueryRepository.findAll(specification);
    }
}
