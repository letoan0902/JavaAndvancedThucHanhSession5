package food_management;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MenuManager {

    private List<MenuItem> manager;

    public List<MenuItem> getManager() {
        return manager;
    }

    public void setManager(List<MenuItem> manager) {
        this.manager = manager;
    }

    public MenuManager() {
        this.manager = new ArrayList<>();
    }

    // add
    public void add(MenuItem item){
        manager.add(item);
    }

    // display
    public void display(){
        manager.forEach(System.out::println);
    }


    // update
    public void update(String idUpdate, String udName, double udPrice, boolean udStatus){
       for (MenuItem menuItem : manager){
           if(menuItem.getId().equals(idUpdate)){
               menuItem.name = udName;
               menuItem.price = udPrice;
               menuItem.status = udStatus;
           }
       }
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

    // getItemById
    public MenuItem getItemById(String id){
        for (MenuItem item : manager){
            if(item.getId().equals(id)){
                return item;
            }
        }
        return null;
    }

    // sort by price
    public void sort(){
        manager.sort(Comparator.comparing(MenuItem::calculatePrice));
    }


}