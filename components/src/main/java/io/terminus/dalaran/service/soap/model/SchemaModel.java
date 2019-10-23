package io.terminus.dalaran.service.soap.model;

import com.predic8.schema.ComplexType;
import com.predic8.schema.SimpleType;
import lombok.Data;

import java.util.Map;

@Data
public class SchemaModel {

    private Map<String, ComplexType> complexTypes;

    private Map<String, SimpleType> simpleTypes;

    public SchemaModel(Map<String, ComplexType> complexTypes, Map<String, SimpleType> simpleTypes) {
        this.complexTypes = complexTypes;
        this.simpleTypes = simpleTypes;
    }
}
