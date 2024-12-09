package OverrideCore;
import AbstractCore.Room;

public class Deluxe extends Room {
    public Deluxe(String view, double area, String status, int roomNumber, int capacity) {
        super(view, area, status, 1000000, capacity); // Giá cố định cho Deluxe
        generateId(roomNumber);
    }

    @Override
    public void generateId(int roomNumber) {
        this.id = "DLX" + String.format("%03d", roomNumber); // DLX01, DLX02,...
    }


    @Override
    public void getLineFromFile(String line) {
        super.getLineFromFile(line);
    }

    @Override
    public String mergeInformationToFile() {
        return super.mergeInformationToFile();
    }

    @Override
    public void output () {
        super.output();
    }
}

