package io.terminus.dalaran.utils;

import io.terminus.dalaran.model.DalaranModelSchema;

import java.util.Map;

public class ModelConvertUtils {

    public static DalaranModelSchema convertSchema(String modelType, Map<String, Object> schema) {
        switch (modelType){
            case "JSON":
            case "OBJECT":
            case "SOAP":
            case "CSV":
        }
    }
}
