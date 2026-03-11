package test;

import model.Order;
import service.StatisticsService;

import java.time.LocalDate;
import java.util.List;

class StatisticsServiceTest {

    @Test
    void testRevenueByDate() {

        List<Order> orders = TestDataFactory.createOrders();

        StatisticsService service = new StatisticsService(orders);

        double revenue = service.calculateRevenueByDate(LocalDate.now());

        assertTrue(revenue > 0);
    }

    private void assertTrue(boolean b) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'assertTrue'");
    }

    @Test
    void testTopSellingItems() {

        List<Order> orders = TestDataFactory.createOrders();

        StatisticsService service = new StatisticsService(orders);

        var result = service.getTopSellingItems();

        assertTrue(result.isEmpty());
    }
}
