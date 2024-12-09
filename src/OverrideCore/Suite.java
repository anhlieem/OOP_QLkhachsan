package OverrideCore;
import AbstractCore.Room;

public class Suite extends Room {
    public Suite(String view, double area, String status, int roomNumber, int capacity) {
        super(view, area, status, 3000000, capacity); // Giá cố định cho Suite
        generateId(roomNumber);
    }

    @Override
    public void generateId(int roomNumber) {
        this.id = "SUT" + String.format("%03d", roomNumber); // SUT01, SUT02,...
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
    public void adjustPriceForView() {
        //Không charge thêm 15% cho view như các loại phòng khác
    }

    @Override
    public void output () {
        super.output();
    }
}

