package test;

import model.Order;
import service.StatisticsService;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

class StatisticsServiceTest {

    @Test
    void testRevenueByDate() {
        List<Order> orders = TestDataFactory.createOrders();
        StatisticsService service = new StatisticsService(orders);

        double revenue = service.calculateRevenueByDate(LocalDate.now());

        assertTrue(revenue > 0, "Doanh thu ngày hôm nay phải lớn hơn 0");
    }

    @Test
    void testRevenueByMonth() {
        List<Order> orders = TestDataFactory.createOrders();
        StatisticsService service = new StatisticsService(orders);

        LocalDate now = LocalDate.now();
        double revenue = service.calculateRevenueByMonth(now.getYear(), now.getMonthValue());

        assertTrue(revenue > 0, "Doanh thu tháng hiện tại phải lớn hơn 0");
    }

    @Test
    void testTotalRevenue() {
        List<Order> orders = TestDataFactory.createOrders();
        StatisticsService service = new StatisticsService(orders);

        double total = service.calculateTotalRevenue();

        // Burger 50000 x2 + Coca (M) 20000 x3 = 100000 + 60000 = 160000
        assertEquals(160000, total, 0.01, "Tổng doanh thu phải đúng");
    }

    @Test
    void testTopSellingItems() {
        List<Order> orders = TestDataFactory.createOrders();
        StatisticsService service = new StatisticsService(orders);

        List<Map.Entry<String, Integer>> result = service.getTopSellingItems();

        assertFalse(result.isEmpty(), "Danh sách món bán chạy không được rỗng");
        assertEquals("Coca", result.get(0).getKey(), "Coca phải là món bán chạy nhất (3 cái)");
        assertEquals(3, result.get(0).getValue(), "Coca bán được 3 cái");
    }

    @Test
    void testRevenueWithNoOrders() {
        List<Order> orders = List.of();
        StatisticsService service = new StatisticsService(orders);

        double revenue = service.calculateRevenueByDate(LocalDate.now());

        assertEquals(0, revenue, "Doanh thu phải bằng 0 khi không có đơn hàng");
    }
}
