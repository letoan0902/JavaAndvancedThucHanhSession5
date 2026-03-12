# 📋 BÁO CÁO ĐÁNH GIÁ CODE - DỰ ÁN QUẢN LÝ CỬA HÀNG ĐỒ ĂN NHANH

> **Ngày đánh giá:** 12/03/2026  
> **Người đánh giá:** Nhóm trưởng (trước khi merge)  
> **Trạng thái:** ⚠️ CẦN SỬA NHIỀU VẤN ĐỀ TRƯỚC KHI MERGE

---

## 📌 TỔNG QUAN

Dự án hiện có **3 phần chính** do 3 người/nhóm làm:
1. **Quản lý đồ ăn** (Phạm Phương Anh) → package `food_management`
2. **Quản lý đơn hàng** (Lê Tiến Đức, Trần Đăng Việt) → package `repository.legacy` + package `model`
3. **Thống kê** (Bùi Minh Vũ) → package `service`

---

## 🔴 VẤN ĐỀ NGHIÊM TRỌNG (CRITICAL)

### 1. TRÙNG LẶP LỚP NGHIÊM TRỌNG - Không thể merge được

| Lớp | `food_management` (Phương Anh) | `model` (Đức & Việt) | `repository.legacy` (Đức & Việt) |
|---|---|---|---|
| `MenuItem` | ✅ có | ✅ có | ❌ |
| `Food` | ✅ có | ✅ có | ❌ |
| `Drink` | ✅ có | ✅ có | ❌ |
| `Dessert` | ✅ có | ✅ có | ❌ |
| `Order` | ❌ | ✅ có | ✅ có |
| `OrderItem` | ❌ | ✅ có | ✅ có |
| `OrderStatus` | ❌ | ✅ có | ✅ có |

**🔥 Vấn đề:**
- **`MenuItem`, `Food`, `Drink`, `Dessert`** tồn tại ở **2 package khác nhau** (`food_management` và `model`) với thiết kế **KHÁC NHAU HOÀN TOÀN**.
- **`Order`, `OrderItem`, `OrderStatus`** tồn tại ở **2 package khác nhau** (`model` và `repository.legacy`) với thiết kế **KHÁC NHAU HOÀN TOÀN**.
- ➡️ Khi merge, các module **KHÔNG THỂ LIÊN KẾT** với nhau vì dùng class khác nhau.

### 2. Package `food_management` KHÔNG LIÊN KẾT với `model`

- `food_management.MenuItem` và `model.MenuItem` là **2 class hoàn toàn khác nhau**.
- `StatisticsService` sử dụng `model.Order` và `model.OrderItem` → nhưng `food_management` lại dùng `food_management.MenuItem`.
- ➡️ **Khi tạo đơn hàng, không thể dùng chung MenuItem giữa 2 module.**

### 3. Package `repository.legacy` KHÔNG LIÊN KẾT với `model`

- `repository.legacy.Order` dùng `addItem(String name, double price, int quantity)` → chỉ lưu tên + giá, **KHÔNG tham chiếu đến MenuItem**.
- `model.Order` dùng `addItem(OrderItem item)` → sử dụng đối tượng `OrderItem` chứa `MenuItem`.
- ➡️ **Có 2 hệ thống Order song song, không tương thích.**

---

## 🟠 VẤN ĐỀ VỀ THIẾT KẾ (DESIGN ISSUES)

### 4. Vi phạm Encapsulation trong `food_management.MenuItem`

```
❌ public String name;      → nên là private
❌ public double price;     → nên là private
❌ public boolean status;   → nên là private
❌ public int discount;     → nên là private
```

SRS yêu cầu rõ ràng: *"Encapsulation: Các thuộc tính phải để private, truy cập qua getter/setter"*

- `food_management.MenuItem` để `name`, `price`, `status`, `discount` là **public** → **VI PHẠM SRS**.
- `model.MenuItem` tuân thủ đúng (private + getter).
- `MenuManager` truy cập trực tiếp `menuItem.name`, `menuItem.price` thay vì dùng getter → **VI PHẠM ENCAPSULATION**.

