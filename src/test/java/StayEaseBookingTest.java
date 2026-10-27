
import java.net.URL;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
            System.out.println("================================");
            System.out.println("Starting browser: " + browser);
            System.out.println("================================");

            // STEP 1: Start browser through Selenium Grid
            if (browser.equalsIgnoreCase("chrome")) {

                ChromeOptions options = new ChromeOptions();

                driver = new RemoteWebDriver(
                        new URL(GRID_URL), options);

            } else if (browser.equalsIgnoreCase("firefox")) {

                FirefoxOptions options = new FirefoxOptions();

                driver = new RemoteWebDriver(
                        new URL(GRID_URL), options);

            } else {
                throw new IllegalArgumentException(
                        "Unsupported browser: " + browser);
            }

            WebDriverWait wait = new WebDriverWait(
                    driver, Duration.ofSeconds(20));

            // STEP 2: Open the hotel booking website
            driver.get(WEBSITE_URL);

            wait.until(d ->
                    "complete".equals(
                            ((JavascriptExecutor) d)
                                    .executeScript(
                                            "return document.readyState")));

            Assertions.assertTrue(
                    driver.getCurrentUrl().contains("/apps/hotel"),
                    "Hotel booking page did not open");

            Assertions.assertTrue(
                    driver.findElement(By.tagName("body"))
                            .isDisplayed(),
                    "Page body is not visible");

            System.out.println(
                    "PASS: Website opened in " + browser);

            System.out.println(
                    "Page title: " + driver.getTitle());

            System.out.println(
                    "Current URL: " + driver.getCurrentUrl());

            // STEP 3: Find the Search button
            List<WebElement> searchButtons =
                    driver.findElements(
                            By.xpath(
                                    "//button[contains(" +
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

            WebElement searchButton = searchButtons.get(0);

            Assertions.assertTrue(
                    searchButton.isDisplayed(),
                    "Search button is not visible");

            System.out.println(
                    "PASS: Search button found in " + browser);

            // STEP 4: Check whether validation elements exist
            System.out.println(
                    "error-city element count: " +
                    driver.findElements(By.id("error-city")).size());

            System.out.println(
                    "error-checkin element count: " +
                    driver.findElements(By.id("error-checkin")).size());

            // STEP 5: Submit the empty search form
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView(" +
                    "{block:'center'});",
                    searchButton);

            wait.until(d -> searchButton.isDisplayed()
                    && searchButton.isEnabled());

            searchButton.click();

            System.out.println(
                    "Clicked Search in " + browser);

            // STEP 6: Allow the page to update, then print diagnostics
            Thread.sleep(2000);

            System.out.println(
                    "Page text after Search (" + browser + "):");

            System.out.println(
                    driver.findElement(By.tagName("body")).getText());

            System.out.println(
                    "City validation element count: " +
                    driver.findElements(By.id("error-city")).size());

            System.out.println(
                    "Check-in validation element count: " +
                    driver.findElements(By.id("error-checkin")).size());

            // STEP 7: Check whether either validation message appears
            boolean validationAppeared = wait.until(d -> {

                List<WebElement> cityErrors =
                        d.findElements(By.id("error-city"));

                List<WebElement> dateErrors =
                        d.findElements(By.id("error-checkin"));

                boolean cityVisible = cityErrors.stream()
                        .anyMatch(element -> {
                            try {
                                return element.isDisplayed()
                                        && !element.getText()
                                                .trim().isEmpty();
                            } catch (Exception e) {
                                return false;
                            }
                        });

                boolean dateVisible = dateErrors.stream()
                        .anyMatch(element -> {
                            try {
                                return element.isDisplayed()
                                        && !element.getText()
                                                .trim().isEmpty();
                            } catch (Exception e) {
                                return false;
                            }
                        });

                return cityVisible || dateVisible;
            });

            Assertions.assertTrue(
                    validationAppeared,
                    "No city or check-in validation message appeared " +
                    "after submitting the empty search form.");

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
