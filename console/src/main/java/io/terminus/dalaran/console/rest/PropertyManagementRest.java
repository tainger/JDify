package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.common.model.Response;
import io.terminus.dalaran.console.exception.DalaranException;
import io.terminus.dalaran.console.model.ResponseMessage;
import io.terminus.dalaran.console.model.dto.PropertyDTO;
import io.terminus.dalaran.console.model.query.PropertyQuery;
import io.terminus.dalaran.console.service.PropertyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by jingdi on 2019/4/17
 */
@RestController
@RequestMapping("/api/property")
public class PropertyManagementRest {

    @Autowired
    private PropertyManagementService propertyManagementService;

    @ApiOperation(value = "创建环境变量")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @DalaranException(value = ResponseMessage.PROPERTY_CREATE_ERROR)
    public Long create(@RequestBody PropertyDTO model) {
        return propertyManagementService.createProperty(model);
    }

    @ApiOperation(value = "更新环境变量")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @DalaranException(value = ResponseMessage.PROPERTY_UPDATE_ERROR)
    public PropertyDTO update(@RequestBody PropertyDTO model) {
        return propertyManagementService.updateProperty(model);
    }

    @ApiOperation(value = "删除环境变量")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @DalaranException(value = ResponseMessage.PROPERTY_DELETE_ERROR)
    public void delete(@RequestBody Long id) {
        propertyManagementService.deleteProperty(id);
    }

    @ApiOperation(value = "条件查询环境变量")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    @DalaranException(value = ResponseMessage.PROPERTY_QUERY_ERROR)
    public List<PropertyDTO> query(PropertyQuery query) {
        return propertyManagementService.queryProperties(query);
    }

    @ApiOperation(value = "全量查询环境变量")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @DalaranException(value = ResponseMessage.PROCESSOR_QUERY_ERROR)
    public List<PropertyDTO> list() {
        return propertyManagementService.list();
    }
}
