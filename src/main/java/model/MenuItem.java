package model;

import java.text.NumberFormat;

public class MenuItem {

    private String id;
    private String name;
    private double price;
    private boolean active;

    public MenuItem() {
    }

    public MenuItem(String id, String name, double price, boolean active) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.active = active;
    }

    public double calculatePrice() {
        return price;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public boolean isActive() {
        return active;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        NumberFormat nf = NumberFormat.getNumberInstance();
        return id + " | " + name + " | " + nf.format(calculatePrice()) + " VND | " + (active ? "Còn hàng" : "Hết hàng");
    }
}
