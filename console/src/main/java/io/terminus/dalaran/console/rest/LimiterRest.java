package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.ResponseMessage;
import io.terminus.dalaran.console.exception.OnException;
import io.terminus.dalaran.console.service.LimiterService;
import io.terminus.dalaran.model.CreateResponse;
import io.terminus.dalaran.model.dto.LimiterDTO;
import io.terminus.dalaran.model.dto.basic.BasicLimiterInfo;
import io.terminus.dalaran.rest.read.LimiterReadAPI;
import io.terminus.dalaran.rest.write.LimiterWriteAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class LimiterRest implements LimiterReadAPI, LimiterWriteAPI {

    @Autowired
    private LimiterService limiterService;

    @Override
    @OnException(code = ResponseMessage.LIMITER_QUERY_ERROR)
    public LimiterDTO detail(@PathVariable String id) {
        return limiterService.detail(id);
    }

    @Override
    @OnException(code = ResponseMessage.LIMITER_QUERY_ERROR)
    public List<BasicLimiterInfo> selectOptions(@RequestParam String limiterType) {
        return limiterService.listBasicInfoByComponent(limiterType);
    }

    @Override
    @OnException(code = ResponseMessage.LIMITER_CREATE_ERROR)
    public CreateResponse create(@RequestBody LimiterDTO limiterDTO) {
        return new CreateResponse(limiterService.create(limiterDTO));
    }

    @Override
    @OnException(code = ResponseMessage.LIMITER_UPDATE_ERROR)
    public LimiterDTO update(@RequestBody LimiterDTO limiterDTO) {
        return limiterService.update(limiterDTO);
    }

    @Override
    @OnException(code = ResponseMessage.LIMITER_DELETE_ERROR)
    public void deleteById(@PathVariable String id) {
        limiterService.delete(id);
    }
}