### 5. `food_management.MenuItem` thiếu setter cho `name`, `price`, `status`

- Chỉ có `setId()` nhưng không có setter cho `name`, `price`, `status`.
- `MenuManager.update()` truy cập trực tiếp field public → không chuẩn OOP.

### 6. `food_management.Drink` KHÔNG override `calculatePrice()`

SRS yêu cầu: *"Polymorphism: Override phương thức calculatePrice() cho từng loại món (ví dụ: Drink có thêm tiền size L / M / S)"*

- `model.Drink` ✅ có override `calculatePrice()` với logic tính giá theo size S/M/L.
- `food_management.Drink` ❌ **KHÔNG override `calculatePrice()`**, cũng **KHÔNG có thuộc tính size**.

### 7. `food_management.Food.calculatePrice()` logic không rõ ràng

```java
return price * 0.9; // Giảm 10%? Tại sao?
```

- Không có giải thích tại sao Food luôn giảm 10%.
- `food_management.Dessert.calculatePrice()` trả về `price * 0.93` → Giảm 7%? Cũng không có giải thích.
- `food_management.MenuItem.calculatePrice()` trả về `price + price * (discount/100)` → đây là **TĂNG giá** theo discount, logic ngược (discount nên là giảm giá).

### 8. `model.Order` thiếu setter cho `discountPercent`

```java
private double discountPercent;  // Không có setter!
```

- Không có phương thức `setDiscountPercent()` hoặc `applyDiscount()`.
- ➡️ **KHÔNG THỂ áp dụng mã giảm giá** cho đơn hàng trong package `model`.

### 9. `model.OrderItem` sử dụng `unitPrice` thay vì `calculatePrice()`

```java
public OrderItem(MenuItem item, int quantity, double unitPrice) {
    this.unitPrice = unitPrice;  // Truyền giá thủ công
}
```

- Nên dùng `item.calculatePrice()` để tự động lấy giá → đảm bảo tính đa hình (Polymorphism).
- Hiện tại `TestDataFactory` truyền `burger.getPrice()` thay vì `burger.calculatePrice()`, dẫn đến Drink size M/L sẽ tính sai giá.

---

## 🟡 VẤN ĐỀ VỀ EXCEPTION HANDLING

### 10. THIẾU Custom Exceptions

SRS yêu cầu:
- `InsufficientStockException` → ❌ **CHƯA CÓ**
- `InvalidOrderIdException` → ❌ **CHƯA CÓ**
- Sử dụng `try-catch-finally` → chỉ có 1 chỗ dùng trong `MainFoodManager` (case 2 - cập nhật giá).

### 11. Không có Exception package

- SRS yêu cầu cấu trúc: *"Model, Service, Repository, Exception"*
- ❌ **THIẾU package `exception`** hoàn toàn.

### 12. `MainFoodManager` thiếu xử lý lỗi đầu vào

- `Integer.parseInt(sc.nextLine())` ở nhiều chỗ sẽ **crash** nếu nhập sai (NumberFormatException).
- `Double.parseDouble(sc.nextLine())` cũng tương tự.
- Chỉ có case 2 (cập nhật giá) có try-catch, các chỗ khác không có.

---

## 🟡 VẤN ĐỀ VỀ UNIT TEST

### 13. `StatisticsServiceTest` dùng annotation `@Test` tự định nghĩa

```java
// File: test/Test.java
package test;
public @interface Test { }  // ← Tự tạo annotation @Test !!!
```

- **KHÔNG sử dụng JUnit 5** (`org.junit.jupiter.api.Test`).
- `assertTrue()` được tự viết và **throw UnsupportedOperationException** → test sẽ **LUÔN FAIL**.

### 14. `StatisticsServiceTest.testTopSellingItems()` logic test SAI

```java
assertTrue(result.isEmpty());  // ← Kiểm tra list RỖNG?
```

- `TestDataFactory.createOrders()` tạo order với status PAID và có items.
- `getTopSellingItems()` sẽ trả về list KHÔNG rỗng.
- ➡️ Test này **logic sai** (nếu assertTrue hoạt động, test sẽ FAIL).

