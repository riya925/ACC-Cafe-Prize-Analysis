package Team5;

import java.io.*;
import java.util.*;

public class CafeInvertedIndex {
    private static class TrieNode {
        // Holds occurrences of keywords in each website (file)
        Map<String, Map<Integer, List<Integer>>> websiteData = new HashMap<>();
        // Child nodes for each character in keywords
        Map<Character, TrieNode> childrenNodes = new HashMap<>();
    }

    private TrieNode root = new TrieNode(); // Root of the Trie

    // Initiates indexing of cafe menu items from HTML files in the specified directory
    public String startMenuIndexing(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Invalid cafe-related files directory.");
        }
        processMenuFiles(directory.listFiles()); // Process files to build the inverted index
        return searchUserInputKeyword(); // Initiate user input for keyword search and return the keyword
    }

    // Processes HTML files to build the inverted index
    private void processMenuFiles(File[] files) {
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                	try{
                		BufferedReader br = new BufferedReader(new FileReader(file));
                        String line;
                        int position = 0;
                        while ((line = br.readLine()) != null) {
                            // Extract individual words from each line and add to the inverted index
                            String[] words = line.split("\\s+"); // Split line into words
                            for (String word : words) {
                                addToIndex(root, word.toLowerCase(), file.getName(), position++);
                                // Add word positions to the index
                            }
                        }
                        br.close();
                    } catch (IOException e) {
                        System.err.println("Error reading the file: " + file.getAbsolutePath());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // Adds a word and its position to the inverted index
    private void addToIndex(TrieNode node, String word, String website, int position) {
        for (char c : word.toCharArray()) {
            node = node.childrenNodes.computeIfAbsent(c, k -> new TrieNode());
            // Traverse through Trie nodes for each character in the word
        }
        // Store word positions for each website in the inverted index
        node.websiteData.computeIfAbsent(website, k -> new HashMap<>())
                .computeIfAbsent(position, k -> new ArrayList<>())
                .add(position);
    }

    // Accepts user input for a keyword and initiates search in the index
    private String searchUserInputKeyword() {
    	Scanner scanner = new Scanner(System.in);
        try{
            System.out.print("Enter a keyword to search in the cafe menu: ");
            String retValue = scanner.nextLine().trim();
            return retValue; // Return the keyword entered by the user
        } catch (Exception e) {
            e.printStackTrace();
            return ""; // Return an empty string in case of an exception
        }
    }

    // Searches for the keyword in the inverted index and returns results as a string
    private String searchForKeyword(String keyword) {
        TrieNode node = root;
        for (char c : keyword.toLowerCase().toCharArray()) {
            node = node.childrenNodes.get(c); // Traverse Trie nodes for each character in keyword
            if (node == null) {
                return "No cafe menu items found for the keyword '" + keyword + "'.";
            }
        }
        if (node.websiteData.isEmpty()) {
            return "No cafe menu items found for the keyword '" + keyword + "'.";
        }
        return getFoundItemsAsString(node.websiteData); // Return found keyword occurrences as a string
    }

    // Displays web pages containing the keyword in the cafe menu
    private String getFoundItemsAsString(Map<String, Map<Integer, List<Integer>>> websites) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Map<Integer, List<Integer>>> entry : websites.entrySet()) {
            String website = entry.getKey();
            Map<Integer, List<Integer>> positions = entry.getValue();
            int totalFrequency = positions.values().stream().mapToInt(List::size).sum();
            result.append("Cafe item found at: ").append(website).append(" | Total Frequency: ").append(totalFrequency).append("\n");

            for (Map.Entry<Integer, List<Integer>> posEntry : positions.entrySet()) {
                int position = posEntry.getKey();
                int frequency = posEntry.getValue().size();
                result.append("           File: ").append(website).append(", Position: ").append(position).append("\n");
            }
        }
        return result.toString();
    }

    public static String main(String[] args) {
        CafeInvertedIndex index = new CafeInvertedIndex();
        String directoryPath = "C:\\Users\\admin\\eclipse-workspace\\Team 5_Cafe Price Analysis\\TextFiles";
        String keyword = null;
        
        // Read search frequency from the file    -----> at beginning of the main
        SearchFrequencyManager.readSearchFrequencyFromFile();

        try {
            keyword = index.startMenuIndexing(directoryPath);
            //System.out.println("User entered keyword: " + keyword);
            
            // Update search frequency and get word completions for the user input  ---->  prefix -- word entered by user
            SearchFrequencyManager.updateSearchFrequency(keyword);

            String foundItems = index.searchForKeyword(keyword);
            System.out.println(foundItems); // Display or use the found items string
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Write search frequency to the file  ----> before main method
        SearchFrequencyManager.writeSearchFrequencyToFile();
        
        return keyword;
    }
}