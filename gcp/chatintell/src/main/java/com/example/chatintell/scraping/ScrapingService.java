package com.example.chatintell.scraping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScrapingService {

    private static final Logger logger = LoggerFactory.getLogger(ScrapingService.class);

    private static final String[] URLS = {
            "https://www.scoopgaming.com.tn/58-ecrans-gaming",
            "https://megapc.tn/shop/ACCESSOIRES/SOURIS",
            "https://www.sbsinformatique.com/claviers-gamer-tunisie",
            "https://www.tunisianet.com.tn/338-casque-ecouteurs",
            "https://www.tunisianet.com.tn/331-sac-a-dos-tunisie",
            "https://www.tunisianet.com.tn/498-hub-usb-lecteur-carte-tunisie",
            "https://www.tunisianet.com.tn/336-webcam",
            "https://www.tunisianet.com.tn/408-disque-dur-interne",
            "https://www.tunisianet.com.tn/485-microphone",
            "https://www.tunisianet.com.tn/733-meubles-pc",
            "https://www.tunisianet.com.tn/326-scanner-informatique"
    };

    private static final Map<String, SiteSelectors> SITE_SELECTORS = new HashMap<>();

    static {
        SITE_SELECTORS.put("https://www.scoopgaming.com.tn/58-ecrans-gaming",
                new SiteSelectors(".tvproduct-wrapper", ".tvproduct-name h6", ".price"));
        SITE_SELECTORS.put("https://megapc.tn/shop/ACCESSOIRES/SOURIS",
                new SiteSelectors(".product-card", ".text-skin-base", ".text-skin-primary"));
        SITE_SELECTORS.put("https://www.sbsinformatique.com/claviers-gamer-tunisie",
                new SiteSelectors(".tvproduct-wrapper", ".tvproduct-name h6, .product-title h6", ".price"));
        // Updated selectors for Tunisianet category pages
        SITE_SELECTORS.put("https://www.tunisianet.com.tn/338-casque-ecouteurs",
                new SiteSelectors(".js-product-miniature", ".product-title", ".product-price-and-shipping .price"));
        SITE_SELECTORS.put("https://www.tunisianet.com.tn/331-sac-a-dos-tunisie",
                new SiteSelectors(".js-product-miniature", ".product-title", ".product-price-and-shipping .price"));
        SITE_SELECTORS.put("https://www.tunisianet.com.tn/498-hub-usb-lecteur-carte-tunisie",
                new SiteSelectors(".js-product-miniature", ".product-title", ".product-price-and-shipping .price"));
        SITE_SELECTORS.put("https://www.tunisianet.com.tn/336-webcam",
                new SiteSelectors(".js-product-miniature", ".product-title", ".product-price-and-shipping .price"));
        SITE_SELECTORS.put("https://www.tunisianet.com.tn/408-disque-dur-interne",
                new SiteSelectors(".js-product-miniature", ".product-title", ".product-price-and-shipping .price"));
        SITE_SELECTORS.put("https://www.tunisianet.com.tn/485-microphone",
                new SiteSelectors(".js-product-miniature", ".product-title", ".product-price-and-shipping .price"));
        SITE_SELECTORS.put("https://www.tunisianet.com.tn/733-meubles-pc",
                new SiteSelectors(".js-product-miniature", ".product-title", ".product-price-and-shipping .price"));
        SITE_SELECTORS.put("https://www.tunisianet.com.tn/326-scanner-informatique",
                new SiteSelectors(".js-product-miniature", ".product-title", ".product-price-and-shipping .price"));
    }

    public List<Product> scrapeAllSites() {
        logger.info("Starting scrapeAllSites...");
        List<Product> allProducts = new ArrayList<>();

        for (String url : URLS) {
            logger.info("Starting to process URL: {}", url);
            WebDriver driver = null;
            int maxRetries = 3;
            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                try {
                    io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments(
                            "--headless",
                            "--disable-gpu",
                            "--no-sandbox",
                            "--disable-dev-shm-usage",
                            "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36"
                    );
                    driver = new ChromeDriver(options);
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

                    driver.get(url);
                    logger.info("Successfully loaded URL: {}", url);

                    if (isCloudflareProtected(driver)) {
                        logger.warn("Cloudflare protection detected on {}. Page source excerpt: {}. Retrying...", url,
                                driver.getPageSource().substring(0, Math.min(500, driver.getPageSource().length())));
                        continue;
                    }

                    SiteSelectors selectors = SITE_SELECTORS.getOrDefault(url,
                            new SiteSelectors(".product", ".product-name", ".price"));
                    logger.info("Using selectors: product={}, name={}, price={}",
                            selectors.getProductSelector(), selectors.getNameSelector(), selectors.getPriceSelector());

                    scrollToLoadProducts(driver, wait, selectors.getProductSelector(), selectors.getPriceSelector());
                    List<WebElement> products = driver.findElements(By.cssSelector(selectors.getProductSelector()));
                    logger.info("Found {} products on {}", products.size(), url);

                    if (products.isEmpty()) {
                        logger.warn("No products found on {}. Full page source: {}", url,
                                driver.getPageSource().substring(0, Math.min(5000, driver.getPageSource().length())));
                    }

                    // Determine the category from the URL
                    String category = determineCategoryFromUrl(url);

                    for (WebElement product : products) {
                        try {
                            String name = extractName(product, selectors.getNameSelector(), url);
                            if (name.isEmpty()) {
                                logger.warn("Skipping product with empty name on {}. HTML: {}", url, product.getAttribute("outerHTML"));
                                continue;
                            }

                            String priceText = extractPrice(product, selectors.getPriceSelector(), url);
                            if (priceText.isEmpty()) {
                                logger.warn("Skipping product with empty price on {}. Product name: {}", url, name);
                                continue;
                            }

                            double price = parsePrice(priceText);

                            // Updated category filtering for Tunisianet
                            if (url.contains("tunisianet") && !category.equals("other")) {
                                boolean matchesCategory = false;
                                String nameLower = name.toLowerCase();
                                if (category.equals("bureau")) {
                                    // For meubles-pc, accept both "bureau" (desk) and "chaise" (chair)
                                    matchesCategory = nameLower.contains("bureau") || nameLower.contains("chaise");
                                } else {
                                    matchesCategory = nameLower.contains(category);
                                }
                                if (!matchesCategory) {
                                    logger.info("Skipping product not in target category '{}': {} on {}", category, name, url);
                                    continue;
                                }
                            }

                            Product productData = new Product(name, price, getSiteName(url), url);
                            allProducts.add(productData);
                            logger.info("Added product: {} | Price: {} | Site: {}", name, price, getSiteName(url));
                        } catch (Exception e) {
                            logger.error("Error parsing product on {}: {}", url, e.getMessage());
                        }
                    }
                    break; // Exit retry loop if successful
                } catch (NoSuchSessionException e) {
                    logger.error("Session invalidated for URL {}. Reinitializing WebDriver...", url);
                } catch (Exception e) {
                    logger.error("Failed to process URL {} on attempt {}/{}: {}", url, attempt, maxRetries, e.getMessage(), e);
                    if (attempt == maxRetries) {
                        logger.error("Max retries reached for URL {}. Skipping...", url);
                    }
                    try {
                        Thread.sleep(5000); // Wait 5 seconds before retrying
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } finally {
                    closeWebDriver(driver, url);
                }
            }
        }

        logger.info("Scraping completed. Total products: {}", allProducts.size());
        return allProducts;
    }

    private void scrollToLoadProducts(WebDriver driver, WebDriverWait wait, String productSelector, String priceSelector) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            // Wait for initial page load
            Thread.sleep(15000); // 15 seconds for Tunisianet

            // Check if there's a "Load More" button (common on some Tunisianet pages)
            try {
                WebElement loadMoreButton = driver.findElement(By.cssSelector(".load-more, .btn-load-more, [data-load-more]"));
                while (loadMoreButton.isDisplayed()) {
                    loadMoreButton.click();
                    Thread.sleep(2000); // Wait for new content to load
                    loadMoreButton = driver.findElement(By.cssSelector(".load-more, .btn-load-more, [data-load-more]"));
                }
            } catch (NoSuchElementException e) {
                logger.info("No 'Load More' button found, proceeding with scroll loading...");
            }

            // Fallback to infinite scrolling
            long lastHeight = (long) js.executeScript("return document.body.scrollHeight");
            int scrollAttempts = 0;
            final int maxAttempts = 20;
            final int minProductsWithPrice = 3; // Wait for at least 3 products to have prices

            while (scrollAttempts < maxAttempts) {
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                Thread.sleep(10000); // Increased wait time to 10 seconds to ensure content loads
                long newHeight = (long) js.executeScript("return document.body.scrollHeight");

                // Check if products and prices are loaded
                List<WebElement> products = driver.findElements(By.cssSelector(productSelector));
                int productsWithPrices = 0;
                if (!products.isEmpty()) {
                    logger.info("Found {} products after {} scroll attempts", products.size(), scrollAttempts + 1);
                    for (WebElement product : products) {
                        try {
                            List<WebElement> priceElements = product.findElements(By.cssSelector(priceSelector));
                            logger.debug("Found {} price elements in product", priceElements.size());
                            for (WebElement priceElement : priceElements) {
                                String priceText = priceElement.getText().trim();
                                if (priceElement.isDisplayed() && !priceText.isEmpty()) {
                                    productsWithPrices++;
                                    logger.info("Price found for product: {}", priceText);
                                    break;
                                }
                            }
                        } catch (NoSuchElementException ignored) {
                            logger.debug("Price not found for a product during scroll attempt {}", scrollAttempts + 1);
                        }
                    }
                } else {
                    logger.warn("No products found after {} scroll attempts", scrollAttempts + 1);
                }

                if (productsWithPrices >= minProductsWithPrice) {
                    logger.info("Found prices for {} products after {} scroll attempts: {} total products found", productsWithPrices, scrollAttempts + 1, products.size());
                    break;
                }

                if (newHeight == lastHeight) {
                    logger.info("No new content loaded after {} attempts", scrollAttempts + 1);
                    break;
                }
                lastHeight = newHeight;
                scrollAttempts++;
            }

            // Final wait to ensure at least 3 products have non-empty price text
            wait.until(driverInstance -> {
                List<WebElement> products = driverInstance.findElements(By.cssSelector(productSelector));
                int productsWithPrices = 0;
                for (WebElement product : products) {
                    try {
                        List<WebElement> priceElements = product.findElements(By.cssSelector(priceSelector));
                        for (WebElement priceElement : priceElements) {
                            if (priceElement.isDisplayed() && !priceElement.getText().trim().isEmpty()) {
                                productsWithPrices++;
                                break;
                            }
                        }
                    } catch (NoSuchElementException ignored) {
                    }
                    if (productsWithPrices >= minProductsWithPrice) {
                        return true;
                    }
                }
                return false;
            });
        } catch (InterruptedException e) {
            logger.warn("Interrupted while scrolling: {}", e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("Error during scrolling: {}", e.getMessage());
            throw e;
        }
    }

    private String extractName(WebElement product, String nameSelector, String url) {
        try {
            WebElement nameElement = product.findElement(By.cssSelector(nameSelector));
            String name = nameElement.getText().trim();
            logger.debug("Extracted name: '{}' using selector '{}'", name, nameSelector);
            if (name.isEmpty()) {
                logger.warn("Empty name retrieved with selector '{}' on {}. Trying fallbacks...", nameSelector, url);
                return tryFallbackName(product, url);
            }
            return name;
        } catch (NoSuchElementException e) {
            logger.warn("Primary name selector ({}) failed for {}. Trying fallbacks...", nameSelector, url);
            return tryFallbackName(product, url);
        }
    }

    private String tryFallbackName(WebElement product, String url) {
        String[] fallbacks = {".title", ".product-name", "h3", "h4", "h5", "h6"};
        for (String fallback : fallbacks) {
            try {
                WebElement nameElement = product.findElement(By.cssSelector(fallback));
                String name = nameElement.getText().trim();
                if (!name.isEmpty()) {
                    logger.info("Fallback selector '{}' succeeded for name: '{}'", fallback, name);
                    return name;
                }
            } catch (NoSuchElementException ignored) {
            }
        }
        return "";
    }

    private String extractPrice(WebElement product, String priceSelector, String url) {
        try {
            String priceText = "";
            int maxRetries = 3;
            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                List<WebElement> priceElements = product.findElements(By.cssSelector(priceSelector));
                logger.info("Attempt {}: Found {} price elements with selector '{}' on {}", attempt, priceElements.size(), priceSelector, url);
                for (WebElement priceElement : priceElements) {
                    priceText = priceElement.getText().trim();
                    logger.info("Attempt {}: Raw price text: '{}'", attempt, priceText);
                    if (!priceText.isEmpty()) {
                        return priceText;
                    }
                }
                if (attempt < maxRetries) {
                    logger.debug("Price text is empty on attempt {}. Retrying after 2 seconds...", attempt);
                    Thread.sleep(2000); // Wait 2 seconds before retrying
                }
            }
            logger.warn("All price elements were empty after {} attempts with selector '{}' on {}. Trying fallbacks...", maxRetries, priceSelector, url);
            return tryFallbackPrice(product, url);
        } catch (NoSuchElementException e) {
            logger.warn("Primary price selector ({}) failed for {}. Trying fallbacks... Error: {}", priceSelector, url, e.getMessage());
            return tryFallbackPrice(product, url);
        } catch (InterruptedException e) {
            logger.warn("Interrupted while retrying price extraction: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return tryFallbackPrice(product, url);
        }
    }

    private String tryFallbackPrice(WebElement product, String url) {
        String[] fallbacks = {".price", "[itemprop='price']", ".product-price"};
        for (String fallback : fallbacks) {
            try {
                WebElement priceElement = product.findElement(By.cssSelector(fallback));
                String priceText = priceElement.getText().trim();
                if (!priceText.isEmpty()) {
                    logger.info("Fallback selector '{}' succeeded for price: '{}'", fallback, priceText);
                    return priceText;
                }
            } catch (NoSuchElementException ignored) {
            }
        }
        logger.warn("All price selectors failed for product on {}. HTML: {}", url, product.getAttribute("outerHTML"));
        return "";
    }

    private double parsePrice(String priceText) {
        if (priceText == null || priceText.trim().isEmpty()) {
            logger.warn("Price text is empty");
            return 0.0;
        }
        // Handle various price formats (e.g., "18nbsp;169,000 DT", "169.000 DT", "169 DT")
        String cleanPrice = priceText
                .replaceAll("[^0-9,.]", "") // Remove non-numeric characters except comma and dot
                .replace(",", "."); // Convert comma to dot for decimal
        try {
            double price = Double.parseDouble(cleanPrice);
            logger.info("Parsed price: {} from raw text: {}", price, priceText);
            return price;
        } catch (NumberFormatException e) {
            logger.error("Failed to parse price: {}", priceText);
            return 0.0;
        }
    }

    private String getSiteName(String url) {
        if (url.contains("scoopgaming")) return "Scoop Gaming";
        if (url.contains("megapc")) return "MegaPC";
        if (url.contains("sbsinformatique")) return "SBS Informatique";
        if (url.contains("tunisianet")) return "Tunisianet";
        return "Unknown";
    }

    private boolean isCloudflareProtected(WebDriver driver) {
        return driver.getPageSource().contains("Cloudflare") || driver.getTitle().contains("Un instant");
    }

    private void closeWebDriver(WebDriver driver, String url) {
        if (driver != null) {
            try {
                driver.quit();
                logger.info("WebDriver closed for URL: {}", url);
            } catch (Exception e) {
                logger.error("Error closing WebDriver for URL {}: {}", url, e.getMessage());
            }
        }
    }

    private String determineCategoryFromUrl(String url) {
        if (url.contains("casque-ecouteurs")) return "ecouteur";
        if (url.contains("sac-a-dos")) return "sac";
        if (url.contains("hub-usb")) return "hub usb";
        if (url.contains("webcam")) return "webcam";
        if (url.contains("disque-dur")) return "disque dur";
        if (url.contains("microphone")) return "microphone";
        if (url.contains("meubles-pc")) return "bureau";
        if (url.contains("imprimante")) return "imprimante";
        if (url.contains("scanner-informatique")) return "scanner";
        return "other"; // Default for non-Tunisianet URLs or uncategorized Tunisianet URLs
    }
}