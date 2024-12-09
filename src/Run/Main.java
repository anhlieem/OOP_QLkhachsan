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

        // Display current time
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        Date currentTime = new Date();
        System.out.println("\u001B[33mWelcome!\u001B[0m");
        System.out.println("\u001B[33mCurrent time: " + formatter.format(currentTime) + "\u001B[0m");

        // Load rooms and bookings
        loadRoomData(allRooms);
        loadBookingData(allBookings, allRooms);

        // Main menu loop
        boolean running = true;
        while (running) {
            System.out.println("\n+---------------------------+");
            System.out.println("|           MENU            |");
            System.out.println("+---------------------------+");
            System.out.println("| 1. Print Room Table       |");
            System.out.println("| 2. Add Booking            |");
            System.out.println("| 3. Print Bookings         |");
            System.out.println("| 4. Add Service to Room    |");
            System.out.println("| 0. Exit                   |");
            System.out.println("+---------------------------+");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    printRoomTable(allRooms);
                    break;
                case "2":
                    addBooking(allBookings, allRooms, employees, sc);
                    break;
                case "3":
                    manageBookings(allBookings, sc);
                    break;
                case "4":
                    addServiceToRoom(allRooms, sc);
                    break;
                case "0":
                    System.out.println("Exiting program. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        sc.close();

        // Save data before exiting
        saveData(allRooms, allBookings);
    }

    private static void addBooking(List<Booking> allBookings, List<Room> allRooms, List<String[]> employees, Scanner sc) {
        String bookingId = "BK" + bookingCounter++; // Generate automatic Booking ID

        System.out.println("Enter Customer Name:");
        String customerName = sc.nextLine();

        System.out.println("Enter Customer Phone:");
        String customerPhone = sc.nextLine();

        System.out.println("Enter Check-in Date (yyyy-MM-dd):");
        String checkInDate = sc.nextLine();

        System.out.println("Enter Check-out Date (yyyy-MM-dd):");
        String checkOutDate = sc.nextLine();

        System.out.println("Assign an Employee (ID):");
        for (String[] employee : employees) {
            System.out.println(" - " + employee[0] + ": " + employee[1]);
        }
        String employeeId = sc.nextLine();

        Booking booking = new Booking(bookingId, checkInDate, checkOutDate, customerName, customerPhone, employeeId);

        System.out.println("Available Rooms:");
        printRoomTable(allRooms);

        System.out.println("Enter Room IDs to book (separated by commas):");
        String[] roomIds = sc.nextLine().split(",");
        for (String roomId : roomIds) {
            roomId = roomId.trim();
            Room room = findRoomById(roomId, allRooms);
            if (room != null && Booking.isRoomAvailable(roomId, checkInDate, checkOutDate, allBookings)) {
                booking.addRoom(room);
            } else {
                System.out.println("Room " + roomId + " is not available or does not exist.");
            }
        }

        System.out.println("Enter the amount of the people will stay:");
        

        allBookings.add(booking);
        System.out.println("Booking successfully added!");

        // Update room statuses after adding the booking using Booking class's method
        Booking.updateRoomStatuses(allBookings, allRooms);

        // Save the updated room data to ListRoom.txt
        saveData(allRooms, allBookings);
    }

    private static void addServiceToRoom(List<Room> allRooms, Scanner sc) {
        System.out.print("Enter Room ID to add services: ");
        String roomId = sc.nextLine();
    
        // Find the room by its ID
        Room room = findRoomById(roomId, allRooms);
        if (room == null) {
            System.out.println("Room with ID " + roomId + " not found.");
            return;
        }
    
        // Call the addService method of the Room class to allow adding services
        System.out.println("You have selected Room: " + roomId);
        room.addService(sc);  // Pass the Scanner to the room's addService method
    }

    private static void loadRoomData(List<Room> allRooms) {
        List<String> roomData = FileUtil.readFile("./database/ListRoom.txt");
        for (String line : roomData) {
            Room room = parseRoom(line);
            if (room != null) {
                allRooms.add(room);
            }
        }
    }

    private static void loadBookingData(List<Booking> allBookings, List<Room> allRooms) {
        List<String> bookingData = FileUtil.readFile("./database/ListBooking.txt");
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
            System.out.println("\n+---------------------------+");
            System.out.println("|       Booking Menu        |");
            System.out.println("+---------------------------+");
            System.out.println("| 1. View All Bookings      |");
            System.out.println("| 2. Search Booking by ID   |");
            System.out.println("| 3. Search by Customer     |");
            System.out.println("| 0. Return                 |");
            System.out.println("+---------------------------+");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    for (Booking booking : allBookings) {
                        booking.toString();
                    }
                    break;
                case "2":
                    System.out.println("Enter Booking ID:");
                    String bookingId = sc.nextLine();
                    for (Booking booking : allBookings) {
                        if (booking.getBookingId().equalsIgnoreCase(bookingId)) {
                            booking.printBookingDetails();
                            break;
                        }
                    }
                    break;
                case "3":
                    System.out.println("Enter Customer Phone:");
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
                    System.out.println("Invalid choice. Please try again.");
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
                    System.err.println("Unknown room type: " + line);
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

    private static Room findRoomById(String roomID, List<Room> allRooms) {
        for (Room room : allRooms) {
            if (room.getRoomId().equalsIgnoreCase(roomID)) {
                return room;
            }
        }
        return null;
    }

    private static void printRoomTable(List<Room> allRooms) {
        System.out.println("+--------+----------------+--------+-------------+-----------+--------------+");
        System.out.println("| RoomID | View           | Area   | Status      | Capacity  | Price        |");
        System.out.println("+--------+----------------+--------+-------------+-----------+--------------+");

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
        FileUtil.writeFile("./database/ListRoom.txt", roomStrings);

        List<String> bookingStrings = new ArrayList<>();
        for (Booking booking : allBookings) {
            bookingStrings.add(booking.toString()); // Convert each booking to a string
        }
        FileUtil.writeFile("./database/ListBooking.txt", bookingStrings);
    }
}
