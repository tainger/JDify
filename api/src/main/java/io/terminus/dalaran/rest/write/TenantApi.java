package io.terminus.dalaran.rest.write;


import io.terminus.dalaran.model.BasicResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "/api/tenant", produces = {"application/json; charset=UTF-8"})
public interface TenantApi {

    @PostMapping(value = "/register")
    BasicResponse register();
}
