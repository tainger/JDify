package io.terminus.dalaran.component.processor.sql;

import io.terminus.dalaran.DalaranProcessor;
import io.terminus.dalaran.annotation.Processor;
import org.apache.camel.model.ProcessorDefinition;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import javax.sql.DataSource;

@Processor(value = "sql", configType = SqlConfig.class, serializedBody = false)
public class SqlProcessor implements DalaranProcessor<SqlConfig>, BeanFactoryPostProcessor {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void configure(ProcessorDefinition route, SqlConfig config) {
        String dataSourceBeanName = getDataSourceBeanName(config.getConnectorId());
        if (!beanFactory.containsSingleton(dataSourceBeanName)) {
            DataSource dataSource = buildDataSource(config.getConnector());
            beanFactory.registerSingleton(dataSourceBeanName, dataSource);
        }
        // TODO 目前取值只支持一级, 后面可用通过实现 SqlPrepareStatementStrategy 来扩展
        route.to("sql:" + config.getSql() + "?dataSource=#" + dataSourceBeanName);
    }


    private javax.sql.DataSource buildDataSource(SqlDataSourceConnector connector) {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setUsername(connector.getUsername());
        dataSource.setPassword(connector.getPassword());
        switch (connector.getDatabaseType()) {
            case MYSQL:
                String urlTemplate = "jdbc:mysql://%s:%s/%s";
                String url = String.format(urlTemplate, connector.getHost(), connector.getPort(), connector.getSchema());
                dataSource.setUrl(url);
                dataSource.setDriverClassName("com.mysql.jdbc.Driver");
                break;
//            case ORACLE:
//                break;
        }
        return dataSource;
    }

    private String getDataSourceBeanName(Long connectorId) {
        return "sqlDataSource-" + connectorId;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
