package model;

public class OrderStatus {

    public static final byte PAYMENT_PENDING = 0;
    public static final byte RECEIVED = 1;
    public static final byte DISPATCHED = 2;
    public static final byte COMPLETED = 3;

    public static String getStatus(byte status) {
        switch (status) {
            case PAYMENT_PENDING:
                return "Payment Pending";
            case RECEIVED:
                return "Received";
            case DISPATCHED:
                return "Dispatched";
            case COMPLETED:
                return "Completed";
            default:
                return "PAYMENT_PENDING";
        }
    }

}
