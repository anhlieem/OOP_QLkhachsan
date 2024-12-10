package Run;

import AbstractCore.Room;
import OverrideCore.Standard;
import OverrideCore.Deluxe;
import OverrideCore.Suite;
import MainCore.FileUtil;
import MainCore.Booking;
import MainCore.Person;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static int bookingCounter = 1; // Booking ID counter

    public static void main(String[] args) {
        List<Room> allRooms = new ArrayList<>();
        List<Booking> allBookings = new ArrayList<>();
        List<String[]> employees = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
    
        // Add employees
        employees.add(new String[]{"E001", "Ngo Gia Bao"});
        employees.add(new String[]{"E002", "Le Minh Huy"});
        employees.add(new String[]{"E003", "Huu Hau"});
        employees.add(new String[]{"E004", "Tran Thanh"});
        
        loadRoomData(allRooms);
        loadBookingData(allBookings, allRooms);

        // Login system
        boolean loginSuccessful = performLogin(employees, sc);
        if (!loginSuccessful) {
            System.out.println("Sai qua nhieu lan! Vui long thu lai sau!");
            return;
        }
    
        // Display current time and employee info
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        Date currentTime = new Date();
        System.out.println("\u001B[33mWelcome, " + employeeName + "!\u001B[0m");
        System.out.println("\u001B[33mHien tai: " + formatter.format(currentTime) + "\u001B[0m");
    
        // Main menu loop
        boolean running = true;
        while (running) {
            System.out.println("\n+==============================+");
            System.out.println("|             MENU             |");
            System.out.println("+==============================+");
            System.out.println("| 1. In Danh Sach Phong        |");
            System.out.println("| 2. Them Booking              |");
            System.out.println("| 3. Quan ly Bookings          |");
            System.out.println("| 4. Them dich vu vao phong    |");
            System.out.println("| 0. Exit                      |");
            System.out.println("+------------------------------+");
            System.out.print("Choose an option: ");
    
            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    printRoomTable(allRooms);
                    break;
                case "2":
                    // Pass employeeId to the addBooking method
                    addBooking(allBookings, allRooms, employees, sc);
                    break;
                case "3":
                    manageBookings(allBookings, sc);
                    break;
                case "4":
                    addServiceToRoom(allRooms, sc);
                    break;
                case "0":
                    System.out.println("Ket thuc chuong trinh. Hen gap lai!");
                    running = false;
                    break;
                default:
                    System.out.println("Loi lua chon! Vui long chon lai.");
            }
        }
        sc.close();
    
        // Save data before exiting
        saveData(allRooms, allBookings);
    }

// Global variables to store the logged-in employee's ID and name
static String employeeId = "";
static String employeeName = "";

public static boolean performLogin(List<String[]> employees, Scanner sc) {
    int attempts = 3;
    while (attempts > 0) {
        System.out.print("Nhap vao ID nhan vien: ");
        String enteredId = sc.nextLine();
        System.out.print("Nhap vao mat khau: ");
        String enteredPassword = sc.nextLine();

        for (String[] employee : employees) {
            if (employee[0].equalsIgnoreCase(enteredId) && enteredPassword.equals("123")) {
                employeeId = employee[0]; // Store employee ID globally
                employeeName = employee[1]; // Store employee name globally
                return true; // Successful login
            }
        }

        attempts--;
        System.out.println("Sai ID nhan vien hoac mat khau! Ban con " + attempts + " luot thu.");
    }
    return false; // Failed login after 3 attempts
}




