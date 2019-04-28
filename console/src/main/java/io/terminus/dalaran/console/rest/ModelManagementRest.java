package io.terminus.dalaran.console.rest;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.BodyType;
import io.terminus.dalaran.console.model.dto.ModelDTO;
import io.terminus.dalaran.console.model.query.ModelQuery;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.console.util.ExcelUtils;
import io.terminus.dalaran.entity.ModelEntity;
import io.terminus.dalaran.model.schema.model.ModelField;
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

    @ApiOperation(value = "excel文件解析--更新")
    @RequestMapping(value = "/import/excel/update", method = RequestMethod.POST)
    public Map<Long, Map<String, Map<String, ModelField>>> importExcel(@RequestParam MultipartFile file, @RequestParam long id) {
        ExcelUtils excelUtils = new ExcelUtils();
        try {
            Map<String, Map<String, ModelField>> schema = excelUtils.parse(file.getInputStream());
            Map<Long, Map<String, Map<String, ModelField>>> modelSchema = new HashMap<>();
            for (Map.Entry<String, Map<String, ModelField>> entry : schema.entrySet()) {
                ModelEntity model = modelRepository.findOne(id);
                model.setModelSchema(JSON.toJSONString(entry.getValue()));
                modelRepository.save(model);
                modelSchema.put(id, schema);
            }
            return modelSchema;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    @ApiOperation(value = "excel文件解析--新建")
    @RequestMapping(value = "/import/excel/create", method = RequestMethod.POST)
    public Map<Long, Map<String, Map<String, ModelField>>> importExcel(@RequestParam MultipartFile file,
                                                                       @RequestParam String name, @RequestParam String type) {
        ExcelUtils excelUtils = new ExcelUtils();
        try {
            Map<String, Map<String, ModelField>> schema = excelUtils.parse(file.getInputStream());
            Map<Long, Map<String, Map<String, ModelField>>> modelSchema = new HashMap<>();
            for (Map.Entry<String, Map<String, ModelField>> entry : schema.entrySet()) {
                ModelEntity model = new ModelEntity();
                model.setModelSchema(JSON.toJSONString(entry.getValue()));
                model.setName(name);
                model.setType(BodyType.valueOf(type.toUpperCase()));
                modelRepository.save(model);
                Map<String, Map<String, ModelField>> singleSchema = new HashMap<>();
                singleSchema.put(entry.getKey(), entry.getValue());
                modelSchema.put(model.getId(), singleSchema);
            }
            return modelSchema;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}
