package io.terminus.dalaran.model.schema;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.model.CSVModelType;
import io.terminus.dalaran.model.DalaranModelSchema;
import io.terminus.dalaran.model.annotation.Model;
import io.terminus.dalaran.model.annotation.ModelFieldInfo;
import lombok.Data;

@Data
@Model(value = "CSV")
public class CsvModelSchema extends DalaranModelSchema {
    @ModelFieldInfo(label = "字段顺序", inputType = FieldInputType.String, required = false)
    private String columnSequence;

    @ModelFieldInfo(label = "数据解析分隔符", inputType = FieldInputType.String, required = false)
    private String columnDelimiter;

    @ModelFieldInfo(label = "数据拼接分隔符", inputType = FieldInputType.String, required = false)
    private String dataDelimiter;

    @ModelFieldInfo(label = "模型类型", inputType = FieldInputType.Select, defaultValue = "COMMON")
    private CSVModelType type = CSVModelType.COMMON;

    @ModelFieldInfo(label = "保留行结束符", inputType = FieldInputType.Switch, required = false, defaultValue = "false")
    private boolean remainEOF = false;
}
