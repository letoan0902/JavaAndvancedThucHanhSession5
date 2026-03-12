package test;

import model.*;

import java.util.ArrayList;
import java.util.List;

public class TestDataFactory {

    public static List<Order> createOrders() {

        MenuItem burger = new Food("F01", "Burger", 50000, true);
        MenuItem coke = new Drink("D01", "Coca", 15000, true, "M");

        // OrderItem tự động lấy giá từ calculatePrice()
        // Burger: 50000, Coca size M: 15000 + 5000 = 20000
        OrderItem item1 = new OrderItem(burger, 2);
        OrderItem item2 = new OrderItem(coke, 3);

        Order order = new Order("O01");
        order.addItem(item1);
        order.addItem(item2);
        order.setStatus(OrderStatus.PAID);

        List<Order> orders = new ArrayList<>();
        orders.add(order);

        return orders;
    }
}
