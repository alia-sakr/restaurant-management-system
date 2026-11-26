public class Delivery {

    private String deliveryPerson;
    private String status;

    public Delivery(String deliveryPerson) {
        this.deliveryPerson = deliveryPerson;
        this.status = "Preparing";
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getDeliveryPerson() {
        return deliveryPerson;
    }
}
