package food_management;

public class MainFoodManager {
    public static void main(String[] args) {

        MenuManager menu = new MenuManager();

        menu.add(new Drink("D01","Coca",15000,true));
        menu.add(new Food("F01","Fried Rice",50000,true));
        menu.add(new Dessert("DS01","Ice Cream",25000,true));

        System.out.println("=== MENU ===");
        menu.display();

        System.out.println("\nFind name 'Rice'");
        menu.findByName("Rice");

        System.out.println("\nFind price 20000 - 60000");
        menu.findByPriceRange(20000,60000);

        System.out.println("\nSort by price");
        menu.sort();
        menu.display();

        System.out.println("\nDelete F01");
        menu.deleteById("F01");

        menu.display();
    }
}
