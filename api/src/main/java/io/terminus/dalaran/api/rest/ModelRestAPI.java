package io.terminus.dalaran.api.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.BodyType;
import io.terminus.dalaran.model.ClassificationModel;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.dto.DataTemplate;
import io.terminus.dalaran.model.dto.ModelDTO;
import io.terminus.dalaran.model.query.ModelQuery;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.model.schema.ObjectSchema;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequestMapping(value = "/api/model", produces = {"application/json; charset=UTF-8"})
public interface ModelRestAPI {

    @ApiOperation(value = "条件查询数据模型")
    @GetMapping(value = "/query")
    List<ModelDTO> query(ModelQuery query);

    @ApiOperation(value = "创建数据模型")
    @PostMapping(value = "/create")
    Long create(@RequestBody ModelDTO model);

    @ApiOperation(value = "更新数据模型")
    @PostMapping(value = "/update")
    ModelDTO update(@RequestBody ModelDTO model);

    @ApiOperation(value = "删除数据模型")
    @DeleteMapping(value = "/delete")
    void delete(@RequestParam Long id);

    @ApiOperation(value = "查询全部的数据模型")
    @GetMapping(value = "/list")
    List<ModelDTO> list();

    @ApiOperation(value = "全量查询某模块内的数据模型")
    @GetMapping(value = "/list/{moduleId}")
    List<ModelDTO> listByModuleId(@PathVariable Long moduleId);

    @ApiOperation(value = "查询全部可编辑的数据模型")
    @RequestMapping(value = "/list/public", method = RequestMethod.GET)
    List<ModelDTO> listEditable();

    @ApiOperation(value = "查询某模块内可编辑的数据模型")
    @RequestMapping(value = "/list/{moduleId}/public", method = RequestMethod.GET)
    List<ModelDTO> listEditableByModuleId(@PathVariable Long moduleId);

    @ApiOperation(value = "全量查询某模块内的分类数据模型")
    @RequestMapping(value = "/list/classification/{moduleId}/", method = RequestMethod.GET)
    Map<String, ClassificationModel> listClassificationByModuleId(@PathVariable Long moduleId);

    @ApiOperation(value = "根据模型匹配自动生成建议的映射")
    @RequestMapping(value = "/suggestMapping", method = RequestMethod.GET)
    Map<String, String> suggestMapping(@RequestParam Long sourceId, @RequestParam Long targetId);

    @ApiOperation(value = "导入 Excel 更新模型结构")
    @RequestMapping(value = "/{id}/import/excel", method = RequestMethod.POST)
    JsonSchema importExcel(@RequestParam MultipartFile file, @PathVariable long id);

    @ApiOperation(value = "导入数据模板更新模型结构")
    @RequestMapping(value = "/{id}/import/data-template", method = RequestMethod.POST)
    JsonSchema importDataTemplate(@RequestBody DataTemplate dataTemplate, @PathVariable long id);

    @ApiOperation(value = "导入数据模板更新模型结构")
    @RequestMapping(value = "/{id}/import/dalaran-schema", method = RequestMethod.POST)
    ObjectSchema importDalaranSchema(@RequestBody ObjectSchema objectSchema, @PathVariable long id);

    @ApiOperation(value = "根据模型结构生成数据样例")
    @RequestMapping(value = "/{id}/build/data-template", method = RequestMethod.POST)
    String buildRequestTemplate(@RequestBody JsonSchema schema, @PathVariable long id);

    // TODO 待开发
    @ApiOperation(value = "导入模型类信息更新模型结构")
    @RequestMapping(value = "/{id}/import/code-template", method = RequestMethod.POST)
    Map<String, ModelField> importCodeTemplate(@RequestBody String codeTemplate, @PathVariable long id);

    // TODO 其实意义不大
    @ApiOperation(value = "批量导入 Excel 创建模型结构")
    @RequestMapping(value = "/multi-import/excel", method = RequestMethod.POST)
    Map<Long, Map<String, JsonSchema>> multiImportExcel(@RequestParam MultipartFile file, @RequestParam BodyType type);

    @ApiOperation(value = "下载数据模型Excel模板样例")
    @RequestMapping(value = "/download/excel-template", method = RequestMethod.GET)
    ResponseEntity<Resource> downloadExcelTemplate();
}
