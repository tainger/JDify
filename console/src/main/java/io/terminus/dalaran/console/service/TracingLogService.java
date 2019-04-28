package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.dto.log.MainLogDTO;
import io.terminus.dalaran.console.model.query.TracingLogQuery;

import java.util.List;

public interface TracingLogService {

    List<MainLogDTO> triggerLogs(TracingLogQuery query);

    MainLogDTO getRecordDetail(String recordId);
}
