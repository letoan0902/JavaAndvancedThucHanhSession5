import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderRepository {
    private List<Order> orders = new ArrayList<>();

    // Thêm đơn
    public void add(Order order) {
        orders.add(order);
    }

    // Tìm đơn theo ID
    public Optional<Order> findById(String id) {
        return orders.stream()
                .filter(o -> o.getId().equals(id))
                .findFirst();

    }

    // Hiển thị tất cả đơn
    public void display() {
        for (Order o : orders) {
            o.displayOrder();
            System.out.println("----------------");
        }
    }
}