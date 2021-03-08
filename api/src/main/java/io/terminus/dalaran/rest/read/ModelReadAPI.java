package io.terminus.dalaran.rest.read;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.ClassificationModel;
import io.terminus.dalaran.model.dto.ModelDTO;
import io.terminus.dalaran.model.query.ModelQuery;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@RequestMapping(value = "/api/model", produces = {"application/json; charset=UTF-8"})
public interface ModelReadAPI {

    @ApiOperation(value = "条件查询数据模型")
    @GetMapping(value = "/query")
    List<ModelDTO> query(ModelQuery query);

    @ApiOperation(value = "查询全部的数据模型")
    @GetMapping(value = "/list")
    List<ModelDTO> list();

    @ApiOperation(value = "全量查询某模块内的数据模型")
    @GetMapping(value = "/list/{moduleId}")
    List<ModelDTO> listByModuleId(@PathVariable String moduleId);

    @ApiOperation(value = "查询全部可编辑的数据模型")
    @GetMapping(value = "/list/public")
    List<ModelDTO> listEditable();

    @ApiOperation(value = "查询某模块内可编辑的数据模型")
    @GetMapping(value = "/list/{moduleId}/public")
    List<ModelDTO> listEditableByModuleId(@PathVariable String moduleId);

    @ApiOperation(value = "全量查询某模块内的分类数据模型")
    @GetMapping(value = "/list/classification/{moduleId}/")
    Map<String, ClassificationModel> listClassificationByModuleId(@PathVariable String moduleId);

    @ApiOperation(value = "根据模型匹配自动生成建议的映射")
    @GetMapping(value = "/suggestMapping")
    Map<String, String> suggestMapping(@RequestParam String sourceId, @RequestParam String targetId);

    @ApiOperation(value = "下载数据模型Excel模板样例")
    @GetMapping(value = "/download/excel-template")
    ResponseEntity<Resource> downloadExcelTemplate();
}
