package Team5;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.jsoup.select.Elements;

class MenuItem {
    private String name;
    private String price;
    private String description;

    public MenuItem(String name, String price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }
}

//Represents a category in the menu containing multiple menu items
class MenuCategory {
    private String categoryName;
    private List<MenuItem> items;

    public MenuCategory(String categoryName) {
        this.categoryName = categoryName;
        this.items = new ArrayList<>();
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public void addItem(MenuItem item) {
        this.items.add(item);
    }
}

//Represents the entire menu with multiple categories
 class Menu {
    private List<MenuCategory> categories;

    public Menu() {
        this.categories = new ArrayList<>();
    }

    public List<MenuCategory> getCategories() {
        return categories;
    }

    public void addCategory(MenuCategory category) {
        this.categories.add(category);
    }
}
 
public class Htmlparser_Webcrawler {
	
	private static final int MAX_URLS_TO_VISIT = 5;
	
	   public static void main(String[] args) {
		   String chromeDriverPath = "C:/Users/admin/Desktop/First Sem/ACC/Assignments/Assignment 3/chromedriver-win64/chromedriver.exe";
		   
	        // Set the path to the chromedriver executable
	        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

	        // Create a new instance of the Chrome driver
	        WebDriver driver = new ChromeDriver();	       

	        try {
	            // calling 3 methods for 3 different websites you want to scrape
	             scrapeWebsite1(driver, "http://www.agratandoori.ca/");
	             scrapeWebsite2(driver, "https://salambombay.com/");
	             scrapeWebsite3(driver, "http://www.indiacafe.ca/");
	        } finally {
	            // Close the browser
	            driver.quit();
	        }
	    }

	   // breadth first search method
	   private static void webCrawlSite(String startUrl) {
		    Queue<String> queue = new LinkedList<>();
		    Set<String> visited = new HashSet<>();

		    queue.add(startUrl);

		    int urlsVisited = 0;

		    while (!queue.isEmpty() && urlsVisited < MAX_URLS_TO_VISIT) {
		        String currentUrl = queue.poll();

		        // Check if the URL is a valid HTTP or HTTPS URL
		        if (!currentUrl.startsWith("http://") && !currentUrl.startsWith("https://")) {
		            System.out.println("Skipping invalid URL: " + currentUrl);
		            continue;
		        }

		        if (!visited.contains(currentUrl)) {
		            System.out.println("Visiting: " + currentUrl);
		            visited.add(currentUrl);
		            urlsVisited++;

		            try {
		                Document doc = Jsoup.connect(currentUrl).get();
		                Elements links = doc.select("a[href]");

		                for (Element link : links) {
		                    String nextUrl = link.absUrl("href");
		                    queue.add(nextUrl);
		                }
		            } catch (IOException e) {
		                e.printStackTrace(); // Handle exception appropriately
		            }
		        }
		    }
		}


	    
	   
	    // Scrape and extract data from the first website
	   private static void scrapeWebsite3(WebDriver driver, String url) {
	        // Code for website 3
		   
		   			 String chromeDriverPath = "C:/Users/admin/Desktop/First Sem/ACC/Assignments/Assignment 3/chromedriver-win64/chromedriver.exe";
	    	         String startingUrl = url;
	    	         driver.get(url);
	    	         webCrawlSite(url);
	    	         HashSet<String> visitedUrls = new HashSet<String>();
	    	         visitedUrls.add(startingUrl);
	    	    	 System.setProperty("webdriver.chrome.driver", chromeDriverPath);
	    	    	 
	    	        Menu menu = new Menu();

	    	        try {

	    	         	WebElement selectMenu = driver.findElement(By.cssSelector("#nav-header-right a"));
	    	          String href = selectMenu.getAttribute("href");
	    	          visitedUrls.add(href);
	    	          selectMenu.click();
	    	         
	    	          
	    	          // Create a FileWriter and BufferedWriter to write to a text file
	    	          FileWriter fileWriter = new FileWriter("WebSite3.txt");
	    	          BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

	    	          // Jsoup: Parse the HTML content
	    	          Document document = Jsoup.parse(driver.getPageSource());
	    	         	
	    	            // Process categories in section 1
	    	            processCategories(menu, document, "div.menu-cat-pos-1",bufferedWriter);

	    	            // Process categories in section 2
	    	            processCategories(menu, document, "div.menu-cat-pos-2",bufferedWriter);

	    	            // Close the FileWriter and BufferedWriter
	    	            bufferedWriter.close();
	    	            fileWriter.close();
	    	        } catch (IOException e) {
	    	            e.printStackTrace();
	    	        } finally {
	    	            // Close the browser
	    	            driver.quit();
	    	        }
	    	     
	    	        for (String urls : visitedUrls) {
	    	            System.out.println("Visiting: "+urls);
	    	        }
	    	        // Now, 'menu' contains the extracted data in a structured format
	    	    }

