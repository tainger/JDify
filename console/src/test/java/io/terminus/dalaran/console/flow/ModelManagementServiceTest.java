package io.terminus.dalaran.console.flow;

import com.alibaba.fastjson.JSON;
import io.terminus.dalaran.console.entity.ModelEntity;
import io.terminus.dalaran.console.repository.ModelRepository;
import io.terminus.dalaran.console.service.ModelManagementService;
import io.terminus.dalaran.model.FieldType;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.dto.DataTemplate;
import io.terminus.dalaran.model.dto.ModelDTO;
import io.terminus.dalaran.model.query.ModelQuery;
import io.terminus.dalaran.model.schema.JsonSchema;
import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jingdi on 2019/6/30
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback
public class ModelManagementServiceTest {

    @Autowired
    private ModelManagementService modelManagementService;

    @Autowired
    private ModelRepository modelRepository;

    @Test
    public void create() {
        ModelDTO model = new ModelDTO();
        model.setName("test");
        model.setModelType("JSON");
        model.setModuleId(1L);
        Long id = modelManagementService.createModel(model);
        ModelEntity entity = modelRepository.findById(id).get();
        Assert.assertEquals(entity.getName(), model.getName());
    }

    @Test
    public void update() {
        ModelDTO model = new ModelDTO();
        model.setId(23L);
        model.setModuleId(1L);
        model.setModelType("JSON");
        model.setName("test");
        model.setModelSchema(buildModelSchema());
        ModelDTO newModel = modelManagementService.updateModel(model);
        Assert.assertEquals(newModel.getName(), model.getName());
    }

    @Test
    public void importFromExcel() {
        Resource resource = new ClassPathResource("excel-parse-model-s-3.xlsx");
        try {
            File file = resource.getFile();
            FileInputStream inputStream = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream);
            JsonSchema schema = modelManagementService.importExcel(multipartFile, 23L);
            Assert.assertNotNull(schema);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void importFromDataTemplate() {
        DataTemplate dataTemplate = new DataTemplate();
        dataTemplate.setDataTemplate("[{\"user\":{\"id\":2, \"name\":\"momo\", \"phone\":\"10086\", \"address\":\"mmmmmm\", \"wechat\":\"9999\"}, \"order\":{\"id\":\"11001\", \"time\":\"00:00\", \"detail\":\"asdfghjkl\", \"user\":\"momo\", \"address\":[{\"addr1\":\"mmmm\", \"addr2\":\"llllll\", \"list\":[{\"itemA\":\"11111\", \"itemB\":\"2222222\"}]}, {\"addr1\":\"pppppp\"}]}}]");
        JsonSchema schema = modelManagementService.importDataTemplate(dataTemplate, 23L);
        Assert.assertNotNull(schema);
    }

    @Test
    public void list() {
        List<ModelDTO> models = modelManagementService.list();
        Assert.assertNotNull(models);
    }

    @Test
    public void query() {
        ModelQuery query = new ModelQuery();
        query.setModuleId(1L);
        query.setName("test-model");
        List<Long> modelIds = new ArrayList<>();
        modelIds.add(19L);
        query.setModelIds(modelIds);
        List<ModelDTO> models = modelManagementService.queryModels(query);
        models.forEach(model -> {
            Assert.assertSame(model.getModuleId(), 1L);
        });
    }

    @Test
    public void buildRequest() {
        Resource resource = new ClassPathResource("excel-parse-model-s-3.xlsx");
        try {
            File file = resource.getFile();
            FileInputStream inputStream = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream);
            JsonSchema schema = modelManagementService.importExcel(multipartFile, 23L);
            String request = modelManagementService.buildDataTemplate(schema, 23L);
            Assert.assertNotNull(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> buildModelSchema() {
        Map<String, ModelField> inField = new HashMap<>();
        ModelField inModelField = new ModelField();
        inModelField.setType(FieldType.OBJECT);
        Map<String, ModelField> inChild = new HashMap<>();
        ModelField childField1 = new ModelField();
        childField1.setType(FieldType.STRING);
        inChild.put("id", childField1);
        ModelField childField2 = new ModelField();
        childField2.setType(FieldType.STRING);
        inChild.put("name", childField2);
        inModelField.setFields(inChild);
        inField.put("root", inModelField);
        return JSON.parseObject(JSON.toJSONString(inField), Map.class);
    }
}
