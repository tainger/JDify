package io.terminus.dalaran.console.rest;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.console.model.StructureModel;
import io.terminus.dalaran.console.model.query.StructureQuery;
import io.terminus.dalaran.console.service.StructureManagementService;
import io.terminus.dalaran.console.util.ExcelUtils;
import io.terminus.dalaran.entity.StructureEntity;
import io.terminus.dalaran.model.schema.structure.ModelField;
import io.terminus.dalaran.repository.StructureRepository;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/4/1
 */
@RestController
@RequestMapping("dalaran_management/structure")
public class StructureManagementRest {

    @Autowired
    private StructureManagementService structureManagementService;

    @Autowired
    private StructureRepository structureRepository;

    @ApiOperation(value = "条件查询数据模型")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public List<StructureModel> query(StructureQuery query) {
        return structureManagementService.queryStructures(query);
    }

    @ApiOperation(value = "创建数据模型")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void create(@RequestBody StructureModel model) {
        structureManagementService.createStructure(model);
    }

    @ApiOperation(value = "更新数据模型")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public void update(@RequestBody StructureModel model) {
        structureManagementService.updateStructure(model);
    }

    @ApiOperation(value = "删除数据模型")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public void delete(@RequestParam Long id) {
        structureManagementService.deleteStructure(id);
    }

    @ApiOperation(value = "全量查询数据模型")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<StructureModel> list() {
        return structureManagementService.list();
    }

    @ApiOperation(value = "excel文件解析--更新")
    @RequestMapping(value = "/import/excel/update", method = RequestMethod.POST)
    public Map<Long, Map<String, Map<String, ModelField>>> importExcel(@RequestParam MultipartFile file, @RequestParam long id) {
        ExcelUtils excelUtils = new ExcelUtils();
        try {
            Map<String, Map<String, ModelField>> schema = excelUtils.parse(file.getInputStream());
            Map<Long, Map<String, Map<String, ModelField>>> structureSchema = new HashMap<>();
            for (Map.Entry<String, Map<String, ModelField>> entry : schema.entrySet()) {
                StructureEntity structure = structureRepository.findOne(id);
                structure.setStructureSchema(JSON.toJSONString(entry.getValue()));
                structureRepository.save(structure);
                structureSchema.put(id, schema);
            }
            return structureSchema;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    @ApiOperation(value = "excel文件解析--新建")
    @RequestMapping(value = "/import/excel/create", method = RequestMethod.POST)
    public Map<Long, Map<String, Map<String, ModelField>>> importExcel(@RequestParam MultipartFile file) {
        ExcelUtils excelUtils = new ExcelUtils();
        try {
            Map<String, Map<String, ModelField>> schema = excelUtils.parse(file.getInputStream());
            Map<Long, Map<String, Map<String, ModelField>>> structureSchema = new HashMap<>();
            for (Map.Entry<String, Map<String, ModelField>> entry : schema.entrySet()) {
                StructureEntity structure = new StructureEntity();
                structure.setStructureSchema(JSON.toJSONString(entry.getValue()));
                structure.setName("Dalaran Model " + entry.getKey());
                structureRepository.save(structure);
                structureSchema.put(structure.getId(), schema);
            }
            return structureSchema;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}
