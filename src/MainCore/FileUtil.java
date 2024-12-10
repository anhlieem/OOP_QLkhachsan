package MainCore;

import java.io.*;
import java.util.*;

public class FileUtil {

    /**
     * Reads lines from a file and returns them as a List of Strings.
     *
     * @param filePath the path to the file
     * @return a List of Strings containing the file's contents line by line
     */
    public static List<String> readFile(String filePath) {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);

        // Check if the file exists
        if (!file.exists()) {
            System.err.println("File does not exist: " + file.getAbsolutePath());
            return lines;
        }

        // Read the file line by line
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + file.getAbsolutePath());
            e.printStackTrace();
        }

        return lines;
    }

    /**
     * Writes a list of data to a file, each object on a new line.
     *
     * @param filePath the path to the file
     * @param data     the data to be written to the file
     */
    public static void writeFile(String filePath, List<?> data) {
        File file = new File(filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Object obj : data) {
                writer.write(obj.toString());
                writer.newLine();
            }
            System.out.println("Data successfully written to file: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error writing to file: " + file.getAbsolutePath());
            e.printStackTrace();
        }
    }
}
