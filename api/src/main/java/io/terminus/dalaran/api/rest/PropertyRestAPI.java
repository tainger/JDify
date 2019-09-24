package io.terminus.dalaran.api.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.PropertyDTO;
import io.terminus.dalaran.model.query.PropertyQuery;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@RequestMapping(value = "/api/property", produces = {"application/json; charset=UTF-8"})
public interface PropertyRestAPI {

    @ApiOperation(value = "创建环境变量")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    Long create(@RequestBody PropertyDTO model);

    @ApiOperation(value = "更新环境变量")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    PropertyDTO update(@RequestBody PropertyDTO model);

    @ApiOperation(value = "删除环境变量")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    void delete(@RequestBody Long id);

    @ApiOperation(value = "条件查询环境变量")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    List<PropertyDTO> query(PropertyQuery query);

    @ApiOperation(value = "全量查询环境变量")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    List<PropertyDTO> list();
}
