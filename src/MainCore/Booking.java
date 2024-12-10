package MainCore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import AbstractCore.Room;

public class Booking {
    private String bookingId;
    private String checkInTime;
    private String checkOutTime;
    private Person customer;
    private ArrayList<Person> guestList;
    private String employeeId;
    private ArrayList<Room> bookedRooms;
    private double totalPrice;

    // Constructor
    public Booking() {
        this.bookingId = "";
        this.checkInTime = "";
        this.checkOutTime = "";
        this.customer = new Person("", ""); // Empty customer
        this.employeeId = "";
        this.guestList = new ArrayList<>();
        this.bookedRooms = new ArrayList<>();
        this.totalPrice = 0.0;
    }

    public Booking(String bookingId, String checkInTime, String checkOutTime, String customerName, String customerPhone, String employeeId) {
        this.bookingId = bookingId;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.customer = new Person(customerName, customerPhone);
        this.employeeId = employeeId;
        this.guestList = new ArrayList<>();
        this.bookedRooms = new ArrayList<>();
        this.totalPrice = 0.0;
    }

    // Add a guest to the booking, checking room capacity
    public boolean addGuest(String name, String phone) {
        // Calculate total capacity of all booked rooms
        int totalCapacity = 0;
        for (Room room : bookedRooms) {
            totalCapacity += room.getCapacity(); // Assuming getCapacity() method exists in the Room class
        }

        // Ensure the number of guests does not exceed room capacity
        if (guestList.size() < totalCapacity) {
            guestList.add(new Person(name, phone));
            return true;
        } else {
            System.out.println("Phong khong du chua!!");
            return false;
        }
    }

    // Add a room to the booking
    public void addRoom(Room room) {
        bookedRooms.add(room);
        room.setStatus("booked");
    }

    // Method to parse from file
    public void getLineFromFile(String line, List<Room> allRooms) {
        try {
            String[] str = line.split("#");

            if (str.length < 8) {
                throw new IllegalArgumentException("Invalid input data: Insufficient fields in the line.");
            }

            this.bookingId = str[0];
            this.checkInTime = str[1];
            this.checkOutTime = str[2];
            this.customer = new Person(str[3], str[4]);
            this.employeeId = str[5];

            // Guest List Parsing
            this.guestList = new ArrayList<>();
            if (!str[6].isEmpty()) {
                String[] guestData = str[6].split(";");
                for (String guestInfo : guestData) {
                    String[] guestDetails = guestInfo.split(",");
                    if (guestDetails.length == 2) {
                        guestList.add(new Person(guestDetails[0], guestDetails[1]));
                    }
                }
            }

            // Room List Parsing
            this.bookedRooms = new ArrayList<>();
            if (!str[7].isEmpty()) {
                String[] roomData = str[7].split(";");
                for (String roomID : roomData) {
                    Room foundRoom = findRoomById(roomID, allRooms);
                    if (foundRoom != null) {
                        addRoom(foundRoom);
                    } else {
                        System.err.println("Khong the tim thay phong" + roomID);
                    }
                }
            }

            // Set total price or calculate it
            this.totalPrice = str.length > 8 ? Double.parseDouble(str[8]) : calculateTotalPrice();

        } catch (Exception e) {
            System.err.println("Error parsing line: " + e.getMessage());
        }
    }

    // Helper method to find a room by its ID in the list of all rooms
    private Room findRoomById(String roomID, List<Room> allRooms) {
        for (Room room : allRooms) {
            if (room.getRoomId().equals(roomID)) {
                return room; // Return the matching room
            }
        }
        return null; // Return null if no matching room was found
    }

    // Merge booking details into a file-friendly string
    public String mergeInformationToFile() {
        StringBuilder guests = new StringBuilder();
        for (Person guest : guestList) {
            guests.append(guest.getName()).append(",").append(guest.getPhone()).append(";"); // Format: Name,Phone;
        }

        StringBuilder rooms = new StringBuilder();
        for (Room room : bookedRooms) {
            rooms.append(room.getRoomId()).append(";"); // Only include RoomID, type inferred by prefix
        }

        // Return formatted string with all details
        return String.format("%s#%s#%s#%s#%s#%s#%s#%s#%.2f",
                bookingId,
                checkInTime,
                checkOutTime,
                customer.getName(),
                customer.getPhone(),
                employeeId,
                guests.toString().isEmpty() ? "" : guests.toString().substring(0, guests.length() - 1), // Remove trailing `;`
                rooms.toString().isEmpty() ? "" : rooms.toString().substring(0, rooms.length() - 1), // Remove trailing `;`
                totalPrice);
    }

    // Calculate total price for all booked rooms, including service fees
    public double calculateTotalPrice() {
        double totalPrice = 0;
        for (Room room : bookedRooms) {
            totalPrice += room.getGiaNgay(); // Base price per night
            totalPrice += room.calculateServiceFee(); // Add the service fees for this room
        }
        return totalPrice;
    }

