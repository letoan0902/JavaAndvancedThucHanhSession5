package repository.legacy;

import exception.InvalidOrderIdException;
import model.*;
import repository.MenuRepository;
import repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    private MenuRepository menuRepo;
    private OrderRepository orderRepo;

    @BeforeEach
    void setUp() {
        menuRepo = new MenuRepository();
        orderRepo = new OrderRepository();

        menuRepo.add(new Food("F01", "Burger", 50000, true));
        menuRepo.add(new Drink("D01", "Coca", 10000, true, "S"));
        menuRepo.add(new Drink("D02", "Trà sữa", 25000, true, "M"));
        menuRepo.add(new Dessert("DS01", "Kem", 20000, true));
    }

    // ===== TEST TÍNH TIỀN =====

    @Test
    void testAddItemAndCalculateTotal() {
        Order order = new Order("O01");
        MenuItem burger = menuRepo.findById("F01").get();
        order.addItem(new OrderItem(burger, 2));

        double total = order.calculateTotal();
        assertEquals(100000, total, 0.01, "Burger 50000 x 2 = 100000");
    }

    @Test
    void testCalculateTotalMultipleItems() {
        Order order = new Order("O01");

        MenuItem burger = menuRepo.findById("F01").get();
        MenuItem coca = menuRepo.findById("D01").get();

        order.addItem(new OrderItem(burger, 2)); // 50000 x 2 = 100000
        order.addItem(new OrderItem(coca, 1));   // 10000 x 1 = 10000

        double total = order.calculateTotal();
        assertEquals(110000, total, 0.01);
    }

    @Test
    void testDrinkSizePricing() {
        // Trà sữa size M: 25000 + 5000 = 30000
        MenuItem traSua = menuRepo.findById("D02").get();
        OrderItem item = new OrderItem(traSua, 2);

        assertEquals(60000, item.getTotalPrice(), 0.01, "Trà sữa M: (25000+5000) x 2 = 60000");
    }

    @Test
    void testApplyDiscount() {
        Order order = new Order("O01");
        MenuItem burger = menuRepo.findById("F01").get();
        order.addItem(new OrderItem(burger, 2)); // 100000

        order.applyDiscount(10); // Giảm 10%
        double total = order.calculateTotal();
        assertEquals(90000, total, 0.01, "100000 - 10% = 90000");
    }

    @Test
    void testApplyServiceFee() {
        Order order = new Order("O01");
        MenuItem burger = menuRepo.findById("F01").get();
        order.addItem(new OrderItem(burger, 2)); // 100000

        order.applyServiceFee(5); // Phí dịch vụ 5%
        double total = order.calculateTotal();
        assertEquals(105000, total, 0.01, "100000 + 5% = 105000");
    }

    @Test
    void testDiscountAndServiceFeeTogether() {
        Order order = new Order("O01");
        MenuItem burger = menuRepo.findById("F01").get();
        order.addItem(new OrderItem(burger, 2)); // 100000

        order.applyDiscount(10);    // Giảm 10% → 90000
        order.applyServiceFee(5);   // Phí dịch vụ 5% → 90000 + 4500 = 94500
        double total = order.calculateTotal();
        assertEquals(94500, total, 0.01);
    }

    // ===== TEST THÊM/XÓA MÓN =====

    @Test
    void testRemoveItemByName() {
        Order order = new Order("O01");
        MenuItem burger = menuRepo.findById("F01").get();
        MenuItem coca = menuRepo.findById("D01").get();

        order.addItem(new OrderItem(burger, 2));
        order.addItem(new OrderItem(coca, 1));

        order.removeItem("Burger");
        assertEquals(1, order.getItems().size(), "Sau khi xóa Burger chỉ còn 1 món");
        assertEquals(10000, order.calculateTotal(), 0.01);
    }

    // ===== TEST TRẠNG THÁI ĐƠN HÀNG =====

    @Test
    void testDefaultStatusIsPending() {
        Order order = new Order("O01");
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void testUpdateStatus() {
        Order order = new Order("O01");
        order.updateStatus(OrderStatus.PAID);
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    // ===== TEST ORDER REPOSITORY =====

    @Test
    void testOrderRepositoryAddAndFind() {
        Order order = new Order("O01");
        orderRepo.add(order);

        Optional<Order> found = orderRepo.findById("O01");
        assertTrue(found.isPresent(), "Phải tìm thấy đơn hàng O01");
        assertEquals("O01", found.get().getId());
    }

    @Test
    void testOrderRepositoryFindByIdNotFound() {
        Optional<Order> found = orderRepo.findById("NOT_EXIST");
        assertTrue(found.isEmpty(), "Không được tìm thấy đơn hàng không tồn tại");
    }

    // ===== TEST EXCEPTION =====

    @Test
    void testInvalidOrderIdException() {
        assertThrows(InvalidOrderIdException.class, () -> {
            orderRepo.updateStatus("INVALID_ID", OrderStatus.PAID);
        }, "Phải throw InvalidOrderIdException khi ID không tồn tại");
    }

    @Test
    void testOrderRepositorySum() {
        Order order1 = new Order("O01");
        order1.addItem(new OrderItem(menuRepo.findById("F01").get(), 2)); // 100000
        order1.setStatus(OrderStatus.PAID);

        Order order2 = new Order("O02");
        order2.addItem(new OrderItem(menuRepo.findById("D01").get(), 3)); // 30000
        order2.setStatus(OrderStatus.PENDING); // Chưa thanh toán, không tính

        orderRepo.add(order1);
        orderRepo.add(order2);

        double sum = orderRepo.sum();
        assertEquals(100000, sum, 0.01, "Chỉ tính đơn PAID: 100000");
    }

    // ===== TEST MENU REPOSITORY =====

    @Test
    void testMenuFindByName() {
        var results = menuRepo.findByName("Burger");
        assertEquals(1, results.size());
        assertEquals("Burger", results.get(0).getName());
    }

    @Test
    void testMenuFindByPriceRange() {
        var results = menuRepo.findByPriceRange(10000, 30000);
        assertTrue(results.size() >= 2, "Phải tìm được ít nhất 2 món trong khoảng 10000-30000");
    }

    @Test
    void testMenuDeleteById() {
        assertTrue(menuRepo.deleteById("F01"), "Xóa thành công");
        assertTrue(menuRepo.findById("F01").isEmpty(), "Không tìm thấy sau khi xóa");
    }

    @Test
    void testMenuUpdate() {
        boolean updated = menuRepo.update("F01", "Super Burger", 60000, true);
        assertTrue(updated);
        assertEquals("Super Burger", menuRepo.findById("F01").get().getName());
        assertEquals(60000, menuRepo.findById("F01").get().getPrice(), 0.01);
    }
}
