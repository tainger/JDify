package io.terminus.dalaran.component.processor;

import io.terminus.dalaran.component.BasicProcessorTest;
import io.terminus.dalaran.component.connector.SqlDataSourceConnector;
import io.terminus.dalaran.component.processor.sql.Database;
import io.terminus.dalaran.component.processor.sql.SqlConfig;
import io.terminus.dalaran.component.processor.sql.SqlProcessor;
import org.apache.camel.ProducerTemplate;
import org.junit.Assert;
import org.junit.Test;

public class SqlTest extends BasicProcessorTest {

    @Test
    public void testScriptComponent() {
        SqlProcessor processor = new SqlProcessor();
        SqlConfig config = new SqlConfig();
        SqlDataSourceConnector connector = new SqlDataSourceConnector();
        connector.setDatabaseType(Database.MYSQL);
        connector.setHost("localhost");
        connector.setPort(3306);
        connector.setSchema("test");
        connector.setUsername("root");
        connector.setPassword("anywhere");
        config.setConnector(connector);
        config.setSql("SELECT * FROM `order_entity` LIMIT 0,3000;");

        ProducerTemplate template = getProcessorTemplate(processor, config);
        Assert.assertNotNull(template);
        Object result = template.requestBody("");
        System.out.println(result);
    }
}
