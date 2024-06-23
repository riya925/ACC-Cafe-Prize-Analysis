package Team5;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MenuDataValidator class for validating and processing menu data from different websites.
 */
public class MenuDataValidator {

    /**
     * Main method to validate and process menu data files.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        String[] fileNames = {"WebSite1.txt", "WebSite2.txt", "WebSite3.txt"};

        for (String fileName : fileNames) {
            // Process each file
            processFile(fileName);
        }
    }

    /**
     * Method to process a menu data file.
     * @param fileName Name of the menu data file to be processed.
     */
    private static void processFile(String fileName) {
        if (fileName.equals("WebSite1.txt") || fileName.equals("WebSite2.txt") || fileName.equals("WebSite3.txt")) {
            System.out.println("Validating " + fileName + "...");
            // Process menu data for WebSite1, WebSite2, and WebSite3
            processMenuData1(fileName);
        } else {
            System.out.println("Invalid file name: " + fileName);
        }
    }

    /**
     * Method to process menu data for WebSite1, WebSite2, and WebSite3.
     * @param fileName Name of the menu data file to be processed.
     */
    private static void processMenuData1(String fileName) {
        // Validate and write processed data to a new file
        validateAndWriteToFile(fileName, "validated_" + fileName, (menuData, writer) -> {
            Pattern categoryPattern = Pattern.compile("\\*{20} Category: (.+?) \\*{20}");
            Pattern itemPattern = Pattern.compile("Item: ([^\\n]+)\\s*Price: (\\$\\d+\\.\\d+)\\s*Description: ([^\\n]+)\\s*-+");

            Matcher categoryMatcher = categoryPattern.matcher(menuData);
            Matcher itemMatcher;

            while (categoryMatcher.find()) {
                String category = categoryMatcher.group(1);

                itemMatcher = itemPattern.matcher(menuData);

                while (itemMatcher.find()) {
                    String itemName = itemMatcher.group(1);
                    String itemPrice = itemMatcher.group(2);
                    String itemDescription = itemMatcher.group(3);

                    if (itemPrice.isEmpty() || !itemPrice.startsWith("$")) {
                        System.out.println("Invalid price: " + itemPrice + " for item: " + itemName);
                    }

                    // Write processed data to the output file
                    writer.println("Item: " + itemName);
                    writer.println("Price: " + itemPrice);
                    writer.println("Description: " + itemDescription);
                    writer.println("----------------------------");
                }
            }
        });
    }

    /**
     * Method to validate and write processed data to a new file.
     * @param inputFileName Name of the input file containing menu data.
     * @param outputFileName Name of the output file for validated menu data.
     * @param processor DataProcessor interface for processing menu data.
     */
    private static void validateAndWriteToFile(String inputFileName, String outputFileName, DataProcessor processor) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
             PrintWriter writer = new PrintWriter(new FileWriter(outputFileName))) {

            StringBuilder menuData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                menuData.append(line).append("\n");
            }

            // Process menu data using the specified DataProcessor
            processor.process(menuData.toString(), writer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * DataProcessor interface for processing menu data.
     */
    interface DataProcessor {
        void process(String menuData, PrintWriter writer);
    }
}
