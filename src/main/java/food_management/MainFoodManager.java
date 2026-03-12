package food_management;

import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Scanner;

public class MainFoodManager {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        MenuManager menu = new MenuManager();

        int choice;

        do {
            System.out.println("\n===== QUẢN LÝ ĐỒ ĂN ===== ");
            System.out.println("1. Thêm món");
            System.out.println("2. Cập nhật món");
            System.out.println("3. Xóa món");
            System.out.println("4. Tìm theo tên");
            System.out.println("5. Tìm theo khoảng giá");
            System.out.println("6. Hiển thị menu");
            System.out.println("0. Thoát");
            System.out.print("Chọn chức năng: ");

            choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1: // Thêm
                    System.out.println("Chọn loại: 1.Nước uống  2.Đồ ăn  3.Tráng miệng");
                    int type = Integer.parseInt(sc.nextLine());

                    String id;
                    while(true){
                        id = "MI0" + Math.round(Math.random() * 1000000);
                        boolean idExisted = false;
                        for(MenuItem m : menu.getManager()){
                            if(m.getId().equals(id)){
                               idExisted = true;
                            }
                        }
                        if(!idExisted){
                            break;
                        }
                    }

                    String name;
                    while (true){
                        System.out.print("Nhập tên: ");
                        name = sc.nextLine();
                        boolean nameExisted = false;
                        for (MenuItem menuItem : menu.getManager()){
                            if(menuItem.name.equals(name)){
                                nameExisted = true;
                            }
                        }
                        if(!nameExisted){
                            break;
                        }
                    }


                    System.out.print("Nhập giá: ");
                    double price = Double.parseDouble(sc.nextLine());

                    if (type == 1) {
                        menu.add(new Drink(id, name, price, true));
                    } else if (type == 2) {
                        menu.add(new Food(id, name, price, true));
                    } else if (type == 3) {
                        menu.add(new Dessert(id, name, price, true));
                    }

                    System.out.println(id);

                    System.out.println("Thêm món thành công!");
                    break;

                case 2: // Sửa
                    System.out.print("Nhập ID cần cập nhật: ");
                    String updateId = sc.nextLine();
                    Optional<MenuItem> item = menu.getManager().stream().filter((MenuItem it) -> it.getId().equals(updateId)).findFirst();

                    if(item.isEmpty()) {
                        System.out.println("Món cần cập nhật chưa tồn tại");
                    }
                    MenuItem target = menu.getItemById(updateId);
                    if(target == null){
                        System.out.println("Món cần cập nhật không tồn tại");
                        break;
                    }

                    System.out.println("Chọn thuộc tính cần cập nhật: " +
                            "\n1. Tên món " +
                            "\n2. Giá" +
                            "\n3. Trạng thái ");
                    int choiceUd = sc.nextInt();
                    sc.nextLine();
                    if (choiceUd == 1) {
                        // Cập nhật tên món
                        System.out.println(target.toString());
                        String newName;
                        while (true) {
                            System.out.print("Nhập tên: ");
                            newName = sc.nextLine();
                            boolean nameExisted = false;
                            for (MenuItem menuItem : menu.getManager()) {
                                if (menuItem.name.equals(newName)) {
                                    nameExisted = true;
                                }
                            }
                            if (!nameExisted && !newName.equals(target.name)) {
                                menu.update(target.getId(), newName, target.price, target.status);
                                break;
                            } else {
                                System.out.println("Tên món trùng rồi!");
                            }
                        }

                        break;
                    }
                    if(choiceUd == 2) {
                        // Cập nhật giá món
                        System.out.println(target.toString());
                        while (true) {
                            try {
                                System.out.println("Nhập giá sửa đổi: ");
                                double newPrice = sc.nextDouble();
                                sc.nextLine();
                                if (newPrice > 0) {
                                    menu.update(target.getId(), target.name, newPrice, target.status);
                                    break;
                                } else {
                                    System.out.println("Vui lòng nhập giá hợp lệ");
                                }
                            } catch (InputMismatchException e) {
                                System.out.println("Giá phải là số!");
                                sc.nextLine(); // bỏ dữ liệu sai
                            }
                        }
                        break;
                    }
                    if(choiceUd == 3){
                            System.out.println(target.toString());
                            System.out.println("Cập nhật trạng thái món: " +
                                    "1. Còn hàng" +
                                    "2. Hết hàng");
                            boolean udStatus = sc.nextInt() == 1;
                            menu.update(target.getId(), target.name, target.price, udStatus);
                            break;
                    }
                    break;

                case 3: // Xóa
                    System.out.print("Nhập ID cần xóa: ");
                    String deleteId = sc.nextLine();

                    menu.deleteById(deleteId);
                    break;

                case 4: // Tìm theo tên
                    System.out.print("Nhập tên cần tìm: ");
                    String searchName = sc.nextLine();

                    menu.findByName(searchName);
                    break;

                case 5: // Tìm theo khoảng giá
                    System.out.print("Nhập giá thấp nhất: ");
                    double min = Double.parseDouble(sc.nextLine());

                    System.out.print("Nhập giá cao nhất: ");
                    double max = Double.parseDouble(sc.nextLine());

                    menu.findByPriceRange(min, max);
                    break;

                case 6: // Hiển thị
                    System.out.println("===== DANH SÁCH MENU =====");
                    menu.display();
                    break;

                case 0:
                    System.out.println("Thoát chương trình!");
                    break;

                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }

        } while (choice != 0);
    }
}
