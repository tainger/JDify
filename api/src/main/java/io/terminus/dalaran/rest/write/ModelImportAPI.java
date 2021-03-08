package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.schema.DataTemplate;
import io.terminus.dalaran.model.schema.JsonSchema;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RequestMapping(value = "/api/model", produces = {"application/json; charset=UTF-8"})
public interface ModelImportAPI {

    @ApiOperation(value = "导入 Excel 更新模型结构")
    @PostMapping(value = "/{id}/import/excel")
    JsonSchema importExcel(@RequestParam MultipartFile file, @PathVariable String id);

    @ApiOperation(value = "导入 Excel 更新模型结构，不需要id")
    @PostMapping(value = "/import/excel")
    JsonSchema importExcelNoneId(@RequestParam MultipartFile file);

    @ApiOperation(value = "导入数据模板更新模型结构")
    @PostMapping(value = "/{id}/import/data-template")
    DalaranModelSchema importDataTemplate(@RequestBody DataTemplate dataTemplate, @PathVariable String id);

    @ApiOperation(value = "导入数据模版通过类型")
    @PostMapping(value = "/{type}/import/data-template-type")
    DalaranModelSchema importDataTemplateByType(@RequestBody DataTemplate dataTemplate, @PathVariable String type);

    @ApiOperation(value = "导入数据模板更新模型结构")
    @PostMapping(value = "/{id}/import/dalaran-schema")
    DalaranModelSchema importDalaranSchema(@RequestBody DalaranModelSchema schema, @PathVariable String id);

    // TODO 待开发
    @ApiOperation(value = "导入模型类信息更新模型结构")
    @PostMapping(value = "/{id}/import/code-template")
    Map<String, ModelField> importCodeTemplate(@RequestBody String codeTemplate, @PathVariable String id);

    // TODO 其实意义不大
    @ApiOperation(value = "批量导入 Excel 创建模型结构")
    @PostMapping(value = "/multi-import/excel")
    Map<String, Map<String, JsonSchema>> multiImportExcel(@RequestParam MultipartFile file, @RequestParam String modelType);

}
