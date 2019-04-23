package io.terminus.dalaran.trace;

import io.terminus.dalaran.DalaranTraceLogger;
import io.terminus.dalaran.entity.TracingLogEntity;
import io.terminus.dalaran.model.DalaranTracingLog;
import io.terminus.dalaran.repository.TracingLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class DefaultJpaDalaranTraceLogger implements DalaranTraceLogger {

    @Autowired
    private TracingLogRepository tracingLogRepository;

    private BlockingQueue<DalaranTracingLog> logQueue = new LinkedBlockingQueue<>();

    @Override
    public void log(DalaranTracingLog tracingLog) {
        logQueue.add(tracingLog);
    }

    // TODO 临时处理一下异步写, 理论上扔到 MQ 里, 由其他服务 insert to db 会更合适
    @PostConstruct
    private void insertLog() {
        new Thread(() -> {
            for (; ; ) {
                try {
                    DalaranTracingLog tracingLog = logQueue.take();
                    tracingLogRepository.save(toEntity(tracingLog));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private TracingLogEntity toEntity(DalaranTracingLog tracingLog) {
        TracingLogEntity logEntity = new TracingLogEntity();
        BeanUtils.copyProperties(tracingLog, logEntity);
        return logEntity;
    }
}