### 15. `OrderTest` chỉ test `repository.legacy.Order`

- Không test `model.Order`.
- Không test các trường hợp lỗi (nhập sai dữ liệu).
- SRS yêu cầu: *"Test các trường hợp nhập sai dữ liệu để đảm bảo Exception hoạt động đúng"* → ❌ THIẾU.

### 16. THIẾU Unit Test cho Quản lý đồ ăn

- Không có test cho `MenuManager` (thêm, sửa, xóa, tìm kiếm).
- SRS yêu cầu: *"Viết Unit Test cho ít nhất 3 chức năng cốt lõi (Tính tiền, Thêm món, Thống kê)"*.

---

## 🟡 VẤN ĐỀ VỀ BUILD

### 17. `build.gradle` dependency sai

```groovy
testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.3")
```

- **JUnit Jupiter 6.0.3 KHÔNG TỒN TẠI** (phiên bản hiện tại là 5.x).
- Đã khai báo trùng: vừa dùng BOM `5.10.0` vừa thêm `6.0.3`.
- ➡️ Build có thể fail hoặc sử dụng sai version.

---

## 🔵 VẤN ĐỀ NHỎ (MINOR)

### 18. `OrderRepository` thiếu method `update()` và `sum()`

Theo thiết kế nhóm đã thống nhất, `OrderRepository` cần có:
- `add()` ✅ có
- `findById()` ✅ có
- `update()` ❌ **THIẾU**
- `sum()` ❌ **THIẾU**

### 19. `MenuManager` thiếu method `findById()`

- Có `getItemById()` nhưng trả về `null` thay vì `Optional<MenuItem>`.
- SRS yêu cầu sử dụng `Optional` để tránh NullPointerException.

### 20. `model.Order` thiếu `displayOrder()` hoặc `toString()`

- `repository.legacy.Order` có `displayOrder()`.
- `model.Order` **không có** phương thức hiển thị.

### 21. ID sinh tự động không chuẩn

```java
id = "MI0" + Math.round(Math.random() * 1000000);
```

- Dùng `Math.random()` có thể trùng lặp.
- Nên dùng `UUID` hoặc counter tăng dần.

### 22. `food_management.MenuItem` có thuộc tính `discount` nhưng không dùng trong constructor

- Constructor không nhận `discount` → mặc định là `0`.
- `calculatePrice()` tính `price + price * (discount/100)` → logic tăng giá, không phải giảm giá.

---

## 📊 BẢNG TỔNG HỢP THEO NGƯỜI LÀM

### Phạm Phương Anh (Quản lý đồ ăn - `food_management`)

| STT | Vấn đề | Mức độ |
|-----|--------|--------|
| 1 | Thuộc tính public vi phạm Encapsulation | 🔴 Critical |
| 2 | `Drink` không override `calculatePrice()`, thiếu size | 🔴 Critical |
| 3 | `Food.calculatePrice()` giảm 10% không rõ lý do | 🟠 Medium |
| 4 | `Dessert.calculatePrice()` giảm 7% không rõ lý do | 🟠 Medium |
| 5 | `MenuItem.calculatePrice()` logic tăng giá thay vì giảm | 🟠 Medium |
| 6 | Thiếu setter cho `name`, `price`, `status` | 🟠 Medium |
| 7 | Thiếu xử lý exception đầu vào (try-catch) | 🟡 Low |
| 8 | Thiếu Unit Test | 🟡 Low |
| 9 | ID sinh ngẫu nhiên có thể trùng | 🟡 Low |
| 10 | Dùng class khác với `model` → không thể liên kết | 🔴 Critical |

### Lê Tiến Đức & Trần Đăng Việt (Quản lý đơn hàng)

