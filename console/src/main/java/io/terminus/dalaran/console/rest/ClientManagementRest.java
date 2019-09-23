package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.common.model.Response;
import io.terminus.dalaran.console.exception.DalaranException;
import io.terminus.dalaran.console.model.ResponseMessage;
import io.terminus.dalaran.console.model.dto.ClientDTO;
import io.terminus.dalaran.console.service.ClientManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client")
public class ClientManagementRest {

    @Autowired
    private ClientManagementService service;

    @PostMapping
    @ApiOperation("新增客户端")
    @DalaranException(value = ResponseMessage.CLIENT_CREATE_ERROR)
    public Long create(@RequestBody ClientDTO clientDTO) {
        return service.create(clientDTO);
    }

    @PutMapping
    @ApiOperation("更新客户端")
    @DalaranException(value = ResponseMessage.CLIENT_UPDATE_ERROR)
    public ClientDTO update(@RequestBody ClientDTO clientDTO) {
        return service.update(clientDTO);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除客户端")
    @DalaranException(value = ResponseMessage.CLIENT_DELETE_ERROR)
    public void create(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    @ApiOperation("获取客户端详情")
    @DalaranException(value = ResponseMessage.CLIENT_QUERY_ERROR)
    public ClientDTO detail(@PathVariable Long id) {
        return service.detail(id);
    }
}
