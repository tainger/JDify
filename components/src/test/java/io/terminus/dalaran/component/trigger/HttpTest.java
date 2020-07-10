package io.terminus.dalaran.component.trigger;

import io.terminus.dalaran.component.BasicTriggerTest;
import io.terminus.dalaran.component.common.HttpMethod;
import io.terminus.dalaran.component.trigger.rest.RestConfig;
import io.terminus.dalaran.component.trigger.rest.RestListener;
import io.terminus.dalaran.model.HttpProtocol;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class HttpTest extends BasicTriggerTest {

    private static final Integer MOCK_PORT = 8765;
    private static final String MOCK_PATH = "/api/test";
    private static final String MOCK_REQUEST_BODY = "Lok'Tar O'gar";
    private static final String SUCCESSFUL_MESSAGE = "call is successful:";

    @Test
    public void test() throws IOException {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        HttpPost httpRequest = new HttpPost("http://localhost:" + MOCK_PORT + MOCK_PATH);
        httpRequest.setEntity(new StringEntity(MOCK_REQUEST_BODY));
        HttpResponse response = closeableHttpClient.execute(httpRequest);
        String responseBody = StringUtils.join(IOUtils.readLines(response.getEntity().getContent()));
        Assert.assertEquals(responseBody, SUCCESSFUL_MESSAGE + MOCK_REQUEST_BODY);
    }

    @Before
    public void before() {
        RestListener trigger = new RestListener();
        RestConfig config = new RestConfig();
        config.setProtocol(HttpProtocol.HTTP);
        config.setPort(MOCK_PORT);
        config.setPath(MOCK_PATH);
        config.setMethod(HttpMethod.POST);

        registerTrigger(trigger, config);
    }


    @Override
    public Object process(Object param) {
        String requestBody = (String) param;
        return SUCCESSFUL_MESSAGE + requestBody;
    }
}
