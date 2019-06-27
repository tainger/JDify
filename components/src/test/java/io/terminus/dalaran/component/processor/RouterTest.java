package io.terminus.dalaran.component.processor;

import io.terminus.dalaran.component.BasicProcessorTest;
import io.terminus.dalaran.component.processor.route.DalaranRouter;
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
