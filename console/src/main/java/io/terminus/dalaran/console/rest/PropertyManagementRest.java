package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.common.model.Response;
import io.terminus.dalaran.console.model.ResponseMessage;
import io.terminus.dalaran.console.model.dto.PropertyDTO;
import io.terminus.dalaran.console.model.query.PropertyQuery;
import io.terminus.dalaran.console.service.PropertyManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public Response create(@RequestBody PropertyDTO model) {
        try {
            return Response.ok(propertyManagementService.createProperty(model));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.PROPERTY_CREATE_ERROR);
        }
    }

    @ApiOperation(value = "更新环境变量")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Response update(@RequestBody PropertyDTO model) {
        try {
            return Response.ok(propertyManagementService.updateProperty(model));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.PROPERTY_UPDATE_ERROR);
        }
    }

    @ApiOperation(value = "删除环境变量")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Response delete(@RequestBody Long id) {
        try {
            propertyManagementService.deleteProperty(id);
            return Response.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.PROPERTY_DELETE_ERROR);
        }
    }

    @ApiOperation(value = "条件查询环境变量")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Response query(PropertyQuery query) {
        try {
            return Response.ok(propertyManagementService.queryProperties(query));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.PROPERTY_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "全量查询环境变量")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Response list() {
        try {
            return Response.ok(propertyManagementService.list());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.PROCESSOR_QUERY_ERROR);
        }
    }
}
