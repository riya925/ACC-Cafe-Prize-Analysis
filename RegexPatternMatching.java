// required package and imported all the library used in the code
package Team5;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexPatternMatching {
    public static String main(String[] mainValues) {
    	Scanner userScanner = new Scanner(System.in);
    	
    	// Read search frequency from the file    -----> at beginning of the main
        SearchFrequencyManager.readSearchFrequencyFromFile();

    	// Flag variable to track whether any match is there or not in the singleFile
        boolean checkIfMatchFound = false;
        String itemNameToSearch = null;
    	
    	try {
    		// full file folder for the txt files
            //String webPageFilePath = "C:\\Users\\admin\\eclipse-workspace\\Team 5_Cafe Price Analysis";
            // folder input
            //File webPageFolder = new File(webPageFilePath);

            // check for the file and folder exists or not
            //if (webPageFolder.exists() && webPageFolder.isDirectory()) {
            	// Make that the directory, not the file, is pointed to by the fileDict variable.
//            	if (!webPageFolder.isDirectory()) {
//            	    webPageFolder = webPageFolder.getParentFile();
//            	}
            	
                // file list
            	//File[] webPageFileList = webPageFolder.listFiles();
    		
    			String[] webPageFileList = {"validated_WebSite1.txt", "validated_WebSite2.txt", "validated_WebSite3.txt"};
                
            	System.out.print("Type in the term you're looking for information on: ");
            	itemNameToSearch = userScanner.nextLine();
                while (itemNameToSearch.isEmpty() || itemNameToSearch == null || !itemNameToSearch.matches("[a-z A-Z]+")){
                	// handling the empty input case
                	System.out.print("Kindly enter a legitimate input: ");
                    itemNameToSearch = userScanner.nextLine();
                }
                
                // Update search frequency and get word completions for the user input  ---->  prefix -- word entered by user
                SearchFrequencyManager.updateSearchFrequency(itemNameToSearch);
                
                // fetching all the files
                for (String singleFile : webPageFileList) {
                	if(!singleFile.endsWith(".txt")) {
                		continue;
                	}
                	try (BufferedReader bfReaderWebPage = new BufferedReader(new FileReader(singleFile))) {
                		String inputLine;
                		
                		// Variable to track whether any match is there or not in the singleFile
                        boolean checkIfMatchFoundInFile = false;
                        
                		while ((inputLine = bfReaderWebPage.readLine()) != null) {
                			// Regex is used to match the object name.
                			String itemNameRegex = "Item:\\s*" + Pattern.quote(itemNameToSearch);
                			Pattern regPattern = Pattern.compile(itemNameRegex,Pattern.CASE_INSENSITIVE);
                			Matcher regMatcher = regPattern.matcher(inputLine);

                			// if match found then fetch the corresponding data
                			if (regMatcher.find()) {
                				System.out.println("Price and Description for: " + regMatcher.group());
								String firstLine = bfReaderWebPage.readLine();
								String secondLine = bfReaderWebPage.readLine();
								
                				// If the item name is found, extract item price and item description
                				String priceRegPattern = "Price:\\s*\\$*([\\d.]+)";
                				String descRegexPattern = "Description:\\s*(.*)";
                        
                				Pattern pricePattern = Pattern.compile(priceRegPattern);
                				Pattern descriptionPattern = Pattern.compile(descRegexPattern);
                        
                				// checking if we have price pattern matching
                				Matcher priceRegMatcher = pricePattern.matcher(firstLine);
                				if (priceRegMatcher.find()) {
                		            String itemPrice = priceRegMatcher.group(1);
                		            System.out.println("Price: $" + itemPrice);
                		        } else {
                		            System.out.println("The price for an item is missing from the"+ singleFile +" file.");
                		        }
                				
                				// checking if we have price pattern matching
                				Matcher descRegMatcher = descriptionPattern.matcher(secondLine);
                				if (descRegMatcher.find()) {
                		            String itemDescription = descRegMatcher.group(1);
                		            System.out.println("Item Description: " + itemDescription);
                		        } else {
                		            System.out.println("Item Description not found in the"+ singleFile +"file.");
                		        }

                				checkIfMatchFound = true;
                				checkIfMatchFoundInFile = true;
                				break; // Exit the loop after the first match in the file
                			}
                		}
                		
                		// If the item is found in the file, no need to continue checking other lines
                        if (checkIfMatchFoundInFile) {
                            break;
                        }
                	}
                }
                
                // if the item is found or not and if not then print it
                if (!checkIfMatchFound) {
                    System.out.println("Not detected element: " + itemNameToSearch);
                }
//          }
//            else {
//            	System.err.println("Incorrect folder location: " + webPageFilePath);
//            }
        } catch (IOException exReg) {
        	exReg.printStackTrace();
        } catch (Exception exReg) {
        	exReg.printStackTrace();
        } finally {
//        	if(userScanner != null) {
//        		userScanner.close();
//        	}
        }
    	
    	// Write search frequency to the file  ----> before main method
        SearchFrequencyManager.writeSearchFrequencyToFile();

    	return itemNameToSearch;
    }
}