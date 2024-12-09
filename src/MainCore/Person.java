package MainCore;

public class Person {
    private String name;
    private String phone;

    // Constructor
    public Person(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}