| STT | Vấn đề | Mức độ |
|-----|--------|--------|
| 1 | Tồn tại 2 hệ thống Order song song (`model` vs `repository.legacy`) | 🔴 Critical |
| 2 | `model.Order` thiếu `setDiscountPercent()` / `applyDiscount()` | 🔴 Critical |
| 3 | `model.OrderItem` dùng `unitPrice` thủ công thay vì `calculatePrice()` | 🟠 Medium |
| 4 | `OrderRepository` thiếu `update()` và `sum()` | 🟠 Medium |
| 5 | `model.Order` thiếu `displayOrder()` / `toString()` | 🟡 Low |
| 6 | `repository.legacy.Order` không tham chiếu `MenuItem` | 🟠 Medium |
| 7 | Thiếu Custom Exceptions (`InvalidOrderIdException`, etc.) | 🟠 Medium |

### Bùi Minh Vũ (Thống kê - `service`)

| STT | Vấn đề | Mức độ |
|-----|--------|--------|
| 1 | `StatisticsServiceTest` dùng `@Test` tự tạo, không dùng JUnit 5 | 🔴 Critical |
| 2 | `assertTrue()` tự viết, throw UnsupportedOperationException | 🔴 Critical |
| 3 | `testTopSellingItems()` logic test sai (assert isEmpty trên list có data) | 🔴 Critical |
| 4 | `StatisticsService` code logic đúng, sử dụng Stream API tốt | ✅ OK |
| 5 | `TestDataFactory` tạo data đúng cấu trúc | ✅ OK |

---

## ✅ ĐIỂM TỐT

1. **`model` package** được thiết kế khá tốt: đúng OOP, dùng `private` + getter, có inheritance.
2. **`StatisticsService`** sử dụng Stream API, Lambda, Collectors rất tốt.
3. **`repository.legacy.OrderTest`** viết test rõ ràng, đúng JUnit 5.
4. **`model.Drink`** có override `calculatePrice()` với logic size S/M/L đúng SRS.
5. **`MenuManager`** có đầy đủ chức năng CRUD + tìm kiếm + sort.
6. **Enum `OrderStatus`** đúng 3 trạng thái: PENDING, PAID, CANCELLED.

---

## 🛠️ ĐỀ XUẤT HÀNH ĐỘNG (ACTION ITEMS)

### Ưu tiên 1 - Phải sửa trước khi merge:

| # | Hành động | Người chịu trách nhiệm |
|---|-----------|------------------------|
| 1 | **Xóa toàn bộ package `food_management`**, chuyển logic sang dùng `model.MenuItem/Food/Drink/Dessert` | Phương Anh + Nhóm trưởng |
| 2 | **Xóa package `repository.legacy`** (hoặc giữ lại chỉ `OrderRepository` nhưng sửa để dùng `model.Order`) | Đức & Việt + Nhóm trưởng |
| 3 | Thêm `applyDiscount(double percent)` hoặc `setDiscountPercent()` vào `model.Order` | Đức & Việt |
| 4 | Sửa `StatisticsServiceTest` dùng đúng `org.junit.jupiter.api.Test` + xóa file `test/Test.java` | Minh Vũ |
| 5 | Sửa `build.gradle`: xóa dòng `junit-jupiter-api:6.0.3` (version không tồn tại) | Nhóm trưởng |

### Ưu tiên 2 - Sửa trước khi nộp:

| # | Hành động | Người chịu trách nhiệm |
|---|-----------|------------------------|
| 6 | Tạo package `exception` với `InsufficientStockException`, `InvalidOrderIdException` | Tất cả |
| 7 | Thêm setter cho `model.MenuItem` (hoặc tạo `MenuRepository` riêng) | Phương Anh |
| 8 | Sửa `OrderItem` dùng `item.calculatePrice()` thay vì `unitPrice` truyền tay | Đức & Việt |
| 9 | Viết Unit Test cho `MenuManager` | Phương Anh |
| 10 | Sửa logic test `testTopSellingItems()` → `assertFalse(result.isEmpty())` | Minh Vũ |
| 11 | Thêm `update()`, `sum()` vào `OrderRepository` | Đức & Việt |
| 12 | Thêm `toString()` hoặc `displayOrder()` cho `model.Order` | Đức & Việt |

### Ưu tiên 3 - Nice to have:

| # | Hành động |
|---|-----------|
| 13 | Dùng UUID hoặc auto-increment cho ID |
| 14 | Thêm try-catch cho tất cả input người dùng |
| 15 | Sử dụng `Optional` cho `findById()` ở `MenuManager` |
| 16 | Thêm phí dịch vụ (service fee) theo SRS |

