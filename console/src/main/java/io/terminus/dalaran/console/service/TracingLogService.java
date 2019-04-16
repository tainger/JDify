package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.TracingLog;
import io.terminus.dalaran.console.model.TriggerLog;

import java.util.List;

public interface TracingLogService {

    List<TriggerLog> triggerLogs(Long triggerId);

    List<TracingLog> triggerTracingLogs(Long triggerId);

    List<TriggerLog> failedLog();

    List<TriggerLog> successfulLog();

}
