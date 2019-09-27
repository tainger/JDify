package io.terminus.dalaran.service.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import io.swagger.models.*;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.util.Json;
import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.core.component.DalaranService;
import io.terminus.dalaran.core.component.annotation.ServiceConnector;
import io.terminus.dalaran.core.component.model.ServiceOperationModel;
import io.terminus.dalaran.model.BodyType;
import io.terminus.dalaran.model.FieldType;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.ModelField;
import io.terminus.dalaran.model.schema.JsonSchema;
import io.terminus.dalaran.service.soap.SoapService;
import org.apache.camel.builder.Builder;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.camel.Exchange.HTTP_METHOD;

@ServiceConnector(
        value = "swagger-connector",
        importConfigType = SwaggerImportConfig.class,
        serviceConfigType = SwaggerServiceConfig.class
)
public class SwaggerService implements DalaranService<SwaggerImportConfig, SwaggerServiceConfig, SwaggerOperationConfig> {

    private static final Logger logger = LoggerFactory.getLogger(SoapService.class);

    private static final String HTTP_URI = "%s4://%s%s?bridgeEndpoint=true";

    private static final String SUCCESSFUL_RESPONSE_CODE = "200";

    private static final String OPERATION_SPLIT = "::";

    @Override
    public void configure(ProcessorDefinition route, SwaggerOperationConfig operationConfig) {
        // TODO protocol
        String uri = String.format(HTTP_URI, "http", operationConfig.getUrl(), operationConfig.getPath());
        route.setHeader(HTTP_METHOD, Builder.constant(operationConfig.getMethod().name()));
        route.to(uri);
        // TODO Stream to string
        route.convertBodyTo(String.class);
    }

    @Override
    public SwaggerOperationConfig getOperationConfig(SwaggerServiceConfig swaggerServiceConfig, @NotNull String operationKey) {
        String[] operation = operationKey.split(OPERATION_SPLIT);
        Optional<SwaggerOperationConfig> operationConfigOptional = swaggerServiceConfig.getConfigs().stream()
                .filter(config -> StringUtils.equals(config.getPath(), operation[1]) && StringUtils.equals(config.getMethod().toString(), operation[0]))
                .findFirst();
        if (!operationConfigOptional.isPresent()) {
            // TODO throw
            return null;
        }
        return operationConfigOptional.get();
    }

    @Override
    public List<SwaggerOperationConfig> operations(SwaggerServiceConfig swaggerServiceConfig) {
        return swaggerServiceConfig.getConfigs();
    }