	 // Process menu categories and items for the third website
	    	    private static void processCategories(Menu menu, Document document, String sectionSelector, BufferedWriter bufferedWriter) throws IOException {
	    	     
	    	      Element section = document.select(sectionSelector).first();
	    	      List<Element> categories = section.select("div.menu-categories");

	    	      for (Element category : categories) {
	    	          String categoryName = category.select("div.menu-category-title").text();
	    	          bufferedWriter.write("\n******************** Category: " + categoryName + " ********************\n\n");

	    	          MenuCategory menuCategory = new MenuCategory(categoryName);

	    	          List<Element> items = category.select("div.menu-item");

	    	          for (Element item : items) {
	    	              String itemName = item.text().split("\\$")[0].trim();
	    	              String itemPrice = item.select("div.menu-item-price").text();
	    	                  String itemDescription = category.select("div.menu-item-description").text();
	    	              MenuItem menuItem = new MenuItem(itemName, itemPrice, itemDescription);
	    	              bufferedWriter.write("Item: " + itemName + "\n");
	    	            bufferedWriter.write("Price: " + itemPrice + "\n");
	    	            bufferedWriter.write("Description: " + itemDescription + "\n");
	    	            bufferedWriter.write("-----------------------------");
	    	            bufferedWriter.write("\n");
	    	              menuCategory.addItem(menuItem);
	    	          }

	    	          menu.addCategory(menuCategory);
	    	      }
	    	    }
		 

	    
	    
	 // Scrape and extract data from the second website
	    private static void scrapeWebsite2(WebDriver driver, String url) {
	        // Code for website 2
	    	
	      driver.get(url);
	      String startingUrl = url;
	      HashSet<String> visitedUrls = new HashSet<String>();
	      visitedUrls.add(startingUrl);
	      WebElement selectMenu = driver.findElement(By.cssSelector("li#menu-item-121"));
	      WebElement subElementB = driver.findElement(By.cssSelector("#menu-item-121 a"));
	    String href = subElementB.getAttribute("href");
	    visitedUrls.add(href);
	      selectMenu.click();
	      
	      try {
	          // Create a FileWriter and BufferedWriter to write to a text file
	          List<String> extractedData = new ArrayList<>();

	          
	          WebElement menuContainer = driver.findElement(By.cssSelector("div.menu-box-row"));
	        // Jsoup: Parse the HTML content
	        Document document = Jsoup.parse(menuContainer.getAttribute("outerHTML"));
	          
	          for (Element category : document.select("div.menu-box")) {
	              String categoryName = category.select("h3").text();
	              extractedData.add("\n******************** Category: " + categoryName + " ********************\n");

	              for (Element item : category.select("ul li")) {
	                  String itemName = item.select("h4").first().text();
	                  String itemPrice = item.select("p").text();
	                  if (itemName.contains("Chefs Salad")){
	                	  continue;
	                  }
	                  extractedData.add("Item: " + itemName);
	                  extractedData.add("Price: " + itemPrice);

	                  if (!itemName.contains("Chefs Salad") || !itemName.equals("Naan") || !itemName.equals("Garlic Naan") || !itemName.equals("Pulao Rice") || !itemName.contains("Cheese Naan") || !"DESSERTS".equals(categoryName) || !"HOT BEVERAGES".equals(categoryName) || !"SIDES".equals(categoryName)) {
	                      String itemDescription = item.select("h4 small").text();
	                      extractedData.add("Description: " + itemDescription);
	            
	                  }

	                  extractedData.add("-----------------------------");
	              }
	          }

	          // Write the extracted data to a text file
	          try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("WebSite2.txt"))) {
	              for (String line : extractedData) {
	                  bufferedWriter.write(line + "\n");
	              }
	          }

	      } catch (IOException e) {
	          e.printStackTrace();
	      }
	      for (String urls : visitedUrls) {
	          System.out.println("Visiting: "+urls);
	      }
	    }

	 // Scrape and extract data from the third website
	    private static void scrapeWebsite1(WebDriver driver, String url) {
	        driver.get(url);
	        webCrawlSite(url);
	       HashSet<String> visitedUrls = new HashSet<String>();
	        try {
	        	
	            visitedUrls.add(url);

	            WebElement elementToHover = driver.findElement(By.cssSelector("li.item_page_menus"));
	            Actions actions = new Actions(driver);
	            actions.moveToElement(elementToHover).perform();

	            WebElement subElement = driver.findElement(By.cssSelector("li.item_sub_menus"));
	            WebElement subElementA = driver.findElement(By.cssSelector("a.link_sub"));
	            String href = subElementA.getAttribute("href");
	            visitedUrls.add(href);
	            subElement.click();

	            FileWriter fileWriter = new FileWriter("WebSite1.txt");
	            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

	            List<WebElement> categoryItems = driver.findElements(By.cssSelector("div.menu_section"));
	            for (WebElement categoryItem : categoryItems) {
	                // ... (rest of the code for website 1)
	                String categoryName = categoryItem.findElement(By.cssSelector("h4.section_name.sub_title")).getText();
	                bufferedWriter.write("\n******************** Category: " + categoryName + " ********************\n\n");

	                WebElement categoryData = categoryItem.findElement(By.cssSelector("div.menu_item_wrapper"));
	                List<WebElement> items = categoryData.findElements(By.cssSelector("div.menu_item"));

	                for (WebElement item : items) {
	                    String itemName = item.findElement(By.cssSelector("h4.item_name.sub_title")).getText();
	                    String itemDescription = item.findElement(By.cssSelector("div.item_desc")).getText();
	                    String itemPrice = item.findElement(By.cssSelector("td.price.calories-legal")).getText();

	                    bufferedWriter.write("Item: " + itemName + "\n");
	                    bufferedWriter.write("Price: " +"$"  + itemPrice + "\n");
	                    bufferedWriter.write("Description: " + itemDescription + "\n");
	                    bufferedWriter.write("-----------------------------");
	                    bufferedWriter.write("\n");
	              
	                    // Jsoup: Parse the HTML content of the item
	                    String itemHtml = item.getAttribute("outerHTML");
	                }
	            }

	            bufferedWriter.close();
	            fileWriter.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	
	        for (String urls : visitedUrls) {
	            System.out.println("Visiting: "+urls);
	        }
	    }
}
