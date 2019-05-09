package io.terminus.dalaran.console.rest;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.BodyType;
import io.terminus.dalaran.console.model.dto.ModelDTO;
import io.terminus.dalaran.console.model.query.ModelQuery;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.console.util.ExcelUtils;
import io.terminus.dalaran.entity.manage.ModelEntity;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.repository.ModelRepository;
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

    @ApiOperation(value = "导入 Excel 更新模型结构")
    @RequestMapping(value = "/{id}/import/excel", method = RequestMethod.POST)
    public JsonSchema importExcel(@RequestParam MultipartFile file, @PathVariable long id) throws Exception {
        Map<String, ModelField> fields = ExcelUtils.parseFirstSheet(file.getInputStream());
        JsonSchema schema = new JsonSchema();
        schema.setFields(fields);

        // TODO 这些应该扔到 service 里
        ModelEntity model = modelRepository.findOne(id);
        model.setModelSchema(JSON.toJSONString(schema));
        modelRepository.save(model);
        return schema;
    }

    // TODO 待开发
    @ApiOperation(value = "导入数据模板更新模型结构")
    @RequestMapping(value = "/{id}/import/data-template", method = RequestMethod.POST)
    public Map<String, ModelField> importDataTemplate(@RequestBody String dataTemplate, @PathVariable long id) {
        return new HashMap<>();
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
        Map<Long, Map<String, JsonSchema>> modelSchema = new HashMap<>();
        Map<String, Map<String, ModelField>> schemas = ExcelUtils.parseAllSheet(file.getInputStream());
        // TODO 这些应该扔到 service 里
        for (Map.Entry<String, Map<String, ModelField>> entry : schemas.entrySet()) {
            ModelEntity model = new ModelEntity();
            JsonSchema schema = new JsonSchema();
            schema.setFields(entry.getValue());
            model.setModelSchema(JSON.toJSONString(schema));
            model.setName(entry.getKey());
            model.setType(type);
            modelRepository.save(model);
            Map<String, JsonSchema> singleSchema = new HashMap<>();
            singleSchema.put(entry.getKey(), schema);
            modelSchema.put(model.getId(), singleSchema);
        }
        return modelSchema;
    }
}
