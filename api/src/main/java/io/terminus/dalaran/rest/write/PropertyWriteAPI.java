package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.dto.PropertyDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value = "/api/property", produces = {"application/json; charset=UTF-8"})
public interface PropertyWriteAPI {

    @ApiOperation(value = "创建环境变量")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    Long create(@RequestBody PropertyDTO model);

    @ApiOperation(value = "更新环境变量")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    PropertyDTO update(@RequestBody PropertyDTO model);

    @ApiOperation(value = "删除环境变量")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    void deleteById(@RequestBody Long id);
}
