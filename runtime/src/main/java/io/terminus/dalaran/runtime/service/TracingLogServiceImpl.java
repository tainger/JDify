package io.terminus.dalaran.runtime.service;

import io.terminus.dalaran.core.resource.repository.TracingLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TracingLogServiceImpl implements TracingLogService{
    @Autowired
    private TracingLogRepository tracingLogRepository;

    @Override
    public Long countElapseLog(Date oneMinBeforeCurrent, Date now, Long flowId, Long elapse) {
        return tracingLogRepository.count((root, query1, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.between(root.get("createdAt"), oneMinBeforeCurrent, now));
            predicates.add(builder.equal(root.get("flowId"), flowId));
            predicates.add(builder.equal(root.get("elapsed"), elapse));
            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }

    @Override
    public Long countFailureLog(Date oneMinBeforeCurrent, Date now, Long flowId) {
        return tracingLogRepository.count((root, query1, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.between(root.get("createdAt"), oneMinBeforeCurrent, now));
            predicates.add(builder.equal(root.get("flowId"), flowId));
            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
