package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.DalaranAccount;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "/api/platform", produces = {"application/json; charset=UTF-8"})
public interface LoginAPI {

    @ApiOperation(value = "登陆验证")
    @PostMapping(value = "/login/auth")
    boolean loginAuth(@RequestBody DalaranAccount account);
}
