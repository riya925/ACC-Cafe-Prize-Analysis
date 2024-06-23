package Team5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// TrieNode represents a node in the Trie
class TrieNode {
    TrieNode[] children = new TrieNode[26];
    boolean isEndOfWord;
    String word;  // New field to store the full word
}

// Trie is the data structure for word completion
class Trie {
    private TrieNode root = new TrieNode();

    // Method to insert a word into the Trie
    public void insert(String word) {
        TrieNode node = root;
        for (char ch : word.toCharArray()) {
            if (ch < 'a' || ch > 'z') {
                // Skip non-lowercase letters
                continue;
            }
            int index = ch - 'a';
            if (node.children[index] == null) {
                node.children[index] = new TrieNode();
            }
            node = node.children[index];
        }
        node.isEndOfWord = true;
        node.word = word;  // Save the full word in the TrieNode
    }

    // Method to get words with a given prefix from the Trie
    public List<String> getWordsWithPrefix(String prefix) {
        List<String> result = new ArrayList<>();
        TrieNode currentNode = root;

        // Remove spaces between characters, preserve spaces at the end
        StringBuilder cleanedPrefix = new StringBuilder();
        boolean spaceFlag = false;

        for (char character : prefix.toCharArray()) {
            if (character == ' ' && !spaceFlag) {
                spaceFlag = true;
            } else if (character != ' ') {
                spaceFlag = false;
                cleanedPrefix.append(character);
            }
        }

        char[] characters = cleanedPrefix.toString().toCharArray();

        for (char character : characters) {
            int index = character - 'a';
            if (currentNode.children[index] == null) {
                return result;
            }
            currentNode = currentNode.children[index];
        }

        // Include all words with the given prefix in the result
        collectWords(currentNode, result);
        return result;
    }

    // Helper method to recursively collect words from Trie nodes
    private void collectWords(TrieNode node, List<String> result) {
        if (node.isEndOfWord) {
            result.add(node.word.trim());
        }

        for (TrieNode child : node.children) {
            if (child != null) {
                collectWords(child, result);
            }
        }
    }
}

// Main class for the Word Completion Trie application
public class WordCompletionTrie {
    private static Trie trie = new Trie();

    // Main method to run the Word Completion Trie application
    public static String main(String[] args) {
    	String prefix = null;
    	
    	if (args.length > 0) {
            prefix = args[0];
        } else {
            System.out.println("There is an issue in fetching the user input");
        }
    	
        // Read search frequency from the file
        SearchFrequencyManager.readSearchFrequencyFromFile();

        // Read words from three different files and insert item names and descriptions into the trie
        readWordsFromFile("validated_WebSite1.txt");
        readWordsFromFile("validated_WebSite2.txt");
        readWordsFromFile("validated_WebSite3.txt");

        // Take user input for menu item search
        //Scanner scanner = new Scanner(System.in);
//        System.out.print("Enter a prefix for menu item search: ");
//        String prefix = scanner.nextLine();
//        prefix = prefix.toLowerCase();

        // Update search frequency and get word completions for the user input
        updateSearchFrequency(prefix);

        // Get word completions for the user input
        List<String> completions = trie.getWordsWithPrefix(prefix);

        // Check if the entered prefix is present in completions (exact match)
        boolean isExactMatch = completions.contains(prefix.trim());
        if (completions.size() != 1) {
            isExactMatch = false;
        }

        if (!completions.isEmpty() && !isExactMatch) {
            // Display suggestions
            System.out.println("\nSuggestions: ");
            for (String completion : completions) {
                System.out.println(completion);
            }
        } else if (isExactMatch) {
            // The entered prefix is an exact match, no need to show suggestions
            System.out.println("Exact match found: " + prefix);
        } else {
            System.out.println("Searched item not found, please try again! ");
        }

        // Write search frequency to the file
        SearchFrequencyManager.writeSearchFrequencyToFile();
		return prefix;
    }

    // Method to read words from a file and insert them into the Trie
    private static void readWordsFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the line contains "Item Name:"
                if (line.contains("Item:")) {
                    // Extract words from the line (excluding "Item Name:")
                    String itemName = line.replaceAll("(Item:)", "").trim().toLowerCase();

                    // Insert the cleaned word into the trie
                    if (!itemName.isEmpty()) {
                        trie.insert(itemName);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to update the search frequency for a word
    private static void updateSearchFrequency(String word) {
        SearchFrequencyManager.updateSearchFrequency(word);
    }
}