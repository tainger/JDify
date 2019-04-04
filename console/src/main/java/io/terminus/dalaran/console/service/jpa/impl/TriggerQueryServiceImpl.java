package io.terminus.dalaran.console.service.jpa.impl;

import io.terminus.dalaran.console.entity.TriggerEntity;
import io.terminus.dalaran.console.model.query.TriggerQuery;
import io.terminus.dalaran.console.repository.specification.TriggerQueryRepository;
import io.terminus.dalaran.console.service.jpa.TriggerQueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
@Service
public class TriggerQueryServiceImpl implements TriggerQueryService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TriggerQueryRepository triggerQueryRepository;

    @Override
    public List<TriggerEntity> query(TriggerQuery query) {

        Specification<TriggerEntity> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query.getModuleId() != null) {
                Predicate moduleId = criteriaBuilder.equal(root.get("moduleId"), query.getModuleId());
                predicates.add(moduleId);
            }

            if (StringUtils.isNoneBlank(query.getType())) {
                Predicate type = criteriaBuilder.equal(root.get("type"), query.getType());
                predicates.add(type);
            }

            if (CollectionUtils.isNotEmpty(query.getTriggerIds())) {
                Predicate triggerIds = criteriaBuilder.and(root.get("id").in(query.getTriggerIds()));
                predicates.add(triggerIds);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        return triggerQueryRepository.findAll(specification);
    }
}
