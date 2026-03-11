package food_management;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MenuManager {

    private List<MenuItem> manager = new ArrayList<>();

    // add
    public void add(MenuItem item){
        manager.add(item);
    }

    // display
    public void display(){
        manager.forEach(System.out::println);
    }

    // find by name
    public void findByName(String name){
        manager.stream()
                .filter(m -> m.name.toLowerCase().contains(name.toLowerCase()))
                .forEach(System.out::println);
    }

    // find by price range
    public void findByPriceRange(double min, double max){
        manager.stream()
                .filter(m -> m.price >= min && m.price <= max)
                .forEach(System.out::println);
    }

    // delete by id
    public void deleteById(String id){
        manager.removeIf(m -> m.getId().equalsIgnoreCase(id));
    }

    // sort by price
    public void sort(){
        manager.sort(Comparator.comparing(MenuItem::calculatePrice));
    }


}