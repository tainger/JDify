package io.terminus.dalaran.component.trigger.rest.utils;

import io.swagger.models.*;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.*;
import io.terminus.dalaran.component.trigger.rest.model.ApiInfo;
import io.terminus.dalaran.component.trigger.rest.model.ApiParameter;
import io.terminus.dalaran.component.trigger.rest.model.DalaranSwaggerModel;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class SwaggerUtils {

    public static Swagger buildSwagger(List<ApiInfo> apiInfoList) {
        Swagger swagger = new Swagger();
        swagger.setPaths(new LinkedHashMap<>());
        List<Tag> tagList = new ArrayList<>();
        for (ApiInfo apiInfo : apiInfoList) {
            if(StringUtils.isNotBlank(apiInfo.getModuleName())) {
                Path path = swagger.getPaths().computeIfAbsent(apiInfo.getPath(), p -> new Path());
                Operation operation = new Operation();
                path.set(apiInfo.getMethod().toString().toLowerCase(), operation);

                Tag tag = new Tag();
                tag.setName(apiInfo.getModuleName());
                tag.setDescription(apiInfo.getDescription());
                tagList.add(tag);

                operation.setSummary(apiInfo.getName());
                operation.setDescription(apiInfo.getDescription());
                operation.addTag(apiInfo.getModuleName());
                operation.addConsumes("application/json");
                Response response = new Response();
                operation.addResponse("200", response);
                response.description("OK");
                response.setSchema(toSwaggerProperty(apiInfo.getOutput()));
                response.getSchema().setExample(apiInfo.getOutExample());
                if (apiInfo.getMethod().isNoBody()) {
                    operation.setParameters(toQueryParameter(apiInfo.getInput()));
                } else {
                    operation.addParameter(toBodyParameter(apiInfo.getInput(), apiInfo.getInExample()));
                }
            }
        }
        swagger.setTags(tagList);
        return swagger;
    }

    private static List<Parameter> toQueryParameter(ApiParameter param) {
        List<Parameter> parameters = new ArrayList<>();
        param.getSubParameter().forEach((name, subField) -> {
            QueryParameter parameter = new QueryParameter();
            parameters.add(parameter);
            Property subFieldProperty = buildProperty(subField);
            if (subFieldProperty != null) {
                parameter.name(name).type(subFieldProperty.getType());
            }
        });
        return parameters;
    }

    private static Parameter toBodyParameter(ApiParameter param, Object example) {
        BodyParameter parameter = new BodyParameter();
        Model bodyModel = new DalaranSwaggerModel();
        Map<String, Property> properties = new HashMap<>();
        param.getSubParameter().forEach((name, subField) -> properties.put(name, buildProperty(subField)));
        bodyModel.setProperties(properties);
        bodyModel.setExample(example);
        parameter.setSchema(bodyModel);
        return parameter;
    }

    private static Property toSwaggerProperty(ApiParameter param) {
        return buildProperty(param);
    }

    private static Property buildProperty(ApiParameter param) {
        switch (param.getType()) {
            case INTEGER:
                return new LongProperty();
            case FLOAT:
                return new DoubleProperty();
            case DATE:
                return new DateProperty();
            case STRING:
                return new StringProperty();
            case BOOLEAN:
                return new BooleanProperty();
            case ARRAY: {
                // TODO 这个 SubType 很难受...
                ArrayProperty arrayProperty = new ArrayProperty();
                ApiParameter subParam = param.getSubParameter().get("");
                if (subParam != null) {
                    arrayProperty.setItems(buildProperty(subParam));
                } else {
                    ObjectProperty property = new ObjectProperty();
                    param.getSubParameter().forEach((name, subField) -> {
                        property.property(name, buildProperty(subField));
                    });
                    arrayProperty.setItems(property);
                }
                return arrayProperty;
            }
            case OBJECT: {
                ObjectProperty property = new ObjectProperty();
                param.getSubParameter().forEach((name, subField) -> {
                    property.property(name, buildProperty(subField));
                });
                return property;
            }
        }
        return null;
    }
}
