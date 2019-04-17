package io.terminus.dalaran.component.processor;

import com.alibaba.dubbo.common.utils.IOUtils;
import com.alibaba.dubbo.common.utils.PojoUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.rpc.service.GenericService;
import io.terminus.dalaran.component.BasicProcessorTest;
import io.terminus.dalaran.component.processor.dubbo.DalaranDubboConsumer;
import io.terminus.dalaran.component.processor.dubbo.DalaranDubboConsumerConfig;
import io.terminus.dalaran.component.processor.http.HttpMethod;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultMessage;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class DubboTest extends BasicProcessorTest {

    private TestingServer zkTestServer;

    private ApplicationConfig applicationConfig = new ApplicationConfig("dalaran-unit-test");
    private ServiceConfig provider = new ServiceConfig();

    private static final String REGISTRY_ADDRESS = "zookeeper://localhost:52181";
    private static final String DUBBO_VERSION = "1.0.0";
    private static final String DUBBO_METHOD = "execute";
    private static final String DUBBO_SERVICE_ID = "io.terminus.dalaran.TestDubboService";

    private static final Integer INPUT_NUMBER = 998;

    @Test
    public void test() {
        DalaranDubboConsumer processor = new DalaranDubboConsumer();
        DalaranDubboConsumerConfig config = new DalaranDubboConsumerConfig();
        config.setRegistryAddress(REGISTRY_ADDRESS);
        config.setServiceId(DUBBO_SERVICE_ID);
        config.setMethod(DUBBO_METHOD);
        config.setVersion(DUBBO_VERSION);

        ProducerTemplate httpGetTemplate = getProcessorTemplate(processor, config);

        Assert.assertNotNull(httpGetTemplate);
        Integer result = (Integer) httpGetTemplate.requestBody(INPUT_NUMBER);
        Assert.assertTrue(result == INPUT_NUMBER * 1024);
    }


    @Before
    public void startZookeeper() throws Exception {
        zkTestServer = new TestingServer(52181);
        zkTestServer.start();
        provider.setApplication(applicationConfig);
        provider.setRegistry(new RegistryConfig(REGISTRY_ADDRESS));
        provider.setVersion(DUBBO_VERSION);
        provider.setInterface(DUBBO_SERVICE_ID);
        GenericService serviceBean = (method, parameterTypes, args) -> {
            if (DUBBO_METHOD.equals(method)) {
                Integer num = (Integer) args[0];
                return num * 1024;
            }
            throw new RuntimeException("no such method");
        };
        provider.setRef(serviceBean);
        provider.export();
    }

    @After
    public void stopZookeeper() throws IOException {
        provider.unexport();
        zkTestServer.stop();
    }

}
