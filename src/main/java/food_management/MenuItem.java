package food_management;

public class MenuItem {
    private String id;
    public String name;
    public double price;
    public boolean status;
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
        return id + " | " + name + " | " + price + " | discount: " + discount;
    }


}
