package io.terminus.dalaran.example;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;

public class TestDubboCaller {

    public static void main(String[] args) {
        ReferenceConfig<OrderService> reference = new ReferenceConfig<>();
        reference.setApplication(new ApplicationConfig("test"));
        reference.setRegistry(new RegistryConfig("zookeeper://localhost:2181"));
        reference.setVersion("terminus");
//        reference.setInterface("io.terminus.dalaran.example.OrderService"); // 接口名
        reference.setInterface("io.terminus.dalaran.example.OrderService"); // 接口名
        ReferenceConfigCache cache = ReferenceConfigCache.getCache();
        OrderService genericService = cache.get(reference);
        OrderItem item = new OrderItem();
        item.setName("abc");
        item.setPrice(3.33);
        TestOrder abc = genericService.getUserOrders(item);//, new String[]{"java.lang.String"}, new Object[]{"testAbc"});
        System.out.println(abc.getOrderNumber());
    }
}
