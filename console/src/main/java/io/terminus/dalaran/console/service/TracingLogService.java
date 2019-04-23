package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.TracingMainLog;
import io.terminus.dalaran.console.model.query.TracingLogQuery;

import java.util.List;

public interface TracingLogService {

    List<TracingMainLog> triggerLogs(TracingLogQuery query);

    TracingMainLog getRecordDetail(String recordId);
}
