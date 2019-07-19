package io.terminus.dalaran.core.model;

import lombok.Data;

import java.util.Map;

// TODO Schema 最好有版本, 做升级时比较好处理
@Data
public abstract class DalaranModelSchema {
    private Map<String, ModelField> fields;
}
