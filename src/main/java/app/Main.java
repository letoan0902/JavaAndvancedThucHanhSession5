package app;

import exception.InvalidOrderIdException;
import model.*;
import repository.MenuRepository;
import repository.OrderRepository;
import service.StatisticsService;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * Chương trình chính - Hệ thống Quản lý Cửa hàng Đồ ăn nhanh
 */
public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final MenuRepository menuRepo = new MenuRepository();
    private static final OrderRepository orderRepo = new OrderRepository();
    private static int menuIdCounter = 1;
    private static int orderIdCounter = 1;

    public static void main(String[] args) {
        // Thêm dữ liệu mẫu
        initSampleData();

        int choice;
        do {
            printMainMenu();
            choice = readInt("Chọn chức năng: ");

            switch (choice) {
                case 1:
                    menuManagement();
                    break;
                case 2:
                    orderManagement();
                    break;
                case 3:
                    statisticsManagement();
                    break;
                case 0:
                    System.out.println("Cảm ơn bạn đã sử dụng! Tạm biệt!");
                    break;
                default:
                    System.out.println("  Lựa chọn không hợp lệ!");
            }
        } while (choice != 0);
    }

    // ==================== MAIN MENU ====================
    private static void printMainMenu() {
        System.out.println("\n");
        System.out.println("   HỆ THỐNG QUẢN LÝ CỬA HÀNG ĐỒ ĂN    ");
        System.out.println("");
        System.out.println("1. Quản lý đồ ăn (Menu) ");
        System.out.println("2. Quản lý đơn hàng (Order)        ");
        System.out.println("3. Thống kê (Statistics)                ");
        System.out.println("0. Thoát                                ");
        System.out.println("");
    }

    // ==================== 1. QUẢN LÝ ĐỒ ĂN ====================
    private static void menuManagement() {
        int choice;
        do {
            System.out.println("\n===== QUẢN LÝ ĐỒ ĂN =====");
            System.out.println("1. Thêm món");
            System.out.println("2. Cập nhật món");
            System.out.println("3. Xóa món");
            System.out.println("4. Tìm theo tên");
            System.out.println("5. Tìm theo khoảng giá");
            System.out.println("6. Hiển thị menu");
            System.out.println("7. Sắp xếp menu theo giá");
            System.out.println("0. Quay lại");
            choice = readInt("Chọn chức năng: ");

            switch (choice) {
                case 1:
                    addMenuItem();
                    break;
                case 2:
                    updateMenuItem();
                    break;
                case 3:
                    deleteMenuItem();
                    break;
                case 4:
                    findByName();
                    break;
                case 5:
                    findByPriceRange();
                    break;
                case 6:
                    System.out.println("\n===== DANH SÁCH MENU =====");
                    menuRepo.display();
                    break;
                case 7:
                    menuRepo.sort();
                    System.out.println("  Đã sắp xếp menu theo giá!");
                    menuRepo.display();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("  Lựa chọn không hợp lệ!");
            }
        } while (choice != 0);
    }

    private static void addMenuItem() {
        System.out.println("Chọn loại: 1. Món chính  2. Đồ uống  3. Tráng miệng");
        int type = readInt("Loại: ");

        String id = generateMenuId();

        String name;
        while (true) {
            System.out.print("Nhập tên món: ");
            name = sc.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("  Tên không được để trống!");
                continue;
            }
            if (menuRepo.isNameExisted(name)) {
                System.out.println("  Tên món đã tồn tại! Vui lòng nhập tên khác.");
                continue;
            }
            break;
        }

        double price = readDouble("Nhập giá: ");
        if (price <= 0) {
            System.out.println("  Giá phải lớn hơn 0!");
            return;
        }

        switch (type) {
            case 1:
                menuRepo.add(new Food(id, name, price, true));
                break;
            case 2:
                System.out.print("Nhập size (S/M/L): ");
                String size = sc.nextLine().trim().toUpperCase();
                if (!size.equals("S") && !size.equals("M") && !size.equals("L")) {
                    System.out.println("  Size không hợp lệ! Mặc định S.");
                    size = "S";
                }
                menuRepo.add(new Drink(id, name, price, true, size));
                break;
            case 3:
                menuRepo.add(new Dessert(id, name, price, true));
                break;
            default:
                System.out.println("  Loại không hợp lệ!");
                return;
        }

        System.out.println("  Thêm món thành công! ID: " + id);
    }

    private static void updateMenuItem() {
        System.out.print("Nhập ID cần cập nhật: ");
        String updateId = sc.nextLine().trim();

        Optional<MenuItem> optItem = menuRepo.findById(updateId);
        if (optItem.isEmpty()) {
            System.out.println("  Không tìm thấy món có ID: " + updateId);
            return;
        }

        MenuItem target = optItem.get();
        System.out.println("Món hiện tại: " + target);

        System.out.println("Chọn thuộc tính cần cập nhật:");
        System.out.println("1. Tên món");
        System.out.println("2. Giá");
        System.out.println("3. Trạng thái (Còn hàng / Hết hàng)");
        int choiceUd = readInt("Chọn: ");

        try {
            switch (choiceUd) {
                case 1:
                    String newName;
                    while (true) {
                        System.out.print("Nhập tên mới: ");
                        newName = sc.nextLine().trim();
                        if (newName.isEmpty()) {
                            System.out.println("  Tên không được để trống!");
                            continue;
                        }
                        if (menuRepo.isNameExisted(newName) && !newName.equalsIgnoreCase(target.getName())) {
                            System.out.println("  Tên món đã tồn tại!");
                            continue;
                        }
                        break;
                    }
                    menuRepo.update(target.getId(), newName, target.getPrice(), target.isActive());
                    System.out.println("  Cập nhật tên thành công!");
                    break;

                case 2:
                    double newPrice = readDouble("Nhập giá mới: ");
                    if (newPrice <= 0) {
                        System.out.println("  Giá phải lớn hơn 0!");
                        return;
                    }
                    menuRepo.update(target.getId(), target.getName(), newPrice, target.isActive());
                    System.out.println("  Cập nhật giá thành công!");
                    break;

                case 3:
                    System.out.println("1. Còn hàng  2. Hết hàng");
                    int statusChoice = readInt("Chọn: ");
                    boolean newActive = statusChoice == 1;
                    menuRepo.update(target.getId(), target.getName(), target.getPrice(), newActive);
                    System.out.println("  Cập nhật trạng thái thành công!");
                    break;

                default:
                    System.out.println("  Lựa chọn không hợp lệ!");
            }
        } catch (Exception e) {
            System.out.println("  Lỗi khi cập nhật: " + e.getMessage());
        }
    }

    private static void deleteMenuItem() {
        System.out.print("Nhập ID cần xóa: ");
        String deleteId = sc.nextLine().trim();

        if (menuRepo.deleteById(deleteId)) {
            System.out.println("  Xóa thành công!");
        } else {
            System.out.println("  Không tìm thấy món có ID: " + deleteId);
        }
    }

    private static void findByName() {
        System.out.print("Nhập tên cần tìm: ");
        String searchName = sc.nextLine().trim();

        List<MenuItem> results = menuRepo.findByName(searchName);
        if (results.isEmpty()) {
            System.out.println("Không tìm thấy món nào có tên chứa: " + searchName);
        } else {
            System.out.println("Kết quả tìm kiếm:");
            results.forEach(System.out::println);
        }
    }

    private static void findByPriceRange() {
        double min = readDouble("Nhập giá thấp nhất: ");
        double max = readDouble("Nhập giá cao nhất: ");

        if (min > max) {
            System.out.println("  Giá thấp nhất phải nhỏ hơn hoặc bằng giá cao nhất!");
            return;
        }

        List<MenuItem> results = menuRepo.findByPriceRange(min, max);
        if (results.isEmpty()) {
            System.out.println("Không tìm thấy món nào trong khoảng giá " + min + " - " + max);
        } else {
            System.out.println("Kết quả tìm kiếm:");
            results.forEach(System.out::println);
        }
    }

    // ==================== 2. QUẢN LÝ ĐƠN HÀNG ====================
    private static void orderManagement() {
        int choice;
        do {
            System.out.println("\n===== QUẢN LÝ ĐƠN HÀNG =====");
            System.out.println("1. Tạo đơn hàng mới");
            System.out.println("2. Cập nhật trạng thái đơn hàng");
            System.out.println("3. Hiển thị tất cả đơn hàng");
            System.out.println("0. Quay lại");
            choice = readInt("Chọn chức năng: ");

            switch (choice) {
                case 1:
                    createOrder();
                    break;
                case 2:
                    updateOrderStatus();
                    break;
                case 3:
                    orderRepo.display();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("  Lựa chọn không hợp lệ!");
            }
        } while (choice != 0);
    }

    private static void createOrder() {
        String orderId = generateOrderId();
        Order order = new Order(orderId);
        System.out.println("  Tạo đơn hàng mới: " + orderId);

        int choice;
        do {
            System.out.println("\n--- Đơn hàng: " + orderId + " ---");
            System.out.println("1. Thêm món vào đơn");
            System.out.println("2. Xóa món khỏi đơn");
            System.out.println("3. Áp dụng mã giảm giá");
            System.out.println("4. Áp dụng phí dịch vụ");
            System.out.println("5. Xem đơn hàng hiện tại");
            System.out.println("6. Xác nhận đơn hàng");
            System.out.println("0. Hủy đơn hàng");
            choice = readInt("Chọn: ");

            switch (choice) {
                case 1:
                    addItemToOrder(order);
                    break;
                case 2:
                    removeItemFromOrder(order);
                    break;
                case 3:
                    double discount = readDouble("Nhập % giảm giá: ");
                    if (discount < 0 || discount > 100) {
                        System.out.println("  Phần trăm giảm giá phải từ 0 đến 100!");
                    } else {
                        order.applyDiscount(discount);
                        System.out.println("  Áp dụng giảm giá " + discount + "%");
                    }
                    break;
                case 4:
                    double fee = readDouble("Nhập % phí dịch vụ: ");
                    if (fee < 0) {
                        System.out.println("  Phí dịch vụ không được âm!");
                    } else {
                        order.applyServiceFee(fee);
                        System.out.println("  Áp dụng phí dịch vụ " + fee + "%");
                    }
                    break;
                case 5:
                    order.displayOrder();
                    break;
                case 6:
                    if (order.getItems().isEmpty()) {
                        System.out.println("  Đơn hàng trống! Vui lòng thêm món.");
                    } else {
                        orderRepo.add(order);
                        System.out.println("  Đơn hàng đã được lưu!");
                        order.displayOrder();
                        return;
                    }
                    break;
                case 0:
                    order.updateStatus(OrderStatus.CANCELLED);
                    System.out.println("  Đơn hàng đã bị hủy!");
                    return;
                default:
                    System.out.println("  Lựa chọn không hợp lệ!");
            }
        } while (true);
    }

    private static void addItemToOrder(Order order) {
        System.out.println("\n--- Menu hiện tại ---");
        menuRepo.display();

        System.out.print("Nhập ID món cần thêm: ");
        String itemId = sc.nextLine().trim();

        Optional<MenuItem> optItem = menuRepo.findById(itemId);
        if (optItem.isEmpty()) {
            System.out.println("  Không tìm thấy món có ID: " + itemId);
            return;
        }

        MenuItem item = optItem.get();
        if (!item.isActive()) {
            System.out.println("  Món '" + item.getName() + "' hiện đã hết hàng!");
            return;
        }

        int quantity = readInt("Nhập số lượng: ");
        if (quantity <= 0) {
            System.out.println("  Số lượng phải lớn hơn 0!");
            return;
        }

        OrderItem orderItem = new OrderItem(item, quantity);
        order.addItem(orderItem);
        System.out.println("  Đã thêm: " + item.getName() + " x" + quantity
                + " = " + NumberFormat.getNumberInstance().format(orderItem.getTotalPrice()) + " VND");
    }

    private static void removeItemFromOrder(Order order) {
        if (order.getItems().isEmpty()) {
            System.out.println("Đơn hàng trống!");
            return;
        }

        System.out.println("Các món trong đơn:");
        for (int i = 0; i < order.getItems().size(); i++) {
            System.out.println((i + 1) + ". " + order.getItems().get(i));
        }

        System.out.print("Nhập tên món cần xóa: ");
        String itemName = sc.nextLine().trim();
        order.removeItem(itemName);
        System.out.println("  Đã xóa món: " + itemName);
    }

    private static void updateOrderStatus() {
        System.out.println("\n--- Danh sách đơn hàng ---");
        orderRepo.display();

        System.out.print("Nhập ID đơn hàng: ");
        String orderId = sc.nextLine().trim();

        try {
            Optional<Order> optOrder = orderRepo.findById(orderId);
            if (optOrder.isEmpty()) {
                throw new InvalidOrderIdException(orderId, "Không tìm thấy đơn hàng");
            }

            Order order = optOrder.get();
            System.out.println("Trạng thái hiện tại: " + order.getStatus());
            System.out.println("1. Đã thanh toán (PAID)");
            System.out.println("2. Hủy đơn hàng (CANCELLED)");
            int statusChoice = readInt("Chọn: ");

            switch (statusChoice) {
                case 1:
                    orderRepo.updateStatus(orderId, OrderStatus.PAID);
                    System.out.println("  Đơn hàng " + orderId + " đã thanh toán!");
                    break;
                case 2:
                    orderRepo.updateStatus(orderId, OrderStatus.CANCELLED);
                    System.out.println("  Đơn hàng " + orderId + " đã hủy!");
                    break;
                default:
                    System.out.println("  Lựa chọn không hợp lệ!");
            }
        } catch (InvalidOrderIdException e) {
            System.out.println("  " + e.getMessage());
        }
    }

    // ==================== 3. THỐNG KÊ ====================
    private static void statisticsManagement() {
        StatisticsService statsService = new StatisticsService(orderRepo.getAll());

        int choice;
        do {
            System.out.println("\n===== THỐNG KÊ =====");
            System.out.println("1. Doanh thu theo ngày");
            System.out.println("2. Doanh thu theo tháng");
            System.out.println("3. Tổng doanh thu");
            System.out.println("4. Danh sách món bán chạy");
            System.out.println("0. Quay lại");
            choice = readInt("Chọn chức năng: ");

            NumberFormat nf = NumberFormat.getNumberInstance();

            switch (choice) {
                case 1:
                    System.out.print("Nhập ngày (dd/MM/yyyy) hoặc Enter để lấy hôm nay: ");
                    String dateStr = sc.nextLine().trim();
                    LocalDate date;
                    if (dateStr.isEmpty()) {
                        date = LocalDate.now();
                    } else {
                        try {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                            date = LocalDate.parse(dateStr, dtf);
                        } catch (Exception e) {
                            System.out.println("  Định dạng ngày không hợp lệ!");
                            break;
                        }
                    }
                    double revenueByDate = statsService.calculateRevenueByDate(date);
                    System.out.println("  Doanh thu ngày " + date + ": " + nf.format(revenueByDate) + " VND");
                    break;

                case 2:
                    int year = readInt("Nhập năm: ");
                    int month = readInt("Nhập tháng: ");
                    if (month < 1 || month > 12) {
                        System.out.println("  Tháng không hợp lệ!");
                        break;
                    }
                    double revenueByMonth = statsService.calculateRevenueByMonth(year, month);
                    System.out.println("  Doanh thu tháng " + month + "/" + year + ": " + nf.format(revenueByMonth) + " VND");
                    break;

                case 3:
                    double totalRevenue = statsService.calculateTotalRevenue();
                    System.out.println("  Tổng doanh thu: " + nf.format(totalRevenue) + " VND");
                    break;

                case 4:
                    List<Map.Entry<String, Integer>> topItems = statsService.getTopSellingItems();
                    if (topItems.isEmpty()) {
                        System.out.println("Chưa có dữ liệu bán hàng!");
                    } else {
                        System.out.println("  Danh sách món bán chạy:");
                        int rank = 1;
                        for (Map.Entry<String, Integer> entry : topItems) {
                            System.out.println("  #" + rank + ". " + entry.getKey() + " - Số lượng: " + entry.getValue());
                            rank++;
                        }
                    }
                    break;

                case 0:
                    break;
                default:
                    System.out.println("  Lựa chọn không hợp lệ!");
            }
        } while (choice != 0);
    }

    // ==================== HELPER METHODS ====================
    private static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = sc.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("  Vui lòng nhập số nguyên hợp lệ!");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = sc.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("  Vui lòng nhập số hợp lệ!");
            }
        }
    }

    private static String generateMenuId() {
        String id;
        do {
            id = "MI" + String.format("%03d", menuIdCounter++);
        } while (menuRepo.isIdExisted(id));
        return id;
    }

    private static String generateOrderId() {
        String id;
        do {
            id = "OD" + String.format("%03d", orderIdCounter++);
        } while (orderRepo.isIdExisted(id));
        return id;
    }

    // Dữ liệu mẫu
    private static void initSampleData() {
        menuRepo.add(new Food("MI001", "Burger Bò", 55000, true));
        menuRepo.add(new Food("MI002", "Gà rán", 45000, true));
        menuRepo.add(new Food("MI003", "Pizza", 89000, true));
        menuRepo.add(new Drink("MI004", "Coca Cola", 15000, true, "S"));
        menuRepo.add(new Drink("MI005", "Trà sữa", 25000, true, "M"));
        menuRepo.add(new Drink("MI006", "Cà phê", 20000, true, "L"));
        menuRepo.add(new Dessert("MI007", "Kem", 20000, true));
        menuRepo.add(new Dessert("MI008", "Bánh flan", 18000, true));
        menuIdCounter = 9;
    }
}
