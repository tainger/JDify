package io.terminus.dalaran.repository;

import io.terminus.dalaran.TracingType;
import io.terminus.dalaran.entity.TracingLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TracingLogRepository extends JpaRepository<TracingLogEntity, Long>, JpaSpecificationExecutor<TracingLogEntity> {


    List<TracingLogEntity> findByFlowIdAndTracingType(Long flowId, TracingType tracingType);

    List<TracingLogEntity> findByRecordIdAndTracingType(String recordId, TracingType tracingType);

    TracingLogEntity findOneByRecordIdAndTracingType(String recordId, TracingType tracingType);

    TracingLogEntity findByRecordIdAndMainTrue(String recordId);
}
