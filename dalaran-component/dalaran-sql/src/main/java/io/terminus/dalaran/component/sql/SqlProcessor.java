package io.terminus.dalaran.component.sql;

import io.terminus.dalaran.ComponentConstants;
import io.terminus.dalaran.component.connector.SqlDataSourceConnector;
import io.terminus.dalaran.core.component.DalaranProcessor;
import io.terminus.dalaran.core.component.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.commons.lang3.StringUtils;
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
//        String dataSourceBeanName = getDataSourceBeanName(config.getConnectorId());
        String dataSourceBeanName = config.getConnector().toString();
        if (!beanFactory.containsSingleton(dataSourceBeanName)) {
            DataSource dataSource = buildDataSource(config.getConnector());
            beanFactory.registerSingleton(dataSourceBeanName, dataSource);
        }
        // TODO 目前取值只支持一级, 后面可用通过实现 SqlPrepareStatementStrategy 来扩展
        String uri;
        if (config.getSqlType() == SQLType.STORED_PROCEDURE) {
            uri = "sql-stored:";
        } else {
            uri = "sql:";
        }
        if (config.getCompress()) {
            config.setSql(compressSql(config.getSql()));
        }
        route.process(new SqlBeforeProcessor(config));
        if (config.getPreHandle()) {
            route.toD(uri + "${headers." + ComponentConstants.PRE_HANDLE_SQL + "}?dataSource=" + dataSourceBeanName);
        } else {
            route.to(uri + config.getSql() + "?dataSource=" + dataSourceBeanName);
        }
        route.process(new SqlAfterProcessor(config.getOperationType()));
    }

    private DataSource buildDataSource(SqlDataSourceConnector connector) {
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
            case ORACLE:
                urlTemplate = "jdbc:oracle:thin:@%s:%s/%s";
                url = String.format(urlTemplate, connector.getHost(), connector.getPort(), connector.getSchema());
                dataSource.setUrl(url);
                dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
                break;
        }
        return dataSource;
    }

    private String getDataSourceBeanName(Long connectorId) {
        return "sqlDataSource-" + connectorId;
    }

    private String compressSql(String originSql) {
        String[] ignore = {"\n", "\r", "\t"};
        String[] replace = {" ", " ", " "};
        originSql = StringUtils.replaceEachRepeatedly(originSql, ignore, replace);
        return originSql;
    }

    private Boolean equals(org.apache.tomcat.jdbc.pool.DataSource old, SqlDataSourceConnector connector) {
        String oldValue = old.getDriverClassName() + old.getUrl()
                + old.getUsername() + old.getPassword() + old.getMaxIdle()
                + old.getMinIdle() + old.getMaxWait() + old.getInitialSize();
        org.apache.tomcat.jdbc.pool.DataSource current = (org.apache.tomcat.jdbc.pool.DataSource)buildDataSource(connector);
        String currentValue = current.getDriverClassName() + current.getUrl() +
                current.getUsername() + current.getPassword() + current.getMaxIdle()
                + current.getMinIdle() + current.getMaxWait() + current.getInitialSize();
        return StringUtils.equalsIgnoreCase(oldValue, currentValue);
    }
}
