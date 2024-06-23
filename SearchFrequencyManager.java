package Team5;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Class to manage search frequency data
public class SearchFrequencyManager {
    private static Map<String, Integer> searchFrequency = new HashMap<>();
    private static final String SEARCH_FREQUENCY_FILE = "search_frequency.txt";

    // Method to update the search frequency for a word
    public static void updateSearchFrequency(String word) {
        searchFrequency.put(word, searchFrequency.getOrDefault(word, 0) + 1);
    }

    // Method to read search frequency data from a file
    public static void readSearchFrequencyFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SEARCH_FREQUENCY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String word = parts[0].trim();
                    int frequency = Integer.parseInt(parts[1].trim());
                    searchFrequency.put(word, frequency);
                }
            }
        } catch (IOException | NumberFormatException e) {
            // Handle exceptions
            e.printStackTrace();
        }
    }

    // Method to write search frequency data to a file
    public static void writeSearchFrequencyToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SEARCH_FREQUENCY_FILE))) {
            for (Map.Entry<String, Integer> entry : searchFrequency.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            // Handle exceptions
            e.printStackTrace();
        }
    }

    // Method to get search frequency for a word
    public static int getSearchFrequency(String word) {
        return searchFrequency.getOrDefault(word, 0);
    }

    // Method to get search frequency for a list of words
    public static Map<String, Integer> getSearchFrequency(List<String> words) {
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (String word : words) {
            frequencyMap.put(word, getSearchFrequency(word));
        }
        return frequencyMap;
    }
}