private static void addBooking(List<Booking> allBookings, List<Room> allRooms, List<String[]> employees, Scanner sc) {
    String bookingId = "BK" + bookingCounter++; // Generate automatic Booking ID

    System.out.println("Nhap vao ten Khach hang: ");
    String customerName = sc.nextLine();

    System.out.println("Nhap vao SDT Khach hang: ");
    String customerPhone = sc.nextLine();

    System.out.println("Nhap vao ngay Check-in (yyyy-MM-dd):");
    String checkInDate = sc.nextLine();

    System.out.println("Nhap vao ngay Check-out (yyyy-MM-dd):");
    String checkOutDate = sc.nextLine();

    // Use the globally stored employeeId and employeeName
    Booking booking = new Booking(bookingId, checkInDate, checkOutDate, customerName, customerPhone, employeeId);

    System.out.println("Danh sach cac phong co the dat:");
    printRoomTable(allRooms);

    System.out.println("Nhap vao (cac) so phong (ngan cach nhau bang dau ','):");
    String[] roomIds = sc.nextLine().split(",");
    for (String roomId : roomIds) {
        roomId = roomId.trim();
        Room room = findRoomById(roomId, allRooms);
        if (room != null && Booking.isRoomAvailable(roomId, checkInDate, checkOutDate, allBookings)) {
            booking.addRoom(room);
        } else {
            System.out.println("Phong " + roomId + " khong kha dung hoac khong ton tai.");
        }
    }

    // Prompt user to add guests
    System.out.println("Nhap vao so luong Khach luu tru");
    int numberOfGuests = Integer.parseInt(sc.nextLine());

    for (int i = 0; i < numberOfGuests; i++) {
        System.out.println("Nhap ten Khach " + (i + 1) + ":");
        String guestName = sc.nextLine();
        System.out.println("Nhap so dien thoai Khach " + (i + 1) + ":");
        String guestPhone = sc.nextLine();

        // Add the guest to the booking
        boolean addedSuccessfully = booking.addGuest(guestName, guestPhone);
        if (!addedSuccessfully) {
            System.out.println("Khong du cho cho " + guestName + " Khach luu tru.");
        }
    }

    allBookings.add(booking);
    System.out.println("Booking thanh cong!");

    // Update room statuses after adding the booking using Booking class's method
    Booking.updateRoomStatuses(allBookings, allRooms);

    // Save the updated room data to ListRoom.txt
    saveData(allRooms, allBookings);
}


    private static List<Room> getAvailableRoomsForDates(List<Room> allRooms, String checkInDate, String checkOutDate, List<Booking> allBookings) {
        List<Room> availableRooms = new ArrayList<>();
    
        for (Room room : allRooms) {
            boolean isAvailable = true;
    
            // Check if the room is already booked for the given period
            for (Booking booking : allBookings) {
                if (Booking.isRoomAvailable(room.getRoomId(), checkInDate, checkOutDate, allBookings)) {
                    continue;
                } else {
                    isAvailable = false;
                    break;
                }
            }
    
            if (isAvailable) {
                availableRooms.add(room);
            }
        }
    
        return availableRooms;
    }
    
    

    private static void addServiceToRoom(List<Room> allRooms, Scanner sc) {
        System.out.print("Nhap vao ma phong can them dich vu: ");
        String roomId = sc.nextLine();
    
        // Find the room by its ID
        Room room = findRoomById(roomId, allRooms);
        if (room == null) {
            System.out.println("Phong " + roomId + " khong ton tai!");
            return;
        }
    
        // Call the addService method of the Room class to allow adding services
        System.out.println("Ban da chon phong: " + roomId);
        room.addService(sc);  // Pass the Scanner to the room's addService method
    }

    private static void loadRoomData(List<Room> allRooms) {
        List<String> roomData = FileUtil.readFile("OOP_QLkhachsan/database/ListRoom.txt");
        for (String line : roomData) {
            Room room = parseRoom(line);
            if (room != null) {
                allRooms.add(room);
            }
        }
    }

    private static void loadBookingData(List<Booking> allBookings, List<Room> allRooms) {
        List<String> bookingData = FileUtil.readFile("OOP_QLkhachsan/database/ListBooking.txt");
        for (String line : bookingData) {
            Booking booking = new Booking();
            booking.getLineFromFile(line, allRooms);
            allBookings.add(booking);
        }
        // Call the updateRoomStatuses method from Booking class to update room statuses
        Booking.updateRoomStatuses(allBookings, allRooms);
    }

    private static void manageBookings(List<Booking> allBookings, Scanner sc) {
        boolean running = true;
        while (running) {
            System.out.println("\n+====================================+");
            System.out.println("|           Booking Menu             |");
            System.out.println("+====================================+");
            System.out.println("| 1. Xem tat ca Booking              |");
            System.out.println("| 2. Tim kiem theo ID Booking        |");
            System.out.println("| 3. Tim kiem theo SDT Khach hang    |");
            System.out.println("| 0. Tro lai                         |");
            System.out.println("+------------------------------------+");
            System.out.print("Moi ban nhap lua chon: ");

            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    for (Booking booking : allBookings) {
                        booking.toString();
                    }
                    break;
                case "2":
                    System.out.println("Nhap vao ID cua Booking: ");
                    String bookingId = sc.nextLine();
                    for (Booking booking : allBookings) {
                        if (booking.getBookingId().equalsIgnoreCase(bookingId)) {
                            booking.printBookingDetails();
                            break;
                        }
                    }
                    break;
                case "3":
                    System.out.println("Nhap vao SDT cua Khach hang: ");
                    String phone = sc.nextLine();
                    for (Booking booking : allBookings) {
                        if (booking.getCustomer().getPhone().equals(phone)) {
                            booking.printBookingDetails();
                        }
                    }
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Loi lua chon! Vui long thu lai.");
            }
        }
    }

    private static Room parseRoom(String line) {
        try {
            String idPrefix = line.substring(0, 3);
            Room room = null;

            switch (idPrefix) {
                case "STA":
                    room = new Standard("", 0, "available", 0, 0);
                    break;
                case "DLX":
                    room = new Deluxe("", 0, "available", 0, 0);
                    break;
                case "SUT":
                    room = new Suite("", 0, "available", 0, 0);
                    break;
                default:
                    System.err.println("Khong ro loai phong " + line);
                    return null;
            }

            if (room != null) {
                room.getLineFromFile(line);
            }
            return room;

        } catch (Exception e) {
            System.err.println("Error parsing room: " + e.getMessage());
            return null;
        }
    }


    public static Date parseDate(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(dateString);
        } catch (Exception e) {
            return null;
        }
    }

    private static Room findRoomById(String roomID, List<Room> allRooms) {
        for (Room room : allRooms) {
            if (room.getRoomId().equalsIgnoreCase(roomID)) {
                return room;
            }
        }
        return null;
    }

    private static void printRoomTable(List<Room> allRooms) {
        System.out.println("+========+================+========+=============+===========+==============+");
        System.out.println("| RoomID | View           | Area   | Status      | Capacity  | Price        |");
        System.out.println("+========+================+========+=============+===========+==============+");

        for (Room room : allRooms) {
            String statusColor;
            switch (room.getStatus().toLowerCase()) {
                case "available":
                    statusColor = "\u001B[32m"; // Green
                    break;
                case "under maintenance":
                    statusColor = "\u001B[33m"; // Yellow
                    break;
                case "booked":
                case "in use":
                    statusColor = "\u001B[31m"; // Red
                    break;
                default:
                    statusColor = "\u001B[0m"; // Reset
            }

            System.out.printf("| %-6s | %-14s | %-6.2f | %s%-11s\u001B[0m | %-9d | %-12.2f |\n",
                    room.getRoomId(), room.getView(), room.getDienTich(),
                    statusColor, room.getStatus(), room.getCapacity(), room.getGiaNgay());
        }
        System.out.println("+--------+----------------+--------+-------------+-----------+--------------+");
    }

    private static void saveData(List<Room> allRooms, List<Booking> allBookings) {
        List<String> roomStrings = new ArrayList<>();
        for (Room room : allRooms) {
            roomStrings.add(room.mergeInformationToFile()); // Convert each room to a string
        }
        FileUtil.writeFile("OOP_QLkhachsan/database/ListRoom.txt", roomStrings);

        List<String> bookingStrings = new ArrayList<>();
        for (Booking booking : allBookings) {
            bookingStrings.add(booking.mergeInformationToFile()); // Convert each booking to a string
        }
        FileUtil.writeFile("OOP_QLkhachsan/database/ListBooking.txt", bookingStrings);
    }
}