    @Override
    public SwaggerServiceConfig importConfig(SwaggerImportConfig importConfig) {
        HttpClient httpClient = HttpClientBuilder.create().build();

        HttpUriRequest httpUriRequest = new HttpGet(importConfig.getSwaggerUrl());
        try {
            HttpResponse swaggerResponse = httpClient.execute(httpUriRequest);
            ObjectMapper objectMapper = Json.mapper();

            Swagger swagger = objectMapper.readValue(swaggerResponse.getEntity().getContent(), Swagger.class);

            String baseUrl = swagger.getHost();
            if (StringUtils.isNotEmpty(swagger.getBasePath())) {
                baseUrl += swagger.getBasePath();
            }

            String finalBaseUrl = baseUrl;
            List<SwaggerOperationConfig> configs = swagger.getPaths().entrySet().stream().flatMap(path -> path.getValue().getOperationMap().entrySet().stream().map(method -> {
                SwaggerOperationConfig config = new SwaggerOperationConfig();
                config.setUrl(finalBaseUrl);
                config.setPath(path.getKey());
                config.setMethod(HttpMethod.valueOf(method.getKey().name()));
                config.setOperationKey(config.getMethod() + OPERATION_SPLIT + config.getPath());
                return config;
            })).collect(Collectors.toList());

            SwaggerServiceConfig swaggerOperations = new SwaggerServiceConfig();
            swaggerOperations.setUrl(swagger.getHost());
            swaggerOperations.setBasePath(swagger.getBasePath());
            swaggerOperations.setConfigs(configs);
            return swaggerOperations;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ServiceOperationModel buildOperationModel(SwaggerImportConfig swaggerImportConfig, SwaggerOperationConfig swaggerOperationConfig) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpUriRequest httpUriRequest = new HttpGet(swaggerImportConfig.getSwaggerUrl());
        try {
            HttpResponse swaggerResponse = httpClient.execute(httpUriRequest);
            ObjectMapper objectMapper = Json.mapper();

            Swagger swagger = objectMapper.readValue(swaggerResponse.getEntity().getContent(), Swagger.class);
            for (Map.Entry<String, Path> entry : swagger.getPaths().entrySet()) {
                String pathName = entry.getKey();
                Path path = entry.getValue();
                for (Map.Entry<io.swagger.models.HttpMethod, Operation> method : path.getOperationMap().entrySet()) {
                    if (StringUtils.equals(swaggerOperationConfig.getPath(), pathName)
                            && swaggerOperationConfig.getMethod() == HttpMethod.valueOf(method.getKey().name())) {
                        ServiceModel inModel = buildInModel(method.getValue(), swagger.getDefinitions());
                        ServiceModel outModel = buildOutModel(method.getValue(), swagger.getDefinitions());
                        return new ServiceOperationModel(inModel.getModel(), inModel.getName(), outModel.getModel(), outModel.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private ServiceModel buildOutModel(Operation operation, Map<String, Model> definitions) {
        ServiceModel serviceModel = new ServiceModel();
        JsonSchema outSchema = new JsonSchema();
        MessageModel outModel = new MessageModel<>();
        outModel.setModelType(BodyType.JSON);
        outModel.setModelSchema(outSchema);
        Response response = operation.getResponses().get(SUCCESSFUL_RESPONSE_CODE);
        Property property = response.getSchema();
        if (property == null) {
            return serviceModel;
        }
        serviceModel.setName(property.getName());
        ModelField outRootField = buildField(property, definitions);
        Map<String, ModelField> outFields = Maps.newHashMap();
        outFields.put("root", outRootField);
        outSchema.setFields(outFields);
        serviceModel.setModel(outModel);
        return serviceModel;
    }

    private ServiceModel buildInModel(Operation operation, Map<String, Model> definitions) {
        ServiceModel serviceModel = new ServiceModel();
        JsonSchema inSchema = new JsonSchema();
        MessageModel inModel = new MessageModel<>();
        inModel.setModelType(BodyType.JSON);
        inModel.setModelSchema(inSchema);
        Map<String, ModelField> inRootFields = Maps.newHashMap();
        inSchema.setFields(inRootFields);

        for (Parameter parameter : operation.getParameters()) {
            switch (parameter.getIn()) {
                case "body":
                    Model model = ((BodyParameter) parameter).getSchema();
                    if (model instanceof RefModel) {
                        ModelField rootField = new ModelField();
                        inRootFields.put("root", rootField);
                        rootField.setType(FieldType.OBJECT);
                        Map<String, ModelField> subFields = Maps.newHashMap();
                        rootField.setFields(subFields);
                        String modelName = ((RefModel) model).getSimpleRef();
                        serviceModel.setName(modelName);
                        Model realModel = definitions.get(modelName);
                        if (realModel instanceof ModelImpl) {
                            ((ModelImpl) realModel).getType();
                            realModel.getProperties().forEach((key, field) -> {
                                subFields.put(key, buildField(field, definitions));
                            });
                        }
                    }
                    break;
                case "path":
//                                throw new RuntimeException("暂时不支持 path 格式");
                    logger.warn("暂时不支持 path 格式");
                    break;
                case "query":
//                                throw new RuntimeException("暂时不支持 query 格式");
                    logger.warn("暂时不支持 query 格式");
                    break;
                case "formData":
//                                throw new RuntimeException("暂时不支持 formData 格式");
                    logger.warn("暂时不支持 formData 格式");
                    break;
                case "header":
//                                throw new RuntimeException("暂时不支持 header 格式");
                    logger.warn("暂时不支持 header 格式");
                    break;
                case "cookie":
//                                throw new RuntimeException("暂时不支持 cookie 格式");
                    logger.warn("暂时不支持 cookie 格式");
                    break;
            }
        }
        serviceModel.setModel(inModel);
        return serviceModel;
    }

    private ModelField buildField(Property property, Map<String, Model> definitions) {
        ModelField field = new ModelField();
        Map<String, ModelField> subFields = Maps.newHashMap();
        field.setFields(subFields);
        field.setDescription(property.getDescription());


        switch (property.getType()) {
            case "object":
                field.setType(FieldType.OBJECT);
                if (property instanceof ObjectProperty) {
                    if (((ObjectProperty) property).getProperties() != null) {
                        ((ObjectProperty) property).getProperties().forEach((subPropertyName, subProperty) -> {
                            field.getFields().put(subPropertyName, buildField(subProperty, definitions));
                        });
                    }
                }
                break;
            case "array":
                field.setType(FieldType.ARRAY);
                if (property instanceof ArrayProperty) {
                    ModelField arrayField = buildField(((ArrayProperty) property).getItems(), definitions);
                    field.setSubType(arrayField.getType());
                    field.setFields(arrayField.getFields());
                }
                break;
            case "ref":
                if (property instanceof RefProperty) {
                    Model subModel = definitions.get(((RefProperty) property).getSimpleRef());
                    if (subModel instanceof ModelImpl) {
                        field.setType(getFieldType(((ModelImpl) subModel).getType()));
                        subModel.getProperties().forEach((key, subField) -> subFields.put(key, buildField(subField, definitions)));
                    }
                }
                break;
            case "integer":
                field.setType(FieldType.INTEGER);
                break;
            case "number":
                field.setType(FieldType.FLOAT);
                break;
            case "string":
                field.setType(FieldType.STRING);
                break;
            case "boolean":
                field.setType(FieldType.BOOLEAN);
                break;
        }
        return field;
    }

    private FieldType getFieldType(String type) {
        switch (type) {
            case "object":
                return FieldType.OBJECT;
            case "array":
                return FieldType.ARRAY;
            case "integer":
                return FieldType.INTEGER;
            case "number":
                return FieldType.FLOAT;
            case "string":
                return FieldType.STRING;
            case "boolean":
                return FieldType.BOOLEAN;
        }
        return null;
    }
}
