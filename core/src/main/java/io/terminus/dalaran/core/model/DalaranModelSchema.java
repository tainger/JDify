package io.terminus.dalaran.core.model;

import java.util.Map;

// TODO Schema 最好有版本, 做升级时比较好处理
public interface DalaranModelSchema {

    Map<String, ModelField> getFields();
}
