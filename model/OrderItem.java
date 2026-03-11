package model;

public class OrderItem {

    private MenuItem item;
    private int quantity;
    private double unitPrice;

    public OrderItem(MenuItem item, int quantity, double unitPrice) {
        this.item = item;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
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
}
