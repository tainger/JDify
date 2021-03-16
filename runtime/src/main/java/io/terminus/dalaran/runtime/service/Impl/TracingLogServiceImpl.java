package io.terminus.dalaran.runtime.service.Impl;

import io.terminus.dalaran.TracingType;
import io.terminus.dalaran.core.resource.repository.TracingLogRepository;
import io.terminus.dalaran.runtime.service.TracingLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TracingLogServiceImpl implements TracingLogService {
    @Autowired
    private TracingLogRepository tracingLogRepository;

    @Override
    public Long countElapseLog(Date oneMinBeforeCurrent, Date now, String flowId, Long elapse) {
        return tracingLogRepository.count((root, query1, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("main"), Boolean.TRUE));
            predicates.add(builder.equal(root.get("flowId"), flowId));
            predicates.add(builder.equal(root.get("tracingType"), TracingType.Flow));
            predicates.add(builder.ge(root.get("timestamp"), oneMinBeforeCurrent.getTime()));
            predicates.add(builder.le(root.get("timestamp"), now.getTime()));
            predicates.add(builder.ge(root.get("elapsed"), elapse));
            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }

    @Override
    public Long countFailureLog(Date oneMinBeforeCurrent, Date now, String flowId) {
        return tracingLogRepository.count((root, query1, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("main"), Boolean.TRUE));
            predicates.add(builder.equal(root.get("flowId"), flowId));
            predicates.add(builder.equal(root.get("tracingType"), TracingType.Flow));
            predicates.add(builder.ge(root.get("timestamp"), oneMinBeforeCurrent.getTime()));
            predicates.add(builder.le(root.get("timestamp"), now.getTime()));
            predicates.add(builder.equal(root.get("successful"), false));
            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
