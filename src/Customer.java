public class Customer extends User {

    public int customerId;
    public String name;
    public String phone;   // PRIMARY KEY
    public String address;

    public Customer(int customerId, String name, String phone, String address) {
        super(customerId, name);   // aligns with User
        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    @Override
    public void displayRole() {
        System.out.println("Customer");
    }

    @Override
    public String toString() {
        return name + " | " + phone;
    }
}
