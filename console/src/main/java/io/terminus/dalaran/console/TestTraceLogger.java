package io.terminus.dalaran.console;

import io.terminus.dalaran.DalaranTraceLogger;
import io.terminus.dalaran.entity.manage.TracingLogEntity;
import io.terminus.dalaran.model.DalaranTracingLog;
import io.terminus.dalaran.repository.TracingLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class TestTraceLogger implements DalaranTraceLogger {

    @Autowired
    private TracingLogRepository tracingLogRepository;

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
