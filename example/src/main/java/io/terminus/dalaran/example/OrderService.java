package io.terminus.dalaran.example;

public interface OrderService {
    TestOrder getUserOrders(OrderItem item);

    TestOrder getFirst();
}
