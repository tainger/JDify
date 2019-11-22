package io.terminus.dalaran.component.processor.sql;

import io.terminus.dalaran.component.connector.SqlDataSourceConnector;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import javax.sql.DataSource;

@Processor(
        value = "sql",
        order = 12,
        configType = SqlConfig.class
)
public class SqlProcessor implements DalaranProcessor<SqlConfig> {

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void configure(ProcessorDefinition route, SqlConfig config) {
        String dataSourceBeanName = getDataSourceBeanName(config.getConnectorId());
        if (!beanFactory.containsSingleton(dataSourceBeanName)) {
            DataSource dataSource = buildDataSource(config.getConnector());
            beanFactory.registerSingleton(dataSourceBeanName, dataSource);
        }
        // TODO 目前取值只支持一级, 后面可用通过实现 SqlPrepareStatementStrategy 来扩展
        route.to("sql:" + config.getSql() + "?dataSource=" + dataSourceBeanName);
    }


    private javax.sql.DataSource buildDataSource(SqlDataSourceConnector connector) {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();

        dataSource.setMaxIdle(connector.getMaxIdle());
        dataSource.setMinIdle(connector.getMinIdle());
        dataSource.setMaxWait(connector.getMaxWait());
        dataSource.setInitialSize(connector.getInitialSize());
        dataSource.setTimeBetweenEvictionRunsMillis(18800);

        dataSource.setTestOnBorrow(false);
        dataSource.setTestWhileIdle(true);

        dataSource.setUsername(connector.getUsername());
        dataSource.setPassword(connector.getPassword());
        String urlTemplate;
        String url;
        switch (connector.getDatabaseType()) {
            case MYSQL:
                urlTemplate = "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf-8&useSSL=false";
                url = String.format(urlTemplate, connector.getHost(), connector.getPort(), connector.getSchema());
                dataSource.setUrl(url);
                dataSource.setValidationQuery("select 1");
                dataSource.setDriverClassName("com.mysql.jdbc.Driver");
                break;
            case SQL_SERVER:
                urlTemplate = "jdbc:sqlserver://%s:%s;databaseName=%s";
                url = String.format(urlTemplate, connector.getHost(), connector.getPort(), connector.getSchema());
                dataSource.setUrl(url);
                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                break;
//            case ORACLE:
//                break;
        }
        return dataSource;
    }

    private String getDataSourceBeanName(Long connectorId) {
        return "sqlDataSource-" + connectorId;
    }
}
