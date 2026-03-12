package food_management;

import java.text.NumberFormat;

public class MenuItem {
    private String id;
    public String name;
    public double price;
    public boolean status; // true: còn\ false: hết
    public int discount;

    public MenuItem() {
    }

    public MenuItem(String id, String name, double price, boolean status) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    double calculatePrice(){
        return this.price + this.price*((double) this.discount /100);
    }



    @Override
    public String toString() {
        NumberFormat nf = NumberFormat.getNumberInstance();
        return id + " | " + name + " | " + nf.format(price) + "VND | discount: " + discount;
    }


}
