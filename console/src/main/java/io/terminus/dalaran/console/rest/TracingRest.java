package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.api.rest.TracingRestAPI;
import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.DalaranException;
import io.terminus.dalaran.console.service.TracingLogService;
import io.terminus.dalaran.model.dto.log.MainLogDTO;
import io.terminus.dalaran.model.query.TracingLogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TracingRest implements TracingRestAPI {

    @Autowired
    private TracingLogService tracingLogService;

    @DalaranException(value = ResponseMessage.TRACE_QUERY_ERROR)
    public List<MainLogDTO> query(TracingLogQuery query) {
        return tracingLogService.triggerLogs(query);
    }

    @DalaranException(value = ResponseMessage.TRACE_QUERY_ERROR)
    public MainLogDTO logDetail(@PathVariable String recordId) {
        return tracingLogService.getRecordDetail(recordId);
    }
}
