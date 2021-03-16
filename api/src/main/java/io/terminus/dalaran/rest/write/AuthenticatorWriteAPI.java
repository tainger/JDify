package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.CreateResponse;
import io.terminus.dalaran.model.dto.AuthenticatorDTO;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/authenticator", produces = {"application/json; charset=UTF-8"})
public interface AuthenticatorWriteAPI {

    @PostMapping
    @ApiOperation("新增鉴权器")
    CreateResponse create(@RequestBody AuthenticatorDTO authenticatorDTO);

    @PutMapping
    @ApiOperation("更新鉴权器")
    AuthenticatorDTO update(@RequestBody AuthenticatorDTO authenticatorDTO);

    @DeleteMapping("/{id}")
    @ApiOperation("删除鉴权器")
    void deleteById(@PathVariable String id);
}
