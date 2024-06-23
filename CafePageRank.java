package Team5;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CafePageRank {
    private PriorityQueue<CafePage> pageQueue;
    private Map<String, Integer> wordFrequency;

    public CafePageRank() {
        pageQueue = new PriorityQueue<>();
    }

    public static String main(String[] args) {
        CafePageRank rank = new CafePageRank();
        Scanner scanner = new Scanner(System.in);
        
        // Read search frequency from the file    -----> at beginning of the main
        SearchFrequencyManager.readSearchFrequencyFromFile();

        String directoryPath = "C:\\Users\\admin\\eclipse-workspace\\Team 5_Cafe Price Analysis\\TextFiles";
        String searchKeyword = getSearchKeyword(scanner);
        System.out.println("Keyword/item entered by user: " + searchKeyword);
        
        // Update search frequency and get word completions for the user input  ---->  prefix -- word entered by user
        SearchFrequencyManager.updateSearchFrequency(searchKeyword);

        rank.readHtmlFiles(directoryPath, searchKeyword);

        // Generate page ranks as a string and display it
        String pageRanks = rank.generatePageRanksAsString(searchKeyword);
        System.out.println(pageRanks);
        
        // Write search frequency to the file  ----> before main method
        SearchFrequencyManager.writeSearchFrequencyToFile();

        //scanner.close();
        return searchKeyword;
    }

    private static String getSearchKeyword(Scanner scanner) {
        String searchKeyword = "";
        while (searchKeyword.isEmpty()) {
            System.out.print("Please enter a keyword to search for cafe-related content: ");
            searchKeyword = scanner.nextLine().trim().toLowerCase();
            scanner.reset();
            if (searchKeyword.isEmpty()) {
                System.err.println("Invalid input. Please enter a valid keyword.");
            }
        }
        return searchKeyword;
    }

    public void readHtmlFiles(String folderPath, String searchKeyword) {
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Invalid folder or directory.");
            return;
        }

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    Map<String, Integer> frequencies = extractWordFrequencies(file);

                    pageQueue.offer(new CafePage(file.getName(), frequencies));
                }
            }
        }
    }

    public Map<String, Integer> extractWordFrequencies(File file) {
        wordFrequency = new HashMap<>();

        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            String[] words = content.toLowerCase().split("\\W+");

            for (String word : words) {
                if (word.length() > 2) {
                    wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading content from file: " + file.getAbsolutePath());
            e.printStackTrace();
        }

        return wordFrequency;
    }

    public String generatePageRanksAsString(String keyword) {
        StringBuilder result = new StringBuilder();
        int count = 0;
        boolean foundPages = false;
        PriorityQueue<CafePage> filteredQueue = new PriorityQueue<>(pageQueue);

        while (!filteredQueue.isEmpty() && count < 10) {
            CafePage page = filteredQueue.poll();
            if (page.wordFrequency.containsKey(keyword)) {
                count++;
                foundPages = true;
                result.append("Rank ")
                      .append(count)
                      .append(" for ")
                      .append(page.fileName)
                      .append(" -> ")
                      .append(page.wordFrequency.get(keyword))
                      .append(" occurrences\n");
            }
        }

        if (!foundPages && !keyword.isEmpty()) {
            result.append("No cafe web pages found for the keyword '")
                  .append(keyword)
                  .append("'.");
        }

        return result.toString();
    }

    private static class CafePage implements Comparable<CafePage> {
        private String fileName;
        private Map<String, Integer> wordFrequency;

        public CafePage(String fileName, Map<String, Integer> wordFrequency) {
            this.fileName = fileName;
            this.wordFrequency = wordFrequency;
        }

        public int getTotalKeywordFrequency() {
            return wordFrequency.values().stream().mapToInt(Integer::intValue).sum();
        }

        @Override
        public int compareTo(CafePage other) {
            return Integer.compare(other.getTotalKeywordFrequency(), this.getTotalKeywordFrequency());
        }
    }
}
