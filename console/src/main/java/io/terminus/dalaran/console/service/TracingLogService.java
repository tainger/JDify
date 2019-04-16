package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.TracingLog;
import io.terminus.dalaran.console.model.TriggerLog;
import io.terminus.dalaran.console.model.query.TracingLogQuery;

import java.util.List;

public interface TracingLogService {

    List<TriggerLog> triggerLogs(TracingLogQuery query);

    List<TracingLog> triggerTracingLogs(Long logId);

    List<TriggerLog> failedLog();

    List<TriggerLog> successfulLog();

}
