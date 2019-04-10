package io.terminus.dalaran.support.trace;

import io.terminus.dalaran.model.DalaranTracingLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DalaranTracingLogRepository extends JpaRepository<DalaranTracingLog, Long> {
}
