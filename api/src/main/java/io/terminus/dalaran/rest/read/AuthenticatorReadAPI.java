package io.terminus.dalaran.rest.read;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.AuthenticatorKeyResponse;
import io.terminus.dalaran.model.AuthenticatorValueResponse;
import io.terminus.dalaran.model.dto.AuthenticatorDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "/api/authenticator", produces = {"application/json; charset=UTF-8"})
public interface AuthenticatorReadAPI {

    @GetMapping("/{id}")
    @ApiOperation("获取鉴权器详情")
    AuthenticatorDTO detail(@PathVariable String id);

    @GetMapping("/key")
    @ApiOperation("获取动态key")
    AuthenticatorKeyResponse getKey();

    @GetMapping("/value")
    @ApiOperation("获取动态value")
    AuthenticatorValueResponse getValue();

}
