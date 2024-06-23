package Team5;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * SpellCheck class for checking and correcting user input spelling.
 */
public class SpellCheck {

    private static Trie vocabularyTrie = new Trie();

    /**
     * Main method to run the spell check application.
     * @param commandLineArguments Command line arguments (not used).
     * @return Corrected user input or an error message.
     */
    public static String main(String[] commandLineArguments) {
        // Read search frequency from the file
        SearchFrequencyManager.readSearchFrequencyFromFile();

        // File paths for validated menu data
        String validatedMenuFilePath1 = "validated_WebSite1.txt";
        String validatedMenuFilePath2 = "validated_WebSite2.txt";
        String validatedMenuFilePath3 = "validated_WebSite3.txt";

        // Check if file paths are valid
        if (!areFilePathsValid(validatedMenuFilePath1, validatedMenuFilePath2, validatedMenuFilePath3)) {
            return "Error reading or processing files. Exiting program.";
        }

        // Create vocabulary from validated menu data
        createVocabulary(validatedMenuFilePath1);
        createVocabulary(validatedMenuFilePath2);
        createVocabulary(validatedMenuFilePath3);

        // User input
        System.out.println("What would you like to order?");
        Scanner userInputScanner = new Scanner(System.in);
        String userTypedInput = userInputScanner.nextLine();

        // Validate user input
        while (userTypedInput == null || userTypedInput.trim().equalsIgnoreCase("null") || userTypedInput.trim().isEmpty() || !userTypedInput.matches("[a-zA-Z]+")) {
            System.out.println("Please enter a valid input:");
            userTypedInput = userInputScanner.nextLine(); // Reassign the input
        }

        // Run spell check on user input
        String correctedUserInput = spellCheckUserInput(userTypedInput.toLowerCase());

        // Update search frequency and get word completions for the user input
        updateSearchFrequency(correctedUserInput);

        // Write search frequency to the file
        SearchFrequencyManager.writeSearchFrequencyToFile();
        //userInputScanner.close();
        return correctedUserInput;
    }

