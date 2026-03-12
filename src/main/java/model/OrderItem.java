package model;

public class OrderItem {

    private MenuItem item;
    private int quantity;
    private double unitPrice;

    public OrderItem(MenuItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
        this.unitPrice = item.calculatePrice(); // Tự động lấy giá từ calculatePrice() - đảm bảo Polymorphism
    }

    public double getTotalPrice() {
        return unitPrice * quantity;
    }

    public MenuItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return item.getName() + " x" + quantity + " = " + getTotalPrice() + " VND";
    }
}
