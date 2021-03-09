package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.dto.log.DetailLogDTO;
import io.terminus.dalaran.model.dto.log.MainLogDTO;
import io.terminus.dalaran.model.dto.log.TimeLogDTO;
import io.terminus.dalaran.model.query.TracingLogQuery;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TracingLogService {

    Page<MainLogDTO> triggerLogsPageable(TracingLogQuery query, Integer pageNumber, Integer pageSize);

    List<MainLogDTO> triggerLogs(TracingLogQuery query);

    MainLogDTO getRecordDetail(String recordId);

    TimeLogDTO getElapsedTime(TracingLogQuery query);


    DetailLogDTO logDetailById(String id);
}