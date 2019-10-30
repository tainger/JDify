package io.terminus.dalaran.component.processor.sql;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.connector.SqlDataSourceConnector;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.config.ConnectorConfig;
import lombok.Data;

@Data
public class SqlConfig implements ConnectorConfig<SqlDataSourceConnector> {

    @ConfigFieldInfo(inputType = FieldInputType.Hidden)
    @JSONField(serialize = false)
    @JsonIgnore
    private SqlDataSourceConnector connector;

    @ConfigFieldInfo(label = "DB 数据源", inputType = FieldInputType.Connector, connectorType = SqlDataSourceConnector.class)
    private Long connectorId;

    @ConfigFieldInfo(label = "SQL 语句", inputType = FieldInputType.SQL)
    private String sql;
}
