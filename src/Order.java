import java.util.ArrayList;

public class Order {

    private static int orderCounter = 1;   // Static member
    private int orderId;

    private ArrayList<MenuItem> items;
    private Payment payment;
    private Delivery delivery;
    private Customer customer;


    public Order() {
        orderId = orderCounter++;
        items = new ArrayList<>();
        delivery = new Delivery("Ahmed");   // Composition
    }

    // Method Overloading
    public void addItem(MenuItem item) {
        items.add(item);
    }

    public void addItem(MenuItem item, int quantity) {
        for (int i = 0; i < quantity; i++) {
            items.add(item);
        }
    }

    public double getTotal() {
        double total = 0;
        for (MenuItem item : items) {
            total += item.getPrice();
        }
        return total;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public int getOrderId() {
        return orderId;
    }
    
    public void setCustomer(Customer customer) {
    this.customer = customer;
    }
    
    public Customer getCustomer() {
    return customer;
    }


}