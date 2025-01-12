package demo2;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AmazonCriticalTests {

    private static WebDriver webDriver;
    private static final String baseUrl = "https://www.amazon.com";

    @BeforeAll
    static void setUp() throws InterruptedException {

        System.setProperty("webdriver.chrome.driver", "/Users/gospodinsafarov/Downloads/chromedriver-mac-x64/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        webDriver = new ChromeDriver(options);
        webDriver.get(baseUrl);
        Thread.sleep(7000);
    }


    @Test
    void testLogin() {

        webDriver.findElement(By.id("nav-link-accountList")).click();
        webDriver.findElement(By.id("ap_email")).sendKeys("elnu1990@list.ru");
        webDriver.findElement(By.id("continue")).click();
        webDriver.findElement(By.id("ap_password")).sendKeys("ValidPassword123");
        webDriver.findElement(By.id("signInSubmit")).click();
        WebElement accountName = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-link-accountList-nav-line-1")));
        assertTrue(accountName.getText().contains("Hello"));
    }

    @Test
    void testRegistrationInputValidation() {
        webDriver.findElement(By.id("nav-link-accountList")).click();
        WebElement createAccountLink = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.linkText("Create your Amazon account")));
        createAccountLink.click();
        new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("ap_customer_name"))).sendKeys("Test User");
        webDriver.findElement(By.id("ap_email")).sendKeys("invalid_email");
        webDriver.findElement(By.id("ap_password")).sendKeys("ValidPassword123");
        webDriver.findElement(By.id("ap_password_check")).sendKeys("ValidPassword123");
        webDriver.findElement(By.id("continue")).click();
        WebElement errorMessage = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[@id='auth-email-invalid-claim-alert']//div[contains(@class, 'a-alert-content')]")
                ));
        String actualErrorMessage = errorMessage.getText().trim();
        assertEquals("Wrong or Invalid email address or mobile phone number. Please correct and try again.",
                actualErrorMessage);
    }

    // 3. Search Functionality
    @Test
    void testSearchFunctionality() {
        WebElement searchBox = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));
        searchBox.sendKeys("laptop");
        WebElement searchButton = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.id("nav-search-submit-button")));
        searchButton.click();
        WebElement searchResults = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".s-main-slot")));
        assertTrue(searchResults.isDisplayed());
    }


    // 4. Product Details
    @Test
    void testProductDetails() {
        webDriver.findElement(By.id("twotabsearchtextbox")).sendKeys("laptop");
        webDriver.findElement(By.id("nav-search-submit-button")).click();
        webDriver.findElement(By.cssSelector(".s-main-slot .s-result-item .a-link-normal")).click();
        WebElement productTitle = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("productTitle")));
        assertNotNull(productTitle.getText());
    }

    // 5. Add to Cart
    @Test
    void testAddToCart() {
        webDriver.findElement(By.id("twotabsearchtextbox")).sendKeys("phone");
        webDriver.findElement(By.id("nav-search-submit-button")).click();
        webDriver.findElement(By.cssSelector(".s-main-slot .s-result-item .a-link-normal")).click();
        WebElement addToCartButton = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button")));
        addToCartButton.click();
        WebElement cartCount = webDriver.findElement(By.id("nav-cart-count"));
        assertNotEquals("0", cartCount.getText());
    }

    // 6. Navigation Links
    @Test
    void testNavigationLinks() {

        try {
            WebElement dismissButton = new WebDriverWait(webDriver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Dismiss')]")));
            dismissButton.click();
            System.out.println("Dismiss button clicked.");
        } catch (Exception e) {
            System.out.println("No dismissable popup found.");
        }

        try {

            WebElement blockingElement = new WebDriverWait(webDriver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'glow-toaster')]")));
            ((JavascriptExecutor) webDriver).executeScript("arguments[0].remove();", blockingElement);
            System.out.println("Blocking element removed.");
        } catch (Exception e) {
            System.out.println("No blocking element found.");
        }


        try {
            WebElement todaysDealsLink = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(By.linkText("Today's Deals")));
            todaysDealsLink.click();
            System.out.println("Clicked on 'Today's Deals' link.");
        } catch (Exception e) {
            System.out.println("Unable to click on 'Today's Deals', attempting JavaScript click.");
            WebElement todaysDealsLink = webDriver.findElement(By.linkText("Today's Deals"));
            ((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", todaysDealsLink);
        }


        String expectedUrlPart = "wintersale";
        boolean isUrlCorrect = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains(expectedUrlPart));

        String currentUrl = webDriver.getCurrentUrl();
        System.out.println("Redirected URL: " + currentUrl);

        assertTrue(isUrlCorrect && currentUrl.contains(expectedUrlPart),
                "Redirected URL is not as expected. Actual URL: " + currentUrl);
    }
    @Test
    void testFilterApplication() {

        webDriver.findElement(By.id("twotabsearchtextbox")).sendKeys("laptop");
        webDriver.findElement(By.id("nav-search-submit-button")).click();


        WebElement searchResults = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".s-main-slot")));
        assertTrue(searchResults.isDisplayed(), "Search results are not displayed.");


        try {
            WebElement blockingElement = new WebDriverWait(webDriver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'glow-toaster')]")));
            ((JavascriptExecutor) webDriver).executeScript("arguments[0].remove();", blockingElement);
            System.out.println("Blocking element removed.");
        } catch (Exception e) {
            System.out.println("No blocking element found.");
        }


        WebElement filter64GBCheckbox = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[@id='p_n_feature_thirty-three_browse-bin/23720421011']//input[@type='checkbox']")));
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", filter64GBCheckbox);


        try {
            ((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", filter64GBCheckbox);
        } catch (Exception e) {
            System.out.println("JavaScript click failed. Retrying native click.");
            filter64GBCheckbox.click();
        }


        new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".s-main-slot"))));


        WebElement appliedFilterText = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class='a-size-base a-color-base a-text-bold' and text()='64 GB']")));
        assertTrue(appliedFilterText.isDisplayed(), "The 64 GB filter is not applied.");
    }











    // 8. Session Handling
    @Test
    void testSessionHandling() {
        webDriver.findElement(By.id("nav-link-accountList")).click();
        webDriver.findElement(By.id("ap_email")).sendKeys("elnu1990@list.ru");
        webDriver.findElement(By.id("continue")).click();
        webDriver.findElement(By.id("ap_password")).sendKeys("ValidPassword123");
        webDriver.findElement(By.id("signInSubmit")).click();
        webDriver.navigate().refresh();
        WebElement accountName = webDriver.findElement(By.id("nav-link-accountList-nav-line-1"));
        assertTrue(accountName.getText().contains("Hello"));
    }

    // 9. HTTPS Compliance
    @Test
    void testHttpsCompliance() {
        String currentUrl = webDriver.getCurrentUrl();
        assertTrue(currentUrl.startsWith("https://"));
    }

    // 10. Order History
    @Test
    void testOrderHistory() {

        webDriver.findElement(By.id("nav-link-accountList")).click();
        webDriver.findElement(By.id("ap_email")).sendKeys("elnu1990@list.ru");
        webDriver.findElement(By.id("continue")).click();
        webDriver.findElement(By.id("ap_password")).sendKeys("ValidPassword123");
        webDriver.findElement(By.id("signInSubmit")).click();
        WebElement ordersLink = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.id("nav-orders")));
        ordersLink.click();

        new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));

        WebElement header = webDriver.findElement(By.tagName("h1"));
        assertEquals("Your Orders", header.getText().trim(), "Page header does not match.");

        try {
            WebElement noOrdersMessage = webDriver.findElement(By.xpath("//div[contains(text(), \"Looks like you haven't placed an order in the last 3 months.\")]"));
            assertTrue(noOrdersMessage.isDisplayed(), "No orders message is not displayed.");
        } catch (NoSuchElementException e) {
            fail("No orders message was not found on the page.");
        }
    }


    @Test
    void testChangeLanguage() {

        WebElement languageButton = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.id("icp-nav-flyout")));
        languageButton.click();

        new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h3[@id='icp-language-heading' and contains(text(), 'Language Settings')]")));

        WebElement spanishOption = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@type='radio' and @value='es_US']/following-sibling::i")));
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", spanishOption);
        spanishOption.click();

        WebElement saveButton = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@class='a-button-input' and @type='submit']")));
        saveButton.click();

        WebElement confirmationText = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), 'español')]")));
        assertTrue(confirmationText.getText().contains("español"), "Language was not changed to Spanish.");
    }

    @Test
    void testAddToWishlist() {

        webDriver.findElement(By.id("nav-link-accountList")).click();
        webDriver.findElement(By.id("ap_email")).sendKeys("elnu1990@list.ru");
        webDriver.findElement(By.id("continue")).click();
        webDriver.findElement(By.id("ap_password")).sendKeys("ValidPassword123");
        webDriver.findElement(By.id("signInSubmit")).click();

        WebElement searchBox = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));
        searchBox.sendKeys("laptop");
        webDriver.findElement(By.id("nav-search-submit-button")).click();

        WebElement productLink = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector(".s-main-slot .s-result-item .a-link-normal")));
        productLink.click();

        WebElement addToWishlistButton = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.id("add-to-wishlist-button-submit")));
        addToWishlistButton.click();

        WebElement wishlistConfirmation = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".a-size-medium.a-color-success")));
        assertTrue(wishlistConfirmation.isDisplayed(), "Item was not added to the wishlist.");
    }
    @Test
    void testChangeCurrency() throws InterruptedException {



        WebElement currencyDropdownButton = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.id("icp-nav-flyout")));
        currencyDropdownButton.click();


        new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h3#icp-currency-heading")));

        WebElement currencyDropdownList = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector("span.a-button-text.a-declarative")));
        currencyDropdownList.click();

        WebElement currencyOption = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#icp-currency-dropdown_3")));
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", currencyOption);
        Thread.sleep(2000);
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", currencyOption);

        WebElement saveButton = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector("input.a-button-input")));


        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", saveButton);
        Thread.sleep(1000);
        saveButton.click();


        Thread.sleep(5000);
        JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;
        jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");

        WebElement currencySymbol = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("span.icp-color-base"))).get(1);

        assertTrue(currencySymbol.getText().contains("USD"), "Currency was not updated to COP.");
    }


    @Test
    void testSendMessageToRufus() throws InterruptedException {
        webDriver.findElement(By.id("nav-link-accountList")).click();
        webDriver.findElement(By.id("ap_email")).sendKeys("elnu1990@list.ru");
        webDriver.findElement(By.id("continue")).click();
        webDriver.findElement(By.id("ap_password")).sendKeys("ValidPassword123");
        webDriver.findElement(By.id("signInSubmit")).click();

        WebElement rufusButton = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.id("nav-rufus-disco")));
        rufusButton.click();


        WebElement rufusHeader = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#nav-rufus-disco-text")));
        assertTrue(rufusHeader.isDisplayed(), "Rufus window did not open.");

        WebElement messageInput = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("rufus-text-area")));
        messageInput.sendKeys("How are you?");


        WebElement sendButton = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.rufus-submit-icon-container")));
        sendButton.click();

        Thread.sleep(7000);

        List<WebElement> rufusResponses = new WebDriverWait(webDriver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("div.a-section.rufus-text-subsections-with-avatar")));


        assertTrue(rufusResponses.size() > 1, "Rufus did not return the second response.");


        String responseText = rufusResponses.get(1).getText();
        System.out.println("Rufus responded with: " + responseText);


        assertTrue(responseText.contains("helpful") && responseText.contains("honest") && responseText.contains("harmless"),
                "Rufus response did not match expected keywords.");
    }

    @Test
    void testAccountSettingsNavigation() {
        webDriver.findElement(By.id("nav-link-accountList")).click();
        webDriver.findElement(By.id("ap_email")).sendKeys("elnu1990@list.ru");
        webDriver.findElement(By.id("continue")).click();
        webDriver.findElement(By.id("ap_password")).sendKeys("ValidPassword123");
        webDriver.findElement(By.id("signInSubmit")).click();

        WebElement loggedInAccountLink = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-link-accountList-nav-line-1")));
        assertTrue(loggedInAccountLink.getText().contains("Hello"), "User is not logged in.");


        loggedInAccountLink.click();
        webDriver.findElement(By.id("nav-link-accountList")).click();


        WebElement accountSettingsHeader = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
        assertEquals("Your Account", accountSettingsHeader.getText(), "Account settings page did not load.");


        WebElement yourOrdersLink = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Your Orders")));
        assertTrue(yourOrdersLink.isDisplayed(), "Link to 'Your Orders' is not displayed.");


    }






    @AfterAll
    static void tearDown() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }
}
