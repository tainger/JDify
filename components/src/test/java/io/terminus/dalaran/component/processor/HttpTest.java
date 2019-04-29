package io.terminus.dalaran.component.processor;

import com.alibaba.dubbo.common.utils.IOUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.sun.net.httpserver.HttpServer;
import io.terminus.dalaran.component.BasicProcessorTest;
import io.terminus.dalaran.component.processor.http.*;
import org.apache.camel.ProducerTemplate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

public class HttpTest extends BasicProcessorTest {

    private HttpServer httpServer;

    private static final Integer MOCK_PORT = 8765;
    private static final String MOCK_PATH = "/api/test";
    private static final String MOCK_REQUEST_BODY = "Lok'Tar O'gar";
    private static final String METHOD_NOT_SUPPORTED = "method not supported";

    @Test
    public void test() throws IOException {
        DalaranHttpClient processor = new DalaranHttpClient();
        HttpClientConfig config = new HttpClientConfig();
        HttpClientConnector connector = new HttpClientConnector();
        connector.setProtocol(HttpProtocol.HTTP);
        connector.setHost("localhost");
        connector.setPort(MOCK_PORT);
        config.setConnector(connector);
        config.setPath(MOCK_PATH);
        config.setMethod(HttpMethod.GET);

        ProducerTemplate httpGetTemplate = getProcessorTemplate(processor, config);

        Assert.assertNotNull(httpGetTemplate);
        InputStream resultInput = (InputStream) httpGetTemplate.requestBody(MOCK_REQUEST_BODY);
        String result = StringUtils.join(IOUtils.readLines(resultInput));
        Assert.assertEquals(result, METHOD_NOT_SUPPORTED);

        HttpClientConfig config2 = new HttpClientConfig();
        HttpClientConnector connector2 = new HttpClientConnector();
        connector2.setProtocol(HttpProtocol.HTTP);
        connector2.setHost("localhost");
        connector2.setPort(MOCK_PORT);
        config2.setConnector(connector2);
        config2.setPath(MOCK_PATH);
        config2.setMethod(HttpMethod.POST);

        ProducerTemplate template = getProcessorTemplate(processor, config2);

        Assert.assertNotNull(template);
        InputStream resultInput2 = (InputStream) template.requestBody(MOCK_REQUEST_BODY);
        String result2 = StringUtils.join(IOUtils.readLines(resultInput2));
        Assert.assertEquals(result2, "result: " + MOCK_REQUEST_BODY);
    }

    @Before
    public void before() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(MOCK_PORT), 0);
        httpServer.createContext(MOCK_PATH, exchange -> {
            String response;
            if ("POST".equals(exchange.getRequestMethod())) {
                String requestBody = StringUtils.join(IOUtils.readLines(exchange.getRequestBody()));
                response = "result: " + requestBody;
            } else {
                response = METHOD_NOT_SUPPORTED;
            }
            byte[] responseBytes = response.getBytes();
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, responseBytes.length);
            exchange.getResponseBody().write(responseBytes);
            exchange.close();
        });
        httpServer.start();
    }

    @After
    public void after() {
        httpServer.stop(0);
    }

}
