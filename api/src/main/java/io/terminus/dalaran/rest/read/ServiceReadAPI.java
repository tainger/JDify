package io.terminus.dalaran.rest.read;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.ServiceDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping(value = "/api/service", produces = {"application/json; charset=UTF-8"})
public interface ServiceReadAPI {

    @GetMapping("/{id}")
    @ApiOperation("获取服务详情")
    ServiceDTO detail(@PathVariable Long id);

    @GetMapping
    @ApiOperation("获取所有服务")
    List<ServiceDTO> list();

    @GetMapping("/{id}/operation")
    @ApiOperation("获取服务可选项")
    List<String> operations(@PathVariable Long id);
}
