package io.terminus.dalaran.component.trigger;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.alibaba.dubbo.rpc.service.GenericService;
import io.terminus.dalaran.component.BasicTriggerTest;
import io.terminus.dalaran.component.trigger.dubbo.DalaranDubboProvider;
import io.terminus.dalaran.component.trigger.dubbo.DubboProviderConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DubboTest extends BasicTriggerTest {

    private TestingServer zkTestServer;
    private CuratorFramework cli;

    private ApplicationConfig applicationConfig = new ApplicationConfig("dalaran-unit-test");
    private ServiceConfig provider = new ServiceConfig();

    private static final String REGISTRY_ADDRESS = "zookeeper://localhost:52181";
    private static final String DUBBO_VERSION = "1.0.0";
    private static final String DUBBO_METHOD = "execute";
    private static final String DUBBO_SERVICE_ID = "io.terminus.dalaran.TestDubboService";

    public static final String INPUT_STRING = "terminus";
    public static final String SUCCESSFUL_MESSAGE = "call is successful:";

    @Test
    public void test() {
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setApplication(applicationConfig);
        reference.setRegistry(new RegistryConfig(REGISTRY_ADDRESS));
        reference.setVersion(DUBBO_VERSION);
        reference.setInterface(DUBBO_SERVICE_ID);
        reference.setGeneric(true);
        ReferenceConfigCache cache = ReferenceConfigCache.getCache();
        GenericService genericService = cache.get(reference);

        String result = (String) genericService.$invoke(DUBBO_METHOD, new String[]{
        }, new Object[]{
                INPUT_STRING
        });
        Assert.assertEquals(result, SUCCESSFUL_MESSAGE + INPUT_STRING);
    }

    @Override
    public Object process(Object param) {
        return SUCCESSFUL_MESSAGE + param;
    }

    @Before
    public void before() throws Exception {
        zkTestServer = new TestingServer(52181);
        zkTestServer.start();
        DalaranDubboProvider trigger = new DalaranDubboProvider();
        DubboProviderConfig config = new DubboProviderConfig();

        config.setRegistryAddress(REGISTRY_ADDRESS);
        config.setServiceId(DUBBO_SERVICE_ID);
        config.setMethod(DUBBO_METHOD);
        config.setVersion(DUBBO_VERSION);
        config.setParameterType(String.class.getCanonicalName());

        registerTrigger(trigger, config);
    }

    @After
    public void after() throws Exception {
        // TODO provider unregister 不是同步的, 而且要慢半拍, 会导致执行完毕后抛异常, 影响不大, 就是慢了点
        zkTestServer.stop();
    }
}
