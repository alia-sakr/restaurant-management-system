public class Staff extends User {

    public int staffId;
    public String name;

    public Staff(int staffId, String name) {
        super(staffId, name);   // inheritance usage
        this.staffId = staffId;
        this.name = name;
    }

    public int getStaffId() {
        return staffId;
    }

    public String getName() {
        return name;
    }

    @Override
    public void displayRole() {
        System.out.println("Staff");
    }

    @Override
    public String toString() {
        return name;
    }
}
