package io.terminus.dalaran.trace;

import io.terminus.dalaran.TracingType;
import io.terminus.dalaran.model.DalaranTracingLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DalaranTracingLogRepository extends JpaRepository<DalaranTracingLog, Long> {

    List<DalaranTracingLog> findByTriggerIdAndTracingType(Long triggerId, TracingType tracingType);

    List<DalaranTracingLog> findByRecordIdAndTracingType(String recordId, TracingType tracingType);
}
