package io.terminus.dalaran.model.schema;

import io.terminus.dalaran.model.CSVModelType;
import io.terminus.dalaran.model.DalaranModelSchema;
import lombok.Data;

@Data
public class CsvModelSchema extends DalaranModelSchema {

    private String columnSequence;

    private String columnDelimiter;

    private String dataDelimiter;

    private String datePatten;

    private CSVModelType type = CSVModelType.COMMON;
}
