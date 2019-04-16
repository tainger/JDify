package io.terminus.dalaran.trace;

import io.terminus.dalaran.model.DalaranTracingLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DalaranTracingLogRepository extends JpaRepository<DalaranTracingLog, Long> {
}
