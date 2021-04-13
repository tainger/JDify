package io.terminus.dalaran.rest.read;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.BasicResponse;
import io.terminus.dalaran.model.common.OSSAccount;
import io.terminus.dalaran.model.dto.PropertyDTO;
import io.terminus.dalaran.model.query.PropertyQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@RequestMapping(value = "/api/property", produces = {"application/json; charset=UTF-8"})
public interface PropertyReadAPI {

    @ApiOperation(value = "条件查询环境变量")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    List<PropertyDTO> query(PropertyQuery query);

    @ApiOperation(value = "全量查询环境变量")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    List<PropertyDTO> list();

    @ApiOperation(value = "获取当前环境OSS信息")
    @GetMapping(value = "/oss")
    OSSAccount getOSSAccount();

    @ApiOperation(value = "获取组件市场前端地址")
    @GetMapping(value = "/market/host")
    BasicResponse getDalaranMarketUi();
}
