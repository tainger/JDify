package io.terminus.dalaran.console;

import io.terminus.dalaran.core.log.DalaranTraceLogger;
import io.terminus.dalaran.core.log.DalaranTracingLog;
import io.terminus.dalaran.core.resource.entity.common.TracingLogEntity;
import io.terminus.dalaran.core.resource.repository.TracingLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

@Slf4j
public class TestTraceLogger implements DalaranTraceLogger {

    private TracingLogRepository tracingLogRepository;

    public TestTraceLogger(TracingLogRepository tracingLogRepository) {
        this.tracingLogRepository = tracingLogRepository;
    }

    @Override
    public void log(DalaranTracingLog tracingLog) {
        tracingLogRepository.save(toEntity(tracingLog));
    }

    private TracingLogEntity toEntity(DalaranTracingLog tracingLog) {
        TracingLogEntity logEntity = new TracingLogEntity();
        BeanUtils.copyProperties(tracingLog, logEntity);
        return logEntity;
    }
}
