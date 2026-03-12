import java.util.ArrayList;
import java.util.List;

public class Order {
    private String id;
    private List<OrderItem> items;
    private OrderStatus status;
    private double discount;

    public Order(String id) {
        this.id = id;
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.discount = 0;
    }

    // Thêm món
    public void addItem(String name, double price, int quantity) {
        OrderItem item = new OrderItem(name, price, quantity);
        items.add(item);
    }

    // Xóa món
    public void removeItem(String name) {
        items.removeIf(item -> item.getItemName().equals(name));

    }

    // Áp dụng giảm giá
    public void applyDiscount(double percent) {
        this.discount = percent;
    }

    // Tính tổng tiền (dùng Stream API)
    public double calculateTotal() {
        double total = items.stream()
                .mapToDouble(OrderItem::getTotal)
                .sum();

        total = total - (total * discount / 100);
        return total;
    }

    // Cập nhật trạng thái
    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    // Hiển thị đơn hàng
    public void displayOrder() {
        System.out.println("Order ID: " + id);
        System.out.println("Status: " + status);
        for (OrderItem item : items) {
            System.out.println(item.getItemName() + " x" + item.getQuantity());
        }
        System.out.println("Total: " + calculateTotal());
    }

    public String getId() {
        return id;
    }
}