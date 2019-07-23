package io.terminus.dalaran.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import static io.terminus.dalaran.DalaranConstants.MODEL_ROOT;

// TODO Schema 最好有版本, 做升级时比较好处理
@Data
public abstract class DalaranModelSchema {
    private Map<String, ModelField> fields = new HashMap<>();

    public void setRootField(ModelField rootField) {
        fields.put(MODEL_ROOT, rootField);
    }
}
