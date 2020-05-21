package io.terminus.dalaran.component.connector;

import io.terminus.dalaran.FieldInputType;
import io.terminus.dalaran.component.processor.sql.Database;
import io.terminus.dalaran.core.component.annotation.ConfigFieldInfo;
import io.terminus.dalaran.core.component.annotation.Connector;
import lombok.Data;

@Data
@Connector("DataBase")
public class SqlDataSourceConnector {

    @ConfigFieldInfo(label = "数据库类型", inputType = FieldInputType.Select, defaultValue = "MYSQL")
    private Database databaseType;

    @ConfigFieldInfo(label = "主机地址", inputType = FieldInputType.String)
    private String host;

    @ConfigFieldInfo(label = "端口", inputType = FieldInputType.Integer, defaultValue = "3306")
    private Integer port;

    @ConfigFieldInfo(label = "用户名", inputType = FieldInputType.String)
    private String username;

    @ConfigFieldInfo(label = "密码", inputType = FieldInputType.Password)
    private String password;

    @ConfigFieldInfo(label = "Schema/SID", inputType = FieldInputType.String)
    private String schema;

    @ConfigFieldInfo(label = "最小闲置线程", inputType = FieldInputType.Integer, defaultValue = "5")
    private int minIdle = 5;
    @ConfigFieldInfo(label = "最大闲置线程", inputType = FieldInputType.Integer, defaultValue = "10")
    private int maxIdle = 10;
    @ConfigFieldInfo(label = "最大等待线程", inputType = FieldInputType.Integer, defaultValue = "10000")
    private int maxWait = 10000;
    @ConfigFieldInfo(label = "初始化线程数", inputType = FieldInputType.Integer, defaultValue = "5")
    private int initialSize = 10;
}
