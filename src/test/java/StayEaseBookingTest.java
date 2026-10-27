```java
import java.net.URL;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class StayEaseBookingTest {

    private static final String GRID_URL =
            "http://localhost:4444";

    private static final String WEBSITE_URL =
            "https://www.practiceqaautomation.com/apps/hotel";

    @Test
    public void testHotelBookingInChrome() throws Exception {
        runTests("chrome");
    }

    @Test
    public void testHotelBookingInFirefox() throws Exception {
        runTests("firefox");
    }

    private void runTests(String browser) throws Exception {

        WebDriver driver = null;

        try {
            System.out.println("Starting " + browser);

            if (browser.equalsIgnoreCase("chrome")) {
                ChromeOptions options = new ChromeOptions();
                driver = new RemoteWebDriver(
                        new URL(GRID_URL), options);
            } else {
                FirefoxOptions options = new FirefoxOptions();
                driver = new RemoteWebDriver(
                        new URL(GRID_URL), options);
            }

            WebDriverWait wait = new WebDriverWait(
                    driver, Duration.ofSeconds(20));

            // TEST 1: Open the hotel booking website
            driver.get(WEBSITE_URL);

            wait.until(d ->
                    ((org.openqa.selenium.JavascriptExecutor) d)
                            .executeScript(
                                    "return document.readyState")
                            .equals("complete"));

            Assertions.assertTrue(
                    driver.getCurrentUrl().contains("/apps/hotel"),
                    "Hotel booking page did not open");

            Assertions.assertTrue(
                    driver.findElement(By.tagName("body"))
                            .isDisplayed(),
                    "Page body is not visible");

            System.out.println(
                    "PASS: Website opened in " + browser);

            System.out.println("Page title: "
                    + driver.getTitle());

            // TEST 2: Check the expected search controls
            List<WebElement> searchButtons =
                    driver.findElements(
                            By.xpath("//button[contains(" +
                                    "translate(normalize-space(.)," +
                                    "'SEARCH','search'),'search')]"));

            if (searchButtons.isEmpty()) {
                searchButtons = driver.findElements(
                        By.cssSelector(
                                "button[type='submit'], " +
                                "input[type='submit']"));
            }

            Assertions.assertFalse(
                    searchButtons.isEmpty(),
                    "Search button was not found");

            System.out.println(
                    "PASS: Search button found in " + browser);

            // TEST 3: Check that validation elements exist.
            // They may be hidden until an invalid search is submitted.
            boolean cityErrorElementExists =
                    !driver.findElements(
                            By.id("error-city")).isEmpty();

            boolean checkinErrorElementExists =
                    !driver.findElements(
                            By.id("error-checkin")).isEmpty();

            System.out.println(
                    "error-city element exists: "
                            + cityErrorElementExists);

            System.out.println(
                    "error-checkin element exists: "
                            + checkinErrorElementExists);

            // TEST 4: Submit the empty search form
            WebElement searchButton = searchButtons.get(0);

            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript(
                            "arguments[0].scrollIntoView(" +
                            "{block:'center'});",
                            searchButton);

            searchButton.click();

            // Wait for either validation error to appear.
            // This avoids timing out on error-city alone.
            boolean validationAppeared = wait.until(d -> {
                List<WebElement> cityErrors =
                        d.findElements(By.id("error-city"));

                List<WebElement> dateErrors =
                        d.findElements(By.id("error-checkin"));

                boolean cityVisible = cityErrors.stream()
                        .anyMatch(WebElement::isDisplayed);

                boolean dateVisible = dateErrors.stream()
                        .anyMatch(WebElement::isDisplayed);

                return cityVisible || dateVisible;
            });

            Assertions.assertTrue(
                    validationAppeared,
                    "No search validation appeared after " +
                    "submitting an empty form");

            System.out.println(
                    "PASS: Empty-search validation displayed in "
                            + browser);

        } catch (Exception e) {
            System.err.println(
                    "FAIL: " + browser + " test failed");
            System.err.println(
                    "Reason: " + e.getMessage());

            throw e;

        } finally {
            if (driver != null) {
                driver.quit();
                System.out.println(
                        "Closed " + browser + " session");
            }
        }
    }
}
```
