package io.terminus.dalaran.component.processor.sql;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.connector.SqlDataSourceConnector;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import io.terminus.dalaran.core.component.config.OutModelConfig;
import lombok.Data;

@Data
public class SqlConfig extends OutModelConfig implements ConnectorConfig<SqlDataSourceConnector> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private SqlDataSourceConnector connector;

    @ConfigFieldInfo(label = "DB 数据源", inputType = FieldInputType.Connector, connectorType = SqlDataSourceConnector.class)
    private Long connectorId;

    @ConfigFieldInfo(label = "SQL 语句", inputType = FieldInputType.SQL)
    private String sql;

    @ConfigFieldInfo(label = "SQL类型", inputType = FieldInputType.Select, defaultValue = "COMMON")
    private SQLType sqlType = SQLType.COMMON;

    @ConfigFieldInfo(label = "操作类型", inputType = FieldInputType.Select, defaultValue = "SELECT")
    private SQLOperationType operationType = SQLOperationType.SELECT;

    @ConfigFieldInfo(label = "SQL压缩", inputType = FieldInputType.Switch, defaultValue = "true")
    private Boolean compress = true;

    @ConfigFieldInfo(label = "SQL预处理", inputType = FieldInputType.Switch, defaultValue = "false")
    private Boolean preHandle = false;
}
