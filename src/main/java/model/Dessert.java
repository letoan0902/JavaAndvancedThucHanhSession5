package model;

public class Dessert extends MenuItem {

    public Dessert(String id, String name, double price, boolean active) {
        super(id, name, price, active);
    }

    @Override
    public double calculatePrice() {
        return getPrice();
    }

    @Override
    public String toString() {
        return "[Tráng miệng] " + super.toString();
    }
}
