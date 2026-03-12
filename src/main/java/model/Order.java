package model;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private String id;
    private LocalDateTime createdAt;
    private List<OrderItem> items;
    private OrderStatus status;
    private double discountPercent;
    private double serviceFeePercent; // Phí dịch vụ (SRS yêu cầu)

    public Order(String id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.discountPercent = 0;
        this.serviceFeePercent = 0;
    }

    // Thêm món vào đơn hàng
    public void addItem(OrderItem item) {
        items.add(item);
    }

    // Xóa món theo tên
    public void removeItem(String itemName) {
        items.removeIf(oi -> oi.getItem().getName().equalsIgnoreCase(itemName));
    }

    // Xóa món theo đối tượng OrderItem
    public void removeItem(OrderItem item) {
        items.remove(item);
    }

    // Áp dụng mã giảm giá
    public void applyDiscount(double percent) {
        this.discountPercent = percent;
    }

    // Áp dụng phí dịch vụ
    public void applyServiceFee(double percent) {
        this.serviceFeePercent = percent;
    }

    // Tính tổng tiền (dùng Stream API)
    public double calculateTotal() {
        double total = items.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();

        // Áp dụng giảm giá
        if (discountPercent > 0) {
            total -= total * discountPercent / 100;
        }

        // Áp dụng phí dịch vụ
        if (serviceFeePercent > 0) {
            total += total * serviceFeePercent / 100;
        }

        return total;
    }

    // Cập nhật trạng thái đơn hàng
    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    // Hiển thị đơn hàng
    public void displayOrder() {
        NumberFormat nf = NumberFormat.getNumberInstance();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        System.out.println("========================================");
        System.out.println("Order ID: " + id);
        System.out.println("Ngày tạo: " + createdAt.format(dtf));
        System.out.println("Trạng thái: " + status);
        System.out.println("----------------------------------------");
        for (OrderItem item : items) {
            System.out.println("  " + item);
        }
        System.out.println("----------------------------------------");
        if (discountPercent > 0) {
            System.out.println("Giảm giá: " + discountPercent + "%");
        }
        if (serviceFeePercent > 0) {
            System.out.println("Phí dịch vụ: " + serviceFeePercent + "%");
        }
        System.out.println("TỔNG: " + nf.format(calculateTotal()) + " VND");
        System.out.println("========================================");
    }

    // Getters & Setters
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

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public double getServiceFeePercent() {
        return serviceFeePercent;
    }

    public void setServiceFeePercent(double serviceFeePercent) {
        this.serviceFeePercent = serviceFeePercent;
    }

    @Override
    public String toString() {
        NumberFormat nf = NumberFormat.getNumberInstance();
        return "Order{" + id + ", status=" + status + ", total=" + nf.format(calculateTotal()) + " VND}";
    }
}
