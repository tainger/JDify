package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.console.model.PropertyModel;
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
    public Long create(@RequestBody PropertyModel model) {
        return propertyManagementService.createProperty(model);
    }

    @ApiOperation(value = "更新环境变量")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public PropertyModel update(@RequestBody PropertyModel model) {
        return propertyManagementService.updateProperty(model);
    }

    @ApiOperation(value = "删除环境变量")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public void delete(@RequestBody Long id) {
        propertyManagementService.deleteProperty(id);
    }

    @ApiOperation(value = "条件查询环境变量")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public List<PropertyModel> query(PropertyQuery query) {
        return propertyManagementService.queryProperties(query);
    }

    @ApiOperation(value = "全量查询环境变量")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<PropertyModel> list() {
        return propertyManagementService.list();
    }
}
