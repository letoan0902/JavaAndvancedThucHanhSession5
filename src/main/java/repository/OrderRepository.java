package repository;

import exception.InvalidOrderIdException;
import model.Order;
import model.OrderStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository quản lý danh sách đơn hàng
 * Sử dụng model.Order thống nhất
 */
public class OrderRepository {

    private List<Order> orders;

    public OrderRepository() {
        this.orders = new ArrayList<>();
    }

    // Thêm đơn hàng
    public void add(Order order) {
        orders.add(order);
    }

    // Tìm đơn hàng theo ID (trả về Optional - SRS yêu cầu)
    public Optional<Order> findById(String id) {
        return orders.stream()
                .filter(o -> o.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    // Cập nhật trạng thái đơn hàng
    public void updateStatus(String id, OrderStatus status) throws InvalidOrderIdException {
        Order order = findById(id)
                .orElseThrow(() -> new InvalidOrderIdException(id, "Không tìm thấy đơn hàng"));
        order.updateStatus(status);
    }

    // Tính tổng doanh thu (chỉ tính đơn PAID)
    public double sum() {
        return orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PAID)
                .mapToDouble(Order::calculateTotal)
                .sum();
    }

    // Hiển thị tất cả đơn hàng
    public void display() {
        if (orders.isEmpty()) {
            System.out.println("Chưa có đơn hàng nào!");
            return;
        }
        for (Order o : orders) {
            o.displayOrder();
        }
    }

    // Lấy tất cả đơn hàng
    public List<Order> getAll() {
        return orders;
    }

    // Kiểm tra ID đã tồn tại
    public boolean isIdExisted(String id) {
        return orders.stream().anyMatch(o -> o.getId().equalsIgnoreCase(id));
    }
}
