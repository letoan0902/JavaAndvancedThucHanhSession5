package exception;

/**
 * Exception khi số lượng món ăn trong kho không đủ
 */
public class InsufficientStockException extends Exception {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String itemName, int requested, int available) {
        super("Món '" + itemName + "' không đủ số lượng. Yêu cầu: " + requested + ", Còn lại: " + available);
    }
}
