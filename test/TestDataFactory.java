package test;

import model.*;

import java.util.ArrayList;
import java.util.List;

public class TestDataFactory {

    public static List<Order> createOrders() {

        MenuItem burger = new Food("F01", "Burger", 50000, true);
        MenuItem coke = new Drink("D01", "Coca", 15000, true, "M");

        OrderItem item1 = new OrderItem(burger, 2, burger.getPrice());
        OrderItem item2 = new OrderItem(coke, 3, coke.getPrice());

        Order order = new Order("O01");
        order.addItem(item1);
        order.addItem(item2);
        order.setStatus(OrderStatus.PAID);

        List<Order> orders = new ArrayList<>();
        orders.add(order);

        return orders;
    }
}