---

## 📈 ĐIỂM ĐÁNH GIÁ THEO SRS

| Yêu cầu SRS | Trạng thái | Ghi chú |
|---|---|---|
| Encapsulation (private + getter/setter) | ⚠️ Một phần | `model` OK, `food_management` vi phạm |
| Inheritance (MenuItem → Food, Drink, Dessert) | ✅ Có | Nhưng bị trùng lặp 2 bộ |
| Polymorphism (override calculatePrice) | ⚠️ Một phần | `model.Drink` OK, `food_management.Drink` thiếu |
| Collections Framework (List, Map) | ✅ Có | Dùng List<MenuItem>, List<OrderItem> |
| Stream API (filter, reduce, sum) | ✅ Có | StatisticsService, MenuManager, Order |
| Optional | ⚠️ Một phần | OrderRepository có, MenuManager không |
| Custom Exceptions | ❌ Thiếu | Chưa tạo InsufficientStockException, InvalidOrderIdException |
| try-catch-finally | ⚠️ Một phần | Chỉ có 1 chỗ trong MainFoodManager |
| Unit Test (JUnit 5) | ⚠️ Một phần | OrderTest OK, StatisticsServiceTest sai, MenuManager thiếu |
| Test ít nhất 3 chức năng | ❌ Thiếu | Chỉ test được Order (legacy), 2 cái còn lại bị lỗi/thiếu |
| Quản lý đồ ăn (CRUD + tìm kiếm) | ✅ Có | Đầy đủ chức năng |
| Tạo đơn hàng + tính tổng + giảm giá | ✅ Có | Có ở cả 2 package |
| Cập nhật trạng thái đơn hàng | ✅ Có | PENDING, PAID, CANCELLED |
| Thống kê doanh thu theo ngày/tháng | ✅ Có | StatisticsService |
| Món bán chạy nhất | ✅ Có | getTopSellingItems() |
| Phí dịch vụ | ❌ Thiếu | SRS yêu cầu nhưng chưa có |

---

## 🎯 KẾT LUẬN (TRƯỚC KHI REFACTOR)

**Trạng thái trước refactor: ❌ CHƯA SẴN SÀNG ĐỂ MERGE**

Vấn đề lớn nhất là **sự trùng lặp và không tương thích giữa các package**. Mỗi người làm task của mình nhưng **KHÔNG dùng chung model**, dẫn đến 3 module không thể kết nối.

---

## ✅ KẾT QUẢ SAU KHI REFACTOR

**Trạng thái hiện tại: ✅ ĐÃ HOÀN THÀNH - BUILD SUCCESSFUL, 22/22 TEST PASS**

### Những gì đã làm:

| # | Hành động | Trạng thái |
|---|-----------|-----------|
| 1 | Xóa package `food_management` (trùng lặp) | ✅ Đã xóa |
| 2 | Xóa package `repository.legacy` (trùng lặp) | ✅ Đã xóa |
| 3 | Thống nhất dùng 1 bộ `model.*` duy nhất | ✅ Hoàn thành |
| 4 | Thêm setter cho `model.MenuItem` | ✅ Hoàn thành |
| 5 | Thêm `applyDiscount()`, `applyServiceFee()`, `displayOrder()`, `toString()` cho `model.Order` | ✅ Hoàn thành |
| 6 | Sửa `OrderItem` dùng `calculatePrice()` tự động (Polymorphism) | ✅ Hoàn thành |
| 7 | Thêm `toString()` cho `Food`, `Drink`, `Dessert` | ✅ Hoàn thành |
| 8 | Tạo `repository.MenuRepository` mới (dùng `model.MenuItem`) | ✅ Hoàn thành |
| 9 | Tạo `repository.OrderRepository` mới (dùng `model.Order`, có `update()`, `sum()`) | ✅ Hoàn thành |
| 10 | Tạo `exception.InsufficientStockException` | ✅ Hoàn thành |
| 11 | Tạo `exception.InvalidOrderIdException` | ✅ Hoàn thành |
| 12 | Sửa `build.gradle` - xóa JUnit 6.0.3 không tồn tại | ✅ Hoàn thành |
| 13 | Sửa `StatisticsServiceTest` dùng đúng JUnit 5 | ✅ Hoàn thành |
| 14 | Xóa file `test/Test.java` (annotation tự tạo) | ✅ Đã xóa |
| 15 | Sửa `TestDataFactory` dùng `OrderItem(item, quantity)` mới | ✅ Hoàn thành |
| 16 | Viết lại `OrderTest` toàn diện (17 test cases) | ✅ Hoàn thành |
| 17 | Tạo `app.Main` - Console Menu thống nhất | ✅ Hoàn thành |
| 18 | Thêm phí dịch vụ (serviceFee) theo SRS | ✅ Hoàn thành |
| 19 | Thêm `StatisticsService.calculateTotalRevenue()` | ✅ Hoàn thành |