    // Print booking details including room services
    public void printBookingDetails() {
        System.out.println("Booking ID: " + bookingId);
        System.out.println("Check-in Time: " + checkInTime);
        System.out.println("Check-out Time: " + checkOutTime);
        System.out.println("Customer Name: " + customer.getName());
        System.out.println("Customer Phone: " + customer.getPhone());
        System.out.println("Employee ID: " + employeeId);

        System.out.println("Khach o:");
        for (Person guest : guestList) {
            System.out.println(" - Ten: " + guest.getName() + ", SDT: " + guest.getPhone());
        }

        System.out.println("Danh sach phong dat:");
        for (Room room : bookedRooms) {
            System.out.println(" - " + room.toString());
            System.out.println("   Cac dich vu da su dung: ");
            room.printServicesUsed();
        }

        System.out.println("Total: " + calculateTotalPrice() + " VND");
    }

    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getCheckInTime() { return checkInTime; }
    public void setCheckInTime(String checkInTime) { this.checkInTime = checkInTime; }

    public String getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(String checkOutTime) { this.checkOutTime = checkOutTime; }

    public Person getCustomer() { return customer; }
    public void setCustomer(Person customer) { this.customer = customer; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public ArrayList<Person> getGuestList() { return guestList; }
    public ArrayList<Room> getBookedRooms() { return bookedRooms; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }


    public static void updateRoomStatuses(List<Booking> bookings, List<Room> allRooms) {
        Date currentTime = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
        // Reset all rooms to "available" initially
        for (Room room : allRooms) {
            room.setStatus("available");
        }
    
        // Process each booking
        for (Booking booking : bookings) {
            try {
                Date checkIn = sdf.parse(booking.getCheckInTime());
                Date checkOut = sdf.parse(booking.getCheckOutTime());
    
                // Iterate through each room in the current booking
                for (Room room : booking.getBookedRooms()) {
                    if (currentTime.compareTo(checkIn) >= 0 && currentTime.compareTo(checkOut) <= 0) {
                        // Room is "in use" because the current date is within the booking period
                        room.setStatus("in use");
                    } else if (currentTime.compareTo(checkIn) < 0) {
                        // Room is "booked" because the current date is before the booking starts
                        if (!room.getStatus().equals("in use")) { // Avoid overriding "in use" status
                            room.setStatus("booked");
                        }
                    } else if (currentTime.compareTo(checkOut) > 0) {
                        // Room is "available" because the current date is after the booking ends
                        if (!room.getStatus().equals("in use")) { // Ensure we don't overwrite in-use rooms
                            room.setStatus("available");
                        }
                    }
                }
            } catch (ParseException e) {
                System.err.println("Error parsing dates for booking ID: " + booking.getBookingId());
            }
        }
    }
    

    public static boolean isRoomAvailable(String roomId, String desiredCheckIn, String desiredCheckOut, List<Booking> bookings) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date desiredIn = sdf.parse(desiredCheckIn);
            Date desiredOut = sdf.parse(desiredCheckOut);

            for (Booking booking : bookings) {
                for (Room room : booking.getBookedRooms()) {
                    if (room.getRoomId().equals(roomId)) {
                        Date existingIn = sdf.parse(booking.getCheckInTime());
                        Date existingOut = sdf.parse(booking.getCheckOutTime());

                        if (desiredIn.compareTo(existingOut) < 0 && desiredOut.compareTo(existingIn) > 0) {
                            return false; // Overlap detected
                        }
                    }
                }
            }
            return true; // No overlaps
        } catch (ParseException e) {
            System.err.println("Error parsing dates.");
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder guestDetails = new StringBuilder();
        if (!guestList.isEmpty()) {
            guestDetails.append("Guests: ");
            for (Person guest : guestList) {
                guestDetails.append(guest.getName()).append(" (").append(guest.getPhone()).append("), ");
            }
            guestDetails.setLength(guestDetails.length() - 2); // Remove trailing comma
        } else {
            guestDetails.append("No guests.");
        }

        StringBuilder roomDetails = new StringBuilder();
        if (!bookedRooms.isEmpty()) {
            roomDetails.append("Booked Rooms: ");
            for (Room room : bookedRooms) {
                roomDetails.append(room.getRoomId()).append(" (").append(room.getStatus()).append("), ");
            }
            roomDetails.setLength(roomDetails.length() - 2); // Remove trailing comma
        } else {
            roomDetails.append("No rooms booked.");
        }

        return String.format("Booking ID: %s\nCustomer: %s (%s)\nCheck-in: %s\nCheck-out: %s\nEmployee ID: %s\n%s\n%s\nTotal Price: %.2f VND",
                bookingId, 
                customer.getName(), customer.getPhone(), 
                checkInTime, checkOutTime, 
                employeeId,
                guestDetails.toString(), 
                roomDetails.toString(),
                totalPrice);
    }
}
