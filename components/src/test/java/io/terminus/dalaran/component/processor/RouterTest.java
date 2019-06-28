package io.terminus.dalaran.component.processor;

import io.terminus.dalaran.component.BasicProcessorTest;
import io.terminus.dalaran.component.processor.route.DalaranRouter;
import io.terminus.dalaran.component.processor.sql.Database;
import io.terminus.dalaran.component.processor.sql.SqlConfig;
import io.terminus.dalaran.component.processor.sql.SqlDataSourceConnector;
import io.terminus.dalaran.component.processor.sql.SqlProcessor;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.collections.MapUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jingdi on 2019/6/26
 */
public class RouterTest extends BasicProcessorTest {

    private static final String MYSQL_HOST = "127.0.0.1";

    private static final Integer MYSQL_PORT = 3306;

    private static final String SCHEMA = "ability-test";

    private static final String USER_NAME = "root";

    private static final String PASSWORD = "secret";

    private static final int id = 3;

    /**
     * id = 1: insert branch01
     * id = 2: insert branch02
     * id = 3: insert branch03
     * otherwise: insert default
     */
    @Test
    public void testRouterTest() {
        DalaranRouter router = new DalaranRouter();

        SqlDataSourceConnector connector = new SqlDataSourceConnector();
        connector.setDatabaseType(Database.MYSQL);
        connector.setHost(MYSQL_HOST);
        connector.setPort(MYSQL_PORT);
        connector.setUsername(USER_NAME);
        connector.setPassword(PASSWORD);
        connector.setSchema(SCHEMA);

        SqlProcessor sqlProcessor1 = new SqlProcessor();
        SqlConfig sqlConfig1 = new SqlConfig();
        sqlConfig1.setConnector(connector);
        sqlConfig1.setSql("insert into user (name, age, company_id, salary) values ('branch01', 18, 1, 1)\"}");

        SqlProcessor sqlProcessor2 = new SqlProcessor();
        SqlConfig sqlConfig2 = new SqlConfig();
        sqlConfig2.setConnector(connector);
        sqlConfig2.setSql("insert into user (name, age, company_id, salary) values ('branch02', 18, 1, 1)\"}");

        SqlProcessor sqlProcessor3 = new SqlProcessor();
        SqlConfig sqlConfig3 = new SqlConfig();
        sqlConfig3.setConnector(connector);
        sqlConfig3.setSql("insert into user (name, age, company_id, salary) values ('branch03', 18, 1, 1)\"}");

        SqlProcessor sqlProcessor4 = new SqlProcessor();
        SqlConfig sqlConfig4 = new SqlConfig();
        sqlConfig4.setConnector(connector);
        sqlConfig4.setSql("insert into user (name, age, company_id, salary) values ('default', 18, 1, 1)\"}");



        Map<String, String> config = new HashMap<>();

        ProducerTemplate template = getProcessorTemplate(router, config);
        Assert.assertNotNull(template);

        Map<String, Map<String, Object>> requestBody = new HashMap<>();
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        requestBody.put("user", body);

        Object result = template.requestBody(requestBody);
        Assert.assertNotNull(result);
    }
}