    /**
     * Method to check if file paths are valid.
     * @param filePaths File paths to be checked.
     * @return True if all file paths are valid, false otherwise.
     */
    private static boolean areFilePathsValid(String... filePaths) {
        for (String filePath : filePaths) {
            if (!filePath.equals("validated_WebSite1.txt") &&
                    !filePath.equals("validated_WebSite2.txt") &&
                    !filePath.equals("validated_WebSite3.txt")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method to read file content.
     * @param filePath Path of the file to be read.
     * @return Content of the file as a string.
     * @throws IOException If an I/O error occurs.
     */
    private static String readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    /**
     * Method to create vocabulary from a file.
     * @param filePath Path of the file containing menu data.
     */
    private static void createVocabulary(String filePath) {
        try {
            String menuText = readFile(filePath);
            extractWordsAndBuildVocabulary(menuText);
        } catch (IOException exception) {
            System.err.println("Error reading or processing files: " + exception.getMessage());
        }
    }

    /**
     * Method to extract words from text and build vocabulary.
     * @param menuText Text containing menu data.
     */
    private static void extractWordsAndBuildVocabulary(String menuText) {
        String[] words = menuText.split("\\s+|\\p{Punct}");
        for (String word : words) {
            if (!word.isEmpty() && !word.matches("\\d+")) {
                vocabularyTrie.insert(word.toLowerCase());
            }
        }
    }

    /**
     * Method to run spell check on user input.
     * @param userInput User input to be spell-checked.
     * @return Corrected user input.
     */
    public static String spellCheckUserInput(String userInput) {
        StringBuilder correctedUserInput = new StringBuilder();

        String[] wordsTypedByUser = Arrays.stream(userInput.split("\\s+|\\p{Punct}"))
                .filter(word -> !word.isEmpty())
                .toArray(String[]::new);

        // Iterate over words in user input
        for (String word : wordsTypedByUser) {
            word = word.replace("\n", "").toLowerCase();

            if (!vocabularyTrie.search(word)) {
                // If word is not in vocabulary, suggest corrections
                Set<String> suggestionsForWord = findSuggestionsForWord(word);
                if (suggestionsForWord.isEmpty()) {
                    System.out.println("No suggestions found for '" + word + "'.");
                    correctedUserInput.append(word).append(" ");
                } else {
                    String firstSuggestion = suggestionsForWord.iterator().next();
                    System.out.println("Did you mean for '" + word + "': " + firstSuggestion + "? (yes/no)");

                    Scanner inputScanner = new Scanner(System.in);
                    String userResponse = inputScanner.nextLine().toLowerCase();

                    if (userResponse.equals("yes")) {
                        // Replace the misspelled word with the suggestion
                        System.out.println("Correction applied: " + firstSuggestion);
                        correctedUserInput.append(firstSuggestion).append(" ");
                    } else {
                        System.out.println("No correction applied for '" + word + "'.");
                        correctedUserInput.append(word).append(" ");
                    }
                    
                    //inputScanner.close();
                }
            } else {
                // Word is correctly spelled
                System.out.println("No corrections needed for '" + word + "'.");
                correctedUserInput.append(word).append(" ");
            }
        }

        // System.out.println(correctedUserInput);
        return correctedUserInput.toString().trim();
    }

    /**
     * Method to find suggestions for a word.
     * @param input Word for which suggestions are needed.
     * @return Set of suggested words.
     */
    private static Set<String> findSuggestionsForWord(String input) {
        Set<String> wordSuggestions = new HashSet<>();
        int maximumDistance = 2;

        List<String> allWordsInVocabulary = vocabularyTrie.getAllWords();

        // Iterate over all words in vocabulary
        for (String vocabWord : allWordsInVocabulary) {
            int distance = calculateEditDistance(input, vocabWord);
            if (distance <= maximumDistance) {
                wordSuggestions.add(vocabWord);
            }
        }

        return wordSuggestions;
    }

    /**
     * Trie data structure for efficient word lookup.
     */
    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord;
    }

    /**
     * Trie class for storing and searching words efficiently.
     */
    static class Trie {
        TrieNode root = new TrieNode();

        /**
         * Method to insert a word into the trie.
         * @param word Word to be inserted.
         */
        void insert(String word) {
            TrieNode node = root;
            for (char character : word.toCharArray()) {
                node.children.putIfAbsent(character, new TrieNode());
                node = node.children.get(character);
            }
            node.isEndOfWord = true;
        }

        /**
         * Method to search for a word in the trie.
         * @param word Word to be searched.
         * @return True if the word is found, false otherwise.
         */
        boolean search(String word) {
            TrieNode node = root;
            for (char character : word.toCharArray()) {
                if (!node.children.containsKey(character)) {
                    return false;
                }
                node = node.children.get(character);
            }
            return node.isEndOfWord;
        }

        /**
         * Method to get all words in the trie.
         * @return List of all words in the trie.
         */
        List<String> getAllWords() {
            List<String> wordsList = new ArrayList<>();
            collectAllWords(root, "", wordsList);
            return wordsList;
        }

        /**
         * Helper method to collect all words in the trie.
         * @param node Current node in the trie.
         * @param currentWord Current word being formed.
         * @param wordsList List to store all words.
         */
        private void collectAllWords(TrieNode node, String currentWord, List<String> wordsList) {
            if (node.isEndOfWord) {
                wordsList.add(currentWord);
            }

            for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
                collectAllWords(entry.getValue(), currentWord + entry.getKey(), wordsList);
            }
        }
    }

    /**
     * Method to calculate the edit distance between two words.
     * @param target Target word.
     * @param input Input word.
     * @return Edit distance between the two words.
     */
    private static int calculateEditDistance(String target, String input) {
        int[][] distanceMatrix = new int[target.length() + 1][input.length() + 1];

        // Fill the distance matrix
        for (int i = 0; i <= target.length(); i++) {
            for (int j = 0; j <= input.length(); j++) {
                if (i == 0) {
                    distanceMatrix[i][j] = j;
                } else if (j == 0) {
                    distanceMatrix[i][j] = i;
                } else {
                    int cost = (Character.toLowerCase(target.charAt(i - 1)) == Character.toLowerCase(input.charAt(j - 1))) ? 0 : 1;
                    distanceMatrix[i][j] = Math.min(Math.min(distanceMatrix[i - 1][j] + 1, distanceMatrix[i][j - 1] + 1), distanceMatrix[i - 1][j - 1] + cost);
                }
            }
        }

        return distanceMatrix[target.length()][input.length()];
    }

    /**
     * Method to update the search frequency for a word.
     * @param word Word for which search frequency needs to be updated.
     */
    private static void updateSearchFrequency(String word) {
        SearchFrequencyManager.updateSearchFrequency(word);
    }
}
