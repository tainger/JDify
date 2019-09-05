package io.terminus.dalaran.console.rest;

import io.swagger.annotations.ApiOperation;
import io.terminus.common.model.Response;
import io.terminus.dalaran.console.model.ResponseMessage;
import io.terminus.dalaran.console.model.dto.DataTemplate;
import io.terminus.dalaran.console.model.dto.ModelDTO;
import io.terminus.dalaran.console.model.query.ModelQuery;
import io.terminus.dalaran.console.repository.ModelRepository;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.model.BodyType;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.model.schema.ObjectSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
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
    public Response query(ModelQuery query) {
        try {
            return Response.ok(modelManagementService.queryModels(query));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.MODEL_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "创建数据模型")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Response create(@RequestBody ModelDTO model) {
        try {
            return Response.ok(modelManagementService.createModel(model));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.MODEL_CREATE_ERROR);
        }
    }

    @ApiOperation(value = "更新数据模型")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Response update(@RequestBody ModelDTO model) {
        try {
            return Response.ok(modelManagementService.updateModel(model));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.MODEL_UPDATE_ERROR);
        }
    }

    @ApiOperation(value = "删除数据模型")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Response delete(@RequestParam Long id) {
        try {
            modelManagementService.deleteModel(id);
            return Response.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.MODEL_DELETE_ERROR);
        }
    }

    @ApiOperation(value = "查询全部的数据模型")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Response list() {
        try {
            return Response.ok(modelManagementService.list());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.MODEL_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "全量查询某模块内的数据模型")
    @RequestMapping(value = "/list/{moduleId}", method = RequestMethod.GET)
    public Response listByModuleId(@PathVariable Long moduleId) {
        try {
            return Response.ok(modelManagementService.listByModuleId(moduleId));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.MODEL_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "查询全部未隐藏的数据模型")
    @RequestMapping(value = "/list/public", method = RequestMethod.GET)
    public Response listNoHidden() {
        try {
            return Response.ok(modelManagementService.listNoHidden());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.MODEL_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "查询某模块内未隐藏的数据模型")
    @RequestMapping(value = "/list/{moduleId}/public", method = RequestMethod.GET)
    public Response listNoHiddenByModuleId(@PathVariable Long moduleId) {
        try {
            return Response.ok(modelManagementService.listNotHiddenByModuleId(moduleId));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.MODEL_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "全量查询某模块内的分类数据模型")
    @RequestMapping(value = "/list/classification/{moduleId}/", method = RequestMethod.GET)
    public Response listClassificationByModuleId(@PathVariable Long moduleId) {
        try {
            return Response.ok(modelManagementService.listClassificationModels(moduleId));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.MODEL_QUERY_ERROR);
        }
    }

    @ApiOperation(value = "根据模型匹配自动生成建议的映射")
    @RequestMapping(value = "/suggestMapping", method = RequestMethod.GET)
    public Response suggestMapping(@RequestParam Long sourceId, @RequestParam Long targetId) {
        try {
            return Response.ok(modelManagementService.suggestMapping(sourceId, targetId));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.BUILD_MAPPING_SUGGEST_ERROR);
        }
    }

    @ApiOperation(value = "导入 Excel 更新模型结构")
    @RequestMapping(value = "/{id}/import/excel", method = RequestMethod.POST)
    public Response importExcel(@RequestParam MultipartFile file, @PathVariable long id) {
        try {
            return Response.ok(modelManagementService.importExcel(file, id));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.EXCEL_PARSE_ERROR);
        }
    }

    @ApiOperation(value = "导入数据模板更新模型结构")
    @RequestMapping(value = "/{id}/import/data-template", method = RequestMethod.POST)
    public Response importDataTemplate(@RequestBody DataTemplate dataTemplate, @PathVariable long id) {
        try {
            return Response.ok(modelManagementService.importDataTemplate(dataTemplate, id));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.DATA_TEMPLATE_PARSE_ERROR);
        }
    }

    @ApiOperation(value = "导入数据模板更新模型结构")
    @RequestMapping(value = "/{id}/import/dalaran-schema", method = RequestMethod.POST)
    public Response importDalaranSchema(@RequestBody ObjectSchema objectSchema, @PathVariable long id) {
        try {
            return Response.ok(modelManagementService.importDalaranSchema(objectSchema, id));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.DATA_TEMPLATE_PARSE_ERROR);
        }
    }

    @ApiOperation(value = "根据模型结构生成数据样例")
    @RequestMapping(value = "/{id}/build/data-template", method = RequestMethod.POST)
    public Response buildRequestTemplate(@RequestBody JsonSchema schema, @PathVariable long id) {
        try {
            return Response.ok(modelManagementService.buildDataTemplate(schema, id));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.MODEL_EXAMPLE_BUILD_ERROR);
        }
    }

    // TODO 待开发
    @ApiOperation(value = "导入模型类信息更新模型结构")
    @RequestMapping(value = "/{id}/import/code-template", method = RequestMethod.POST)
    public Map<String, ModelField> importCodeTemplate(@RequestBody String codeTemplate, @PathVariable long id) {
        return new HashMap<>();
    }

    // TODO 其实意义不大
    @ApiOperation(value = "批量导入 Excel 创建模型结构")
    @RequestMapping(value = "/multi-import/excel", method = RequestMethod.POST)
    public Response multiImportExcel(@RequestParam MultipartFile file, @RequestParam BodyType type) {
        try {
            return Response.ok(modelManagementService.multiImportExcel(file, type));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail(ResponseMessage.EXCEL_PARSE_ERROR);
        }
    }

    @ApiOperation(value = "下载数据模型Excel模板样例")
    @RequestMapping(value = "/download/excel-template", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadExcelTemplate() {
        return modelManagementService.downloadExcelTemplate();
    }
}
