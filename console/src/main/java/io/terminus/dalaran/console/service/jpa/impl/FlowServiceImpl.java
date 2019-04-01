package io.terminus.dalaran.console.service.jpa.impl;

import io.terminus.dalaran.console.entity.FlowEntity;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.repository.specification.FlowQueryRepository;
import io.terminus.dalaran.console.service.jpa.FlowQueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
public class FlowServiceImpl implements FlowQueryService {

    @Autowired
    private FlowQueryRepository flowQueryRepository;

    @Override
    public List<FlowEntity> query(FlowQuery query) {

        Specification<FlowEntity> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNoneBlank(query.getType())) {
                Predicate type = criteriaBuilder.equal(root.get("type"), query.getType());
                predicates.add(type);
            }

            if (CollectionUtils.isNotEmpty(query.getFlowIds())) {
                Predicate flowIds = criteriaBuilder.equal(root.get("id"), query.getFlowIds());
                predicates.add(flowIds);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));

        };

        return flowQueryRepository.findAll(specification);
    }
}
