package model;

public class Food extends MenuItem {

    public Food(String id, String name, double price, boolean active) {
        super(id, name, price, active);
    }

    @Override
    public double calculatePrice() {
        return getPrice();
    }

    @Override
    public String toString() {
        return "[Món chính] " + super.toString();
    }
}
