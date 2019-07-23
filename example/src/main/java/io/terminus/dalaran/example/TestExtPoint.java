package io.terminus.dalaran.example;

import io.terminus.dalaran.DalaranIntegration;
import io.terminus.dalaran.DalaranIntegrationAction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@DalaranIntegration(key = "TEST-EXT-POINT", name = "测试用集成扩展点")
public interface TestExtPoint {

    Map getName(String a, String b);

    String buildOrder(long orderNumber, List<Integer> orderItems);

    String buildOrder2(long orderNumber, List<Map> orderItems);

    @DalaranIntegrationAction(key = "getAllOrder-Array", name = "获取定案接口, 以数组为入参")
    OrderItem allOrders(Long orderNumber, List<ExtOrderItem> orderItems);

    Set<OrderItem> allOrders(BigDecimal orderNumber, ExtOrderItem[] orderItems);
}
