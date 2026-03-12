package food_management;

public class Food extends MenuItem {

    public Food(String id, String name, double price, boolean status) {
        super(id, name, price, status);
    }

    @Override
    public double calculatePrice() {
        return price * 0.9;
    }
}