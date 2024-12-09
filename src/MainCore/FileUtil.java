package MainCore;

import java.io.*;
import java.util.*;

public class FileUtil {
    
    // Read lines from a file
    public static List<String> readFile(String filePath) {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        
        // Check if the file exists
        if (!file.exists()) {
            System.err.println("File does not exist: " + filePath);
            return lines;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return lines;
    }


    public static void writeFile(String filePath, List<?> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Object obj : data) {
                writer.write(obj.toString());
                writer.newLine(); // Ensure each object is written on a new line
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    // Write a list of objects to file (e.g., for Booking or Room objects)
    public static void writeObjectListToFile(String filePath, List<Object> objects) {
        List<String> lines = new ArrayList<>();
        for (Object obj : objects) {
            lines.add(obj.toString()); // Assuming the toString method formats the object as needed
        }
        writeFile(filePath, lines);
    }

    // Read a list of objects from a file
    public static List<Object> readObjectListFromFile(String filePath) {
        List<Object> objects = new ArrayList<>();
        List<String> lines = readFile(filePath);
        for (String line : lines) {
            // Assuming you have a method to parse the string back to objects
            Object obj = parseObjectFromString(line);
            if (obj != null) {
                objects.add(obj);
            }
        }
        return objects;
    }

    // Dummy method for parsing a string back into an object
    // This needs to be implemented for each type of object you're storing
    private static Object parseObjectFromString(String line) {
        // For example, if you're parsing a Booking object:
        String[] parts = line.split("#");
        if (parts.length == 9) {
            return new Booking(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]); // Custom parsing logic
        }
        // Implement parsing for other object types like Room
        return null;
    }

    public static void main(String[] args) {
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        String filePath = "./../database/bookinglist.txt";  // Adjust this path based on your directory structure

        // Read the file content
        List<String> lines = readFile(filePath);
        if (lines.isEmpty()) {
            System.out.println("No content read from file.");
        } else {
            System.out.println("File content:");
            for (String line : lines) {
                System.out.println(line);
            }
        }
    }
}
