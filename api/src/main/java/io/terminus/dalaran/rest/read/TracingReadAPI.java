package io.terminus.dalaran.rest.read;

import io.terminus.dalaran.model.dto.log.MainLogDTO;
import io.terminus.dalaran.model.query.TracingLogQuery;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping(value = "/api/log", produces = {"application/json; charset=UTF-8"})
public interface TracingReadAPI {

    @GetMapping("/pageable")
    Page<MainLogDTO> queryPageable(TracingLogQuery query);

    @GetMapping
    List<MainLogDTO> query(TracingLogQuery query);

    @GetMapping("/{recordId}")
    MainLogDTO logDetail(@PathVariable String recordId);
}
