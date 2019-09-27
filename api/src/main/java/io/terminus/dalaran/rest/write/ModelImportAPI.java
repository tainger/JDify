package io.terminus.dalaran.rest.write;

import io.swagger.annotations.ApiOperation;

import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.dto.DataTemplate;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.model.schema.ObjectSchema;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RequestMapping(value = "/api/model", produces = {"application/json; charset=UTF-8"})
public interface ModelImportAPI {

    @ApiOperation(value = "导入 Excel 更新模型结构")
    @PostMapping(value = "/{id}/import/excel")
    JsonSchema importExcel(@RequestParam MultipartFile file, @PathVariable long id);

    @ApiOperation(value = "导入数据模板更新模型结构")
    @PostMapping(value = "/{id}/import/data-template")
    JsonSchema importDataTemplate(@RequestBody DataTemplate dataTemplate, @PathVariable long id);

    @ApiOperation(value = "导入数据模板更新模型结构")
    @PostMapping(value = "/{id}/import/dalaran-schema")
    ObjectSchema importDalaranSchema(@RequestBody ObjectSchema objectSchema, @PathVariable long id);

    // TODO 待开发
    @ApiOperation(value = "导入模型类信息更新模型结构")
    @PostMapping(value = "/{id}/import/code-template")
    Map<String, ModelField> importCodeTemplate(@RequestBody String codeTemplate, @PathVariable long id);

    // TODO 其实意义不大
    @ApiOperation(value = "批量导入 Excel 创建模型结构")
    @PostMapping(value = "/multi-import/excel")
    Map<Long, Map<String, JsonSchema>> multiImportExcel(@RequestParam MultipartFile file, @RequestParam String modelType);

}
