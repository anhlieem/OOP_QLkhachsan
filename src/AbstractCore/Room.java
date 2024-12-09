package AbstractCore;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public abstract class Room {
    protected String id;
    protected String view;
    protected double dienTich;
    protected String status; // Available, In Use, Booked, Under Maintenance
    protected double giaNgay;
    protected int capacity; // Number of people the room can fit

    public static final Map<Integer, Double> DICH_VU_TINH = new HashMap<>();
    static {
        DICH_VU_TINH.put(1, 30000.0); //Giặt ủi
        DICH_VU_TINH.put(2, 35000.0); //Minibar
        DICH_VU_TINH.put(3, 20000.0); //Báo thức sáng
    }

    protected Map<Integer, Double> dichVuPhong;

    // Constructor
    public Room(String view, double dienTich, String status, double giaNgay, int capacity) {
        this.view = view;
        this.dienTich = dienTich;
        this.status = status;
        this.giaNgay = giaNgay;
        this.capacity = capacity;
        this.dichVuPhong = new HashMap<>();
        adjustPriceForView();
    }

    public abstract void generateId(int roomNumber);

    public double calculateServiceFee() {
        double totalServiceFee = 0;
        for (Map.Entry<Integer, Double> entry : dichVuPhong.entrySet()) {
            totalServiceFee += entry.getValue();
        }
        return totalServiceFee;
    }

    public void addService(Scanner sc) {
        // Assuming dichVuPhong is a Map<Integer, Double> (service ID and price) in your Room class
        boolean addingServices = true;
    
        while (addingServices) {
            System.out.println("+--------------------------------+");
            System.out.println("|      Add services to the room  |");
            System.out.println("|--------------------------------|");
            System.out.println("|  1. Laundry                    |");
            System.out.println("|  2. Minibar                    |");
            System.out.println("|  3. Morning Wake-up Call       |");
            System.out.println("|  0. Done                       |");
            System.out.println("+--------------------------------+");
    
            System.out.println("Choose a service:");
            int serviceChoice = Integer.parseInt(sc.nextLine());
    
            switch (serviceChoice) {
                case 1:
                case 2:
                case 3:
                    // Assuming dichVuPhong contains the services for the room with service ID and price.
                    if (!dichVuPhong.containsKey(serviceChoice)) {
                        // Assuming DICH_VU_TINH is a map where keys are service IDs and values are prices
                        dichVuPhong.put(serviceChoice, DICH_VU_TINH.get(serviceChoice));
                        System.out.println("Added service: " + getServiceName(serviceChoice) + " for " + DICH_VU_TINH.get(serviceChoice) + " VND");
                    } else {
                        System.out.println("This service is already added!");
                    }
                    break;
                case 0:
                    System.out.println("Finished adding services!");
                    addingServices = false;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    
    
    

    public void printServicesUsed() {
        if (dichVuPhong.isEmpty()) {
            System.out.println("Khong su dung dich vu nao.");
        } else {
            System.out.println("Danh sach dich vu da su dung:");
            for (Map.Entry<Integer, Double> entry : dichVuPhong.entrySet()) {
                System.out.println(getServiceName(entry.getKey()) + " - " + entry.getValue() + " VND");
            }
        }
    }

    public String getServiceName(int serviceId) {
        switch (serviceId) {
            case 1: return "Laundry";
            case 2: return "Minibar";
            case 3: return "Morning Wake-up Call";
            default: return "Unknown Service";
        }
    }
    public String getRoomId() { return id; }
    public void setRoomID() { this.id = id; }

    public String getView() { return view; }
    public void setView(String view) {
        this.view = view;
        adjustPriceForView();
    }

    public double getDienTich() { return dienTich; }
    public void setDienTich(double dienTich) { this.dienTich = dienTich; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getGiaNgay() { return giaNgay; }
    public void setGiaNgay(double giaNgay) { this.giaNgay = giaNgay; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public Map<Integer, Double> getDichVuPhong() { return dichVuPhong; }

    public void getLineFromFile(String line) {
        String[] str = line.split("#");
        this.id = str[0];
        this.view = str[1];
        this.dienTich = Double.parseDouble(str[2]);
        this.status = str[3];
        this.giaNgay = Double.parseDouble(str[4]);
        this.capacity = Integer.parseInt(str[5]); // Reading capacity from the file
    }

    public String mergeInformationToFile() {
        return getRoomId() + "#" + getView() + "#" + getDienTich() + "#" + getStatus() + "#" +
                getGiaNgay() + "#" + getCapacity();
    }

    public void inputForRoom() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhap vao view cua phong (hoac 0): ");
        setView(sc.nextLine());
        System.out.println("Nhap vao dien tich cua phong: ");
        setDienTich(Integer.parseInt(sc.nextLine()));
        System.out.println("Nhap vao suc chua cua phong (so nguoi): ");
        setCapacity(Integer.parseInt(sc.nextLine()));
        setStatus("available");
        sc.close();
    }

    public void adjustPriceForView() {
        if (this.view != null && !this.view.isEmpty()) {
            this.giaNgay *= 1.15;
        }
    }

    public void output() {
        // Create the top border of the box
        System.out.println("+------------------------------------------------+");
        System.out.println("|               Room Details                     |");
        System.out.println("+------------------------------------------------+");
    
        // Print room details inside the box with proper formatting
        System.out.printf("| %-46s %-20s |\n", "Room ID:", id);
        System.out.printf("| %-46s %-20s |\n", "View:", view);
        System.out.printf("| %-46s %-20.2f |\n", "Area (m2):", dienTich);
        System.out.printf("| %-46s %-20d |\n", "Capacity:", capacity);
        System.out.printf("| %-46s %-20s |\n", "Status:", status);
        System.out.printf("| %-46s %-20.2f |\n", "Price per Day (VND):", giaNgay);
    
        // Print the services used inside the box
        if (dichVuPhong.isEmpty()) {
            System.out.printf("| %-46s %-20s |\n", "Services Used:", "None");
        } else {
            System.out.printf("| %-46s %-20s |\n", "Services Used:", "");
            for (Map.Entry<Integer, Double> entry : dichVuPhong.entrySet()) {
                String serviceName = getServiceName(entry.getKey());
                double serviceCost = entry.getValue();
                System.out.printf("| %-46s %-20.2f |\n", serviceName, serviceCost);
            }
        }
    
        // Create the bottom border of the box
        System.out.println("+------------------------------------------------+");
    }
    

    @Override
    public String toString() {
        StringBuilder serviceDetails = new StringBuilder();
        if (dichVuPhong.isEmpty()) {
            serviceDetails.append("Khong su dung dich vu nao.");
        } else {
            serviceDetails.append("Dich vu su dung: ");
            for (Map.Entry<Integer, Double> entry : dichVuPhong.entrySet()) {
                serviceDetails.append(getServiceName(entry.getKey()))
                               .append(" - ")
                               .append(entry.getValue())
                               .append(" VND, ");
            }
            serviceDetails.setLength(serviceDetails.length() - 2);
        }

        return "Phong " + id + ", " + view + ", Dien tich " + dienTich + " m2, Capacity: " + capacity +
                " nguoi, Status: " + status + ", " + giaNgay + "/ngay, " + serviceDetails.toString();
    }
}
