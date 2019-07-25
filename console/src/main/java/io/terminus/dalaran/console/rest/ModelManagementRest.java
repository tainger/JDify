package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.component.processor.mapper.model.SimpleMapping;
import io.terminus.dalaran.console.model.dto.DataTemplate;
import io.terminus.dalaran.console.model.dto.ModelDTO;
import io.terminus.dalaran.console.model.query.ModelQuery;
import io.terminus.dalaran.console.repository.ModelRepository;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.model.BodyType;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.schema.JsonSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/4/1
 */
@RestController
@RequestMapping("/api/model")
public class ModelManagementRest {

    @Autowired
    private ModelManagementService modelManagementService;

    @Autowired
    private ModelRepository modelRepository;

    @ApiOperation(value = "条件查询数据模型")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public List<ModelDTO> query(ModelQuery query) {
        return modelManagementService.queryModels(query);
    }

    @ApiOperation(value = "创建数据模型")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Long create(@RequestBody ModelDTO model) {
        return modelManagementService.createModel(model);
    }

    @ApiOperation(value = "更新数据模型")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ModelDTO update(@RequestBody ModelDTO model) {
        return modelManagementService.updateModel(model);
    }

    @ApiOperation(value = "删除数据模型")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public void delete(@RequestParam Long id) {
        modelManagementService.deleteModel(id);
    }

    @ApiOperation(value = "全量查询数据模型")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<ModelDTO> list() {
        return modelManagementService.list();
    }

    @ApiOperation(value = "根据模型匹配自动生成建议的映射")
    @RequestMapping(value = "/suggestMapping", method = RequestMethod.GET)
    public Map<String, SimpleMapping> suggestMapping(@RequestParam Long sourceId, @RequestParam Long targetId) {
        return modelManagementService.suggestMapping(sourceId, targetId);
    }

    @ApiOperation(value = "导入 Excel 更新模型结构")
    @RequestMapping(value = "/{id}/import/excel", method = RequestMethod.POST)
    public JsonSchema importExcel(@RequestParam MultipartFile file, @PathVariable long id) throws Exception {
        return modelManagementService.importExcel(file, id);
    }

    @ApiOperation(value = "导入数据模板更新模型结构")
    @RequestMapping(value = "/{id}/import/data-template", method = RequestMethod.POST)
    public Map<String, ModelField> importDataTemplate(@RequestBody DataTemplate dataTemplate, @PathVariable long id) {
        JsonSchema schema = modelManagementService.importDataTemplate(dataTemplate, id);
        return schema.getFields();
    }

    @ApiOperation(value = "根据模型结构生成数据样例")
    @RequestMapping(value = "/{id}/build/data-template", method = RequestMethod.POST)
    public String buildRequestTemplate(@RequestBody JsonSchema schema, @PathVariable long id) {
        return modelManagementService.buildDataTemplate(schema, id);
    }

    // TODO 待开发
    @ApiOperation(value = "导入数据模板更新模型结构")
    @RequestMapping(value = "/{id}/import/code-template", method = RequestMethod.POST)
    public Map<String, ModelField> importCodeTemplate(@RequestBody String codeTemplate, @PathVariable long id) {
        return new HashMap<>();
    }

    // TODO 其实意义不大
    @ApiOperation(value = "批量导入 Excel 创建模型结构")
    @RequestMapping(value = "/multi-import/excel", method = RequestMethod.POST)
    public Map<Long, Map<String, JsonSchema>> multiImportExcel(@RequestParam MultipartFile file, @RequestParam BodyType type) throws Exception {
        return modelManagementService.multiImportExcel(file, type);
    }


}
