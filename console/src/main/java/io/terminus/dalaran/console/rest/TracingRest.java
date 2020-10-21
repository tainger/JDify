package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.OnException;
import io.terminus.dalaran.console.service.TracingLogService;
import io.terminus.dalaran.model.dto.log.MainLogDTO;
import io.terminus.dalaran.model.query.TracingLogQuery;
import io.terminus.dalaran.rest.read.TracingReadAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TracingRest implements TracingReadAPI {

    @Autowired
    private TracingLogService tracingLogService;

    @OnException(code = ResponseMessage.TRACE_QUERY_ERROR)
    public Page<MainLogDTO> queryPageable(TracingLogQuery query, Integer pageNumber, Integer pageSize) {
        return tracingLogService.triggerLogsPageable(query, pageNumber, pageSize);
    }

    @OnException(code = ResponseMessage.TRACE_QUERY_ERROR)
    public List<MainLogDTO> query(TracingLogQuery query) {
        return tracingLogService.triggerLogs(query);
    }

    @OnException(code = ResponseMessage.TRACE_QUERY_ERROR)
    public Double queryAvgTime(TracingLogQuery query){
        return tracingLogService.getAvgElapsedTime(query);
    }

    @OnException(code = ResponseMessage.TRACE_QUERY_ERROR)
    public MainLogDTO logDetail(@PathVariable String recordId) {
        return tracingLogService.getRecordDetail(recordId);
    }
}
