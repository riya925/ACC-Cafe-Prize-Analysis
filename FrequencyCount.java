// import package and library required
package Team5;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// this class will find the occurrence count for the
public class FrequencyCount {
    public static String main(String[] mainValue) {
    	// asking user for keyWord
    	Scanner userInputScanner = new Scanner(System.in);
    	
    	// Read search frequency from the file    -----> at beginning of the main
        SearchFrequencyManager.readSearchFrequencyFromFile();
        
    	String keyWord = null;
    	
        try {
        	// the file where the data is generated from the web site crawling
        	//String webCrawledFilePath = "C:\\Users\\admin\\eclipse-workspace\\Team 5_Cafe Price Analysis";
        	// the local directory where the web pages are stored
            //File fileDict = new File(webCrawledFilePath);
            
            // Validating the file path and the fileDict in which the web sites files are placed
            //if (fileDict.exists() || fileDict.isDirectory()) {
            	// Make that the directory, not the file, is pointed to by the fileDict variable.
//            	if (!fileDict.isDirectory()) {
//            	    fileDict = fileDict.getParentFile();
//            	}
            	
            	// obtaining every file from the directory
                //File[] filesList = fileDict.listFiles();
        		
        		String[] filesList = {"validated_WebSite1.txt", "validated_WebSite2.txt", "validated_WebSite3.txt"};
                
                // list of keywords which needs to be found having frequency count
                List<String> userInputs = new ArrayList<String>();
                // checking if we have keyword and not kept empty
                while (userInputs.isEmpty()) {
                	// user input
                    System.out.print("Please tell me the keyword here, separated by a comma: ");
                    keyWord = userInputScanner.nextLine();
                    
                    // adding the keywords to a list and splitting it with comma
                    for (String kw : keyWord.split(",")) {
                        userInputs.add(kw.trim().toLowerCase());
                    }
                    
                    // handling the empty input case
                    if (userInputs.isEmpty()) {
                        System.out.println("Kindly input a minimum of one keyword.");
                    }
                }
                //System.out.println("Keywords: "+ userInputs);
                
                // Count the occurrences of the target word
                Map<String, Integer> wordFrequencyCount = countWordFrequency(filesList, userInputs);
                
                // To print the frequencies based on keyword matches with the web page data
                System.out.println("\nFrequencies of webpages according to keyword matching: \n");
                // fetching the frequency count for all the files
                for (String wfc : wordFrequencyCount.keySet()) {
                	// fetching the count
                    int frequencyCount = wordFrequencyCount.get(wfc);
                    // checking the frequency count should be greater than 0
                    if (frequencyCount > 0) {
                        System.out.println(wfc + " - The frequency is: " + frequencyCount);
                    }
                    else {
                    	System.out.println(wfc + " - The frequency is: " + frequencyCount);
                    }
                }
//            }
//            else {
//            	System.err.println("The provided directory path is invalid: " + webCrawledFilePath);
//            }
        } catch (Exception exfc) {
            // Handle file reading or other I/O exceptions
            System.err.println("Error reading file: " + exfc.getMessage());
        } finally {
            // Closing the userInputScanner if it is not null then
//            if (userInputScanner != null) {
//                userInputScanner.close();
//            }
        }
        
        // Update search frequency and get word completions for the user input  ---->  prefix -- word entered by user
        SearchFrequencyManager.updateSearchFrequency(keyWord);
        
        return keyWord;
    }

    // his method will count the word frequency in the website
    private static Map<String, Integer> countWordFrequency(String[] filesList, List<String> userInputs) {
    	Map<String, Integer> wordFrequencyCount = new HashMap<>();

        // Initially set each web page's frequency to 0
        for (String perFile : filesList) {
            // Checking if the file is in .txt format only
            if (perFile.endsWith(".txt")) {
                wordFrequencyCount.put(perFile, 0);
            }
        }
        
        // To iterate over all of the web page data produced in the file
        for (String indFile : filesList) {
        	int freqCountVar = 0;
            if (indFile.endsWith(".txt")) {
                // reading all the contents of the file
                try (BufferedReader bfReaderfc = new BufferedReader(new FileReader(indFile))) {
                    String singleLine;
                    // reading every single line of the file
                    while ((singleLine = bfReaderfc.readLine()) != null) {
                    	// going through the list of user inputs
                    	for(String word: userInputs) {
                    		// pattern is created to match it with the file data
                            Pattern patternToMatch = Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE);
                            // matcher will find the word in the file
                            Matcher matcherOutput = patternToMatch.matcher(singleLine);
                            while(matcherOutput.find()) {
                            	freqCountVar++;
                            }
                        }
                    }
                    wordFrequencyCount.put(indFile, freqCountVar);
                } catch (Exception exfc) {
                	exfc.printStackTrace();
                }
            }
        }
        
        // Write search frequency to the file  ----> before main method
        SearchFrequencyManager.writeSearchFrequencyToFile();
        
        return wordFrequencyCount;
    }
}