package io.terminus.dalaran.console.rest;

import io.terminus.common.model.Response;
import io.terminus.dalaran.console.model.ResponseMessage;
import io.terminus.dalaran.console.model.query.TracingLogQuery;
import io.terminus.dalaran.console.service.TracingLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/log")
public class TracingRest {

    @Autowired
    private TracingLogService tracingLogService;

    @GetMapping
    private Response query(TracingLogQuery query) {
        try {
            return Response.ok(tracingLogService.triggerLogs(query));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.TRACE_QUERY_ERROR);
        }
    }

    @GetMapping("/{recordId}")
    private Response logDetail(@PathVariable String recordId) {
        try {
            return Response.ok(tracingLogService.getRecordDetail(recordId));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.TRACE_QUERY_ERROR);
        }
    }
}
