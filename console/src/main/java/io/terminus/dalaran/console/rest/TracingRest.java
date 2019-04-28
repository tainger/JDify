package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.model.dto.log.MainLogDTO;
import io.terminus.dalaran.console.model.query.TracingLogQuery;
import io.terminus.dalaran.console.service.TracingLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/log")
public class TracingRest {

    @Autowired
    private TracingLogService tracingLogService;

    @GetMapping
    private List<MainLogDTO> query(TracingLogQuery query) {
        return tracingLogService.triggerLogs(query);
    }

    @GetMapping("/{recordId}")
    private MainLogDTO logDetail(@PathVariable String recordId) {
        return tracingLogService.getRecordDetail(recordId);
    }
}
