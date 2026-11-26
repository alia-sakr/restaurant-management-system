public class User {
    protected int customerid;
    protected String name;

    public User(int customerid, String name) {
        this.customerid = customerid;
        this.name = name;
    }

    public int getCustomerId() {
        return customerid;
    }

    public String getName() {
        return name;
    }
    
    public void displayRole() { 
        System.out.println("User");
    }
}
