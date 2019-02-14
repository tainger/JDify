package io.terminus.dalaran.example;

import java.util.List;

public interface OrderService {
    TestOrder getUserOrders(OrderItem item);

    TestOrder getFirst();
}
