package model;

public class Drink extends MenuItem {

    private String size; // S, M, L

    public Drink(String id, String name, double price, boolean active, String size) {
        super(id, name, price, active);
        this.size = size;
    }

    @Override
    public double calculatePrice() {
        double basePrice = getPrice();

        switch (size.toUpperCase()) {
            case "M":
                return basePrice + 5000;
            case "L":
                return basePrice + 10000;
            default: // S
                return basePrice;
        }
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "[Đồ uống - Size " + size + "] " + super.toString();
    }
}
