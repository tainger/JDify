package io.terminus.dalaran.api.rest;

import io.terminus.dalaran.model.dto.log.MainLogDTO;
import io.terminus.dalaran.model.query.TracingLogQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping(value = "/api/log", produces = {"application/json; charset=UTF-8"})
public interface TracingRestAPI {

    @GetMapping
    public List<MainLogDTO> query(TracingLogQuery query);

    @GetMapping("/{recordId}")
    public MainLogDTO logDetail(@PathVariable String recordId);
}
