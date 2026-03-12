package exception;

/**
 * Exception khi ID đơn hàng không hợp lệ hoặc không tìm thấy
 */
public class InvalidOrderIdException extends Exception {

    public InvalidOrderIdException(String message) {
        super(message);
    }

    public InvalidOrderIdException(String orderId, String reason) {
        super("Order ID '" + orderId + "' không hợp lệ: " + reason);
    }
}
