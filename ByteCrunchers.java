package Team5;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ByteCrunchers {
    public static void main(String[] mainMenu) {
    	
    	List<String> wordList = new ArrayList<String>();
    	
    	System.out.println("Welcome to the Byte Crunchers Project Demo !!");
    	System.out.println("---------------------------------------------");
    	
    	// First the web site will be crawled and data will be generated into txt files
    	System.out.println("1. Web Site Crawling and HTML parsing\n");
    	Htmlparser_Webcrawler webcrawler = new Htmlparser_Webcrawler();
    	webcrawler.main(null);
    	System.out.println("\nWeb Site Crawling Completed.\n");
    	
    	// Second the files will be validated for the data properly
    	System.out.println("2. Data Validation Using Regex\n");
    	MenuDataValidator menuDataValidator = new MenuDataValidator();
    	menuDataValidator.main(null);
    	System.out.println("\nData Validated Properly.\n");
    	
    	// Third the user input is asked and the spell checker will check the spelling while the word completion will be
    	System.out.println("3. User Input validated with the Spell Checker\n");
    	SpellCheck spellCheck = new SpellCheck();
    	String correctedUserInput = spellCheck.main(null);
    	System.out.println("\n4. User Input validated with word completion\n");
    	System.out.println(correctedUserInput);
    	WordCompletionTrie wordCompletionTrie = new WordCompletionTrie();
    	wordCompletionTrie.main(new String[]{correctedUserInput});
    	System.out.println("\nThe user input has been gone throughly.");
    	
    	wordList.add(correctedUserInput);
    	
    	// fourth the user input will be matched
    	System.out.println("\n5. Pattern Finding using Regular Expression\n");
        RegexPatternMatching regPatternMatch = new RegexPatternMatching();
        String itemNameToSearch = regPatternMatch.main(null);
        System.out.println("\nThe item details are provided.\n");
        
        wordList.add(itemNameToSearch);
    	
    	System.out.println("6. Frequency Count\n");
    	FrequencyCount frequencyCount = new FrequencyCount();
    	String keyWord = frequencyCount.main(null);
    	System.out.println("\nThe frequency has been estimated.\n");
    	
    	wordList.add(keyWord);
    	
    	System.out.println("7. Inverted Indexing\n");
    	CafeInvertedIndex invertedIndex = new CafeInvertedIndex();
    	String keyword = invertedIndex.main(null);
    	System.out.println("\nInverted indexing has been done.\n");
    	
    	wordList.add(keyword);
    	
    	System.out.println("8. Page Ranking\n");
    	CafePageRank pageRank = new CafePageRank();
    	String searchKeyword = pageRank.main(null);
    	System.out.println("\nPages are been ranked.\n");
    	
    	wordList.add(searchKeyword);
    	
    	System.out.println("9. Search Frequency");
    	SearchFrequencyManager searchFrequencyManager = new SearchFrequencyManager();
    	Map<String, Integer> frequencyMap = SearchFrequencyManager.getSearchFrequency(wordList);

    	// Print the search frequencies for each word
    	for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
    	    System.out.println("\nSearch Frequency for '" + entry.getKey() + "': " + entry.getValue());
    	}
    	System.out.println("\nOn basis of search done the user input has been given an occurrence number.\n");
    	
    	System.out.println("-------------------------------------");
    	System.out.println("System is now exiting. See you later!");
    }
}