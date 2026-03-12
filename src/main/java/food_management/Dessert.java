package food_management;

public class Dessert extends MenuItem {

    public Dessert(String id, String name, double price, boolean status) {
        super(id, name, price, status);
    }

    @Override
    public double calculatePrice() {
        return price * 0.93;
    }
}
