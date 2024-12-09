package OverrideCore;
import AbstractCore.Room;

public class Standard extends Room {
    // Constructor for Standard class
    public Standard(String view, double area, String status, int roomNumber, int capacity) {
        // Call the parent Room constructor with a fixed price of 500000 for Standard rooms
        super(view, area, status, 500000, capacity); // Assuming capacity is 2 for a Standard room
        generateId(roomNumber); // Generate the room ID
    }

    // Override the generateId method to create room IDs like STA001, STA002, etc.
    @Override
    public void generateId(int roomNumber) {
        this.id = "STA" + String.format("%03d", roomNumber); // STA001, STA002,...
    }

    // Override the getLineFromFile to call the parent method
    @Override
    public void getLineFromFile(String line) {
        super.getLineFromFile(line);
    }

    // Override the mergeInformationToFile to call the parent method
    @Override
    public String mergeInformationToFile() {
        return super.mergeInformationToFile();
    }

    // Override the output method to print the room details inside a box
    @Override
    public void output() {
        super.output(); // Call the parent class's output method to print room details
    }
}
