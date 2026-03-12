package service;

import model.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticsService {

    private List<Order> orders;

    public StatisticsService(List<Order> orders) {
        this.orders = orders;
    }

    // Thống kê doanh thu theo ngày
    public double calculateRevenueByDate(LocalDate date) {

        return orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PAID)
                .filter(o -> o.getCreatedAt().toLocalDate().equals(date))
                .mapToDouble(Order::calculateTotal)
                .sum();
    }

    // Thống kê doanh thu theo tháng
    public double calculateRevenueByMonth(int year, int month) {

        YearMonth ym = YearMonth.of(year, month);

        return orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PAID)
                .filter(o -> YearMonth.from(o.getCreatedAt()).equals(ym))
                .mapToDouble(Order::calculateTotal)
                .sum();
    }

    // Danh sách món bán chạy
    public List<Map.Entry<String, Integer>> getTopSellingItems() {

        Map<String, Integer> itemCountMap = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PAID)
                .flatMap(o -> o.getItems().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getItem().getName(),
                        Collectors.summingInt(OrderItem::getQuantity)));

        return itemCountMap.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());
    }
}
