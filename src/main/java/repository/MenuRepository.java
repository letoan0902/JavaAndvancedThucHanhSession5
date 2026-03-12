package repository;

import model.MenuItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository quản lý danh sách món ăn (Menu)
 * Sử dụng model.MenuItem và các lớp con (Food, Drink, Dessert)
 */
public class MenuRepository {

    private List<MenuItem> menuItems;

    public MenuRepository() {
        this.menuItems = new ArrayList<>();
    }

    // Thêm món ăn
    public void add(MenuItem item) {
        menuItems.add(item);
    }

    // Cập nhật món ăn theo ID
    public boolean update(String id, String newName, double newPrice, boolean newActive) {
        Optional<MenuItem> found = findById(id);
        if (found.isPresent()) {
            MenuItem item = found.get();
            item.setName(newName);
            item.setPrice(newPrice);
            item.setActive(newActive);
            return true;
        }
        return false;
    }

    // Xóa món theo ID
    public boolean deleteById(String id) {
        return menuItems.removeIf(m -> m.getId().equalsIgnoreCase(id));
    }

    // Tìm món theo ID (trả về Optional - SRS yêu cầu)
    public Optional<MenuItem> findById(String id) {
        return menuItems.stream()
                .filter(m -> m.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    // Tìm món theo tên (tìm kiếm gần đúng)
    public List<MenuItem> findByName(String name) {
        return menuItems.stream()
                .filter(m -> m.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Tìm món theo khoảng giá
    public List<MenuItem> findByPriceRange(double min, double max) {
        return menuItems.stream()
                .filter(m -> m.calculatePrice() >= min && m.calculatePrice() <= max)
                .collect(Collectors.toList());
    }

    // Sắp xếp theo giá
    public void sort() {
        menuItems.sort(Comparator.comparingDouble(MenuItem::calculatePrice));
    }

    // Hiển thị tất cả menu
    public void display() {
        if (menuItems.isEmpty()) {
            System.out.println("Menu trống!");
            return;
        }
        menuItems.forEach(System.out::println);
    }

    // Lấy danh sách menu
    public List<MenuItem> getAll() {
        return menuItems;
    }

    // Kiểm tra tên đã tồn tại
    public boolean isNameExisted(String name) {
        return menuItems.stream()
                .anyMatch(m -> m.getName().equalsIgnoreCase(name));
    }

    // Kiểm tra ID đã tồn tại
    public boolean isIdExisted(String id) {
        return menuItems.stream()
                .anyMatch(m -> m.getId().equalsIgnoreCase(id));
    }
}
