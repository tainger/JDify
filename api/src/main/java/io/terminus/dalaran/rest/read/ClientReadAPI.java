package io.terminus.dalaran.rest.read;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.ClientDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "/api/client", produces = {"application/json; charset=UTF-8"})
public interface ClientReadAPI {

    @GetMapping("/{id}")
    @ApiOperation("获取客户端详情")
    ClientDTO detail(@PathVariable Long id);
}