### Cấu trúc thư mục sau refactor:

```
src/main/java/
├── app/
│   └── Main.java                    ← Console Menu chính (mới)
├── exception/
│   ├── InsufficientStockException.java  ← Custom Exception (mới)
│   └── InvalidOrderIdException.java     ← Custom Exception (mới)
├── model/
│   ├── MenuItem.java                ← Lớp cha (đã sửa: thêm setter, toString)
│   ├── Food.java                    ← Kế thừa MenuItem (đã sửa: thêm toString)
│   ├── Drink.java                   ← Kế thừa MenuItem, có size S/M/L (đã sửa)
│   ├── Dessert.java                 ← Kế thừa MenuItem (đã sửa: thêm toString)
│   ├── Order.java                   ← Đơn hàng (đã sửa: applyDiscount, serviceFee, display)
│   ├── OrderItem.java               ← Chi tiết đơn hàng (đã sửa: tự lấy calculatePrice)
│   └── OrderStatus.java             ← Enum: PENDING, PAID, CANCELLED
├── repository/
│   ├── MenuRepository.java          ← CRUD Menu (mới, thay food_management.MenuManager)
│   └── OrderRepository.java         ← CRUD Order (mới, thay repository.legacy.OrderRepository)
└── service/
    └── StatisticsService.java       ← Thống kê doanh thu (đã sửa: thêm calculateTotalRevenue)

src/test/java/
├── repository/legacy/
│   └── OrderTest.java               ← 17 test cases (đã viết lại hoàn toàn)
└── test/
    ├── StatisticsServiceTest.java   ← 5 test cases (đã sửa dùng JUnit 5)
    └── TestDataFactory.java         ← Factory tạo test data (đã sửa)
```

### Kết quả Test:

```
OrderTest:          17 tests, 0 failures, 0 errors ✅
StatisticsServiceTest: 5 tests, 0 failures, 0 errors ✅
────────────────────────────────────────────────────
TỔNG:               22 tests, 0 failures, 0 errors ✅
```

### Đáp ứng SRS sau refactor:

| Yêu cầu SRS | Trạng thái |
|---|---|
| Encapsulation (private + getter/setter) | ✅ |
| Inheritance (MenuItem → Food, Drink, Dessert) | ✅ |
| Polymorphism (override calculatePrice - Drink S/M/L) | ✅ |
| Collections Framework (List, Map) | ✅ |
| Stream API (filter, reduce, sum) | ✅ |
| Optional (findById trả về Optional) | ✅ |
| Custom Exceptions (InsufficientStockException, InvalidOrderIdException) | ✅ |
| try-catch (xử lý input, exception) | ✅ |
| Unit Test JUnit 5 (22 test cases) | ✅ |
| Quản lý đồ ăn (CRUD + tìm kiếm) | ✅ |
| Tạo đơn hàng + tính tổng + giảm giá | ✅ |
| Phí dịch vụ | ✅ |
| Cập nhật trạng thái đơn hàng | ✅ |
| Thống kê doanh thu theo ngày/tháng | ✅ |
| Món bán chạy nhất | ✅ |
| Console Menu | ✅ |
