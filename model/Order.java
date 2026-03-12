package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private String id;
    private LocalDateTime createdAt;
    private List<OrderItem> items;
    private OrderStatus status;
    private double discountPercent;

    public Order(String id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
    }

    public void addItem(OrderItem item) {
        items.add(item);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
    }

    public double calculateTotal() {

        double total = items.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();

        if (discountPercent > 0) {
            total -= total * discountPercent / 100;
        }

        return total;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
