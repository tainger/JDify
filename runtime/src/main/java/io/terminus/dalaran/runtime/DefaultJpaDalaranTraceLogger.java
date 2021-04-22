package io.terminus.dalaran.runtime;

import com.alibaba.fastjson.JSONObject;
import io.terminus.dalaran.TracingType;
import io.terminus.dalaran.core.component.config.AlarmRuleConfig;
import io.terminus.dalaran.core.log.DalaranTraceLogger;
import io.terminus.dalaran.core.log.DalaranTracingLog;
import io.terminus.dalaran.core.resource.entity.common.ReleaseRecordEntity;
import io.terminus.dalaran.core.resource.entity.common.TracingLogEntity;
import io.terminus.dalaran.core.resource.log.RequestID;
import io.terminus.dalaran.core.resource.redis.RedisService;
import io.terminus.dalaran.core.resource.redis.RedisUtil;
import io.terminus.dalaran.core.resource.repository.ReleaseRecordRepository;
import io.terminus.dalaran.core.resource.repository.TracingLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class DefaultJpaDalaranTraceLogger implements DalaranTraceLogger {

    @Autowired
    private TracingLogRepository tracingLogRepository;

    @Autowired
    private ReleaseRecordRepository releaseRecordRepository;

    @Autowired
    private RedisService redisService;

    private BlockingQueue<DalaranTracingLog> logQueue = new LinkedBlockingQueue<>();

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

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
                    tracingLog.setVersion(getCurrentVersion());
                    tracingLogRepository.save(toEntity(tracingLog));
                    alarmCount(tracingLog);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void alarmCount(DalaranTracingLog tracingLog) {
        if (tracingLog.isMain() && tracingLog.getTracingType() == TracingType.Flow) {
            String value = getAlarmConfigIfAlarmConfigKey(tracingLog.getFlowId());
            if (null == value) {
                return;
            }
            String format = simpleDateFormat.format(tracingLog.getTimestamp());
            String currentTime = redisService.getValue(RedisUtil.getCurrentTime());
            if (currentTime == null) {
                redisService.persistKey(RedisUtil.getCurrentTime(), format);
            } else if (!currentTime.equals(format)) {
                redisService.persistKey(RedisUtil.getCurrentTime(), format);
                redisService.persistKey(RedisUtil.getTimeToMonitor(), currentTime);
            }
            AlarmRuleConfig alarmRuleConfig = JSONObject.parseObject(value, AlarmRuleConfig.class);
            if (!tracingLog.isSuccessful()) {
                redisService.incrKey(
                        RedisUtil.getFailureKey(tracingLog.getFlowId(), format)
                );
            }
            log.error("---计算超时时间-{}-----", "time");
            Long triggerTimeOut = 0L;
            try {
               triggerTimeOut = getTriggerTimeOut(tracingLog, alarmRuleConfig);
            }catch (Exception e) {
                log.error("---报错:{}---", RequestID.getExceptionStackTrace(e));
            }
            log.error("---超时时间-{}-----", triggerTimeOut);
            if (triggerTimeOut != null && triggerTimeOut <= tracingLog.getElapsed()) {
                redisService.incrKey(
                        RedisUtil.getTimeOutKey(tracingLog.getFlowId(), format)
                );
            }
        }
    }

    private Long getTriggerTimeOut(DalaranTracingLog tracingLog, AlarmRuleConfig alarmRuleConfig) {
        String flowTimeOut = redisService.getValue(RedisUtil.getReleasedFlowIdsTimeOut());
        Map<String, Object> map = JSONObject.parseObject(flowTimeOut, Map.class);
        String triggerFlowTime = (String) map.get(tracingLog.getFlowId());
        if(null != triggerFlowTime) {
            return Long.valueOf(triggerFlowTime);
        }
        if(alarmRuleConfig.getTimeOutAlarm().getIsOpen()){
            return Long.valueOf(alarmRuleConfig.getTimeOutAlarm().getElapse());
        }
        return  null;
    }

    private String getAlarmConfigIfAlarmConfigKey(String flowId) {
        String alarmRuleId = redisService.getValue(RedisUtil.getAlarmConfigKey(flowId));
        if (alarmRuleId == null) {
            return null;
        }
        String alarmConfig = redisService.getValue(RedisUtil.getAlarmRuleKey(alarmRuleId));
        if (alarmConfig == null) {
            redisService.deleteKey(RedisUtil.getAlarmConfigKey(flowId));
            return null;
        }
        return alarmConfig;
    }

    public String getCurrentVersion() {
        ReleaseRecordEntity recordEntity = releaseRecordRepository.findByEnabledTrue();
        return recordEntity.getVersion();
    }

    private TracingLogEntity toEntity(DalaranTracingLog tracingLog) {
        TracingLogEntity logEntity = new TracingLogEntity();
        try {
            BeanUtils.copyProperties(logEntity, tracingLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logEntity;
    }
}
