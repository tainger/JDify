package io.terminus.dalaran.rest.read;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.log.DetailLogDTO;
import io.terminus.dalaran.model.dto.log.MainLogDTO;
import io.terminus.dalaran.model.query.TracingLogQuery;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping(value = "/api/log", produces = {"application/json; charset=UTF-8"})
public interface TracingReadAPI {

    @GetMapping("/pageable")
    Page<MainLogDTO> queryPageable(TracingLogQuery query, @RequestParam Integer pageNumber, @RequestParam Integer pageSize);

    @GetMapping
    List<MainLogDTO> query(TracingLogQuery query);

    @GetMapping("/{recordId}")
    MainLogDTO logDetail(@PathVariable String recordId);

    @ApiOperation(value = "流程统计分页")
    @GetMapping("/detail/{id}")
    DetailLogDTO logDetailById(@PathVariable String id);

}
