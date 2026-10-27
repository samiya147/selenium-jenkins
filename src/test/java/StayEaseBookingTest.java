
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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class StayEaseBookingTest {

    private static final String GRID_URL =
            "http://localhost:4444";

    private static final String WEBSITE_URL =
            "https://www.practiceqaautomation.com/apps/hotel";

    @Test
    public void testChromeBookingPage() throws Exception {
        runBookingTest("chrome");
    }

    @Test
    public void testFirefoxBookingPage() throws Exception {
        runBookingTest("firefox");
    }

    private void runBookingTest(String browser) throws Exception {

        WebDriver driver = null;

        try {
            // STEP 1: Start browser using Selenium Grid
            if (browser.equalsIgnoreCase("chrome")) {

                ChromeOptions options = new ChromeOptions();

                driver = new RemoteWebDriver(
                        java.net.URI.create(GRID_URL).toURL(),
                        options
                );

            } else if (browser.equalsIgnoreCase("firefox")) {

                FirefoxOptions options = new FirefoxOptions();

                driver = new RemoteWebDriver(
                        java.net.URI.create(GRID_URL).toURL(),
                        options
                );

            } else {
                throw new IllegalArgumentException(
                        "Unsupported browser: " + browser
                );
            }

            driver.manage().timeouts()
                    .implicitlyWait(Duration.ofSeconds(2));

            WebDriverWait wait = new WebDriverWait(
                    driver,
                    Duration.ofSeconds(20)
            );

            System.out.println(
                    "\n========== " + browser.toUpperCase()
                            + " TEST STARTED =========="
            );

            // STEP 2: Open website
            driver.get(WEBSITE_URL);

            wait.until(ExpectedConditions
                    .presenceOfElementLocated(By.tagName("body")));

            System.out.println("Website opened successfully.");
            System.out.println("Page title: " + driver.getTitle());
            System.out.println("Current URL: " + driver.getCurrentUrl());

            Assertions.assertTrue(
                    driver.getTitle().contains("StayEase"),
                    "Unexpected page title in " + browser
            );

            System.out.println("PASS: Website title verified.");

            // STEP 3: Verify destination section
            String pageText = driver.findElement(
                    By.tagName("body")
            ).getText();

            Assertions.assertTrue(
                    pageText.contains("Where are you going?"),
                    "Destination section not found in " + browser
            );

            System.out.println("PASS: Destination section found.");

            // STEP 4: Verify check-in section
            Assertions.assertTrue(
                    pageText.contains("Pick a check-in date"),
                    "Check-in section not found in " + browser
            );

            System.out.println("PASS: Check-in section found.");

            // STEP 5: Find Search hotels button
            List<WebElement> searchButtons = driver.findElements(
                    By.xpath(
                            "//button[contains(" +
                            "translate(normalize-space(.)," +
                            "'SEARCH','search'),'search')]"
                    )
            );

            // Try submit buttons if the text locator did not work
            if (searchButtons.isEmpty()) {
                searchButtons = driver.findElements(
                        By.cssSelector(
                                "button[type='submit'], " +
                                "input[type='submit']"
                        )
                );
            }

            Assertions.assertFalse(
                    searchButtons.isEmpty(),
                    "Search button not found in " + browser
            );

            WebElement searchButton = searchButtons.get(0);

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView(" +
                    "{block:'center'});",
                    searchButton
            );

            wait.until(ExpectedConditions
                    .visibilityOf(searchButton));

            Assertions.assertTrue(
                    searchButton.isEnabled(),
                    "Search button is disabled in " + browser
            );

            System.out.println("PASS: Search button found and enabled.");

            // STEP 6: Click Search hotels
            wait.until(ExpectedConditions
                    .elementToBeClickable(searchButton));

            searchButton.click();

            System.out.println("PASS: Search button clicked.");

            // STEP 7: Wait for page response
            Thread.sleep(2000);

            WebElement body = driver.findElement(By.tagName("body"));

            Assertions.assertTrue(
                    body.isDisplayed(),
                    "Page is not visible after clicking Search."
            );

            System.out.println(
                    "URL after search: " + driver.getCurrentUrl()
            );

            System.out.println("Page response verified.");

            // STEP 8: Test completed
            System.out.println(
                    "========== " + browser.toUpperCase()
                            + " TEST PASSED ==========\n"
            );
            Thread.sleep(1000);

        } catch (Exception e) {

            System.err.println(
                    "\n========== " + browser.toUpperCase()
                            + " TEST FAILED =========="
            );

            System.err.println("Reason: " + e.getMessage());

            throw e;

        } finally {

            // STEP 9: Close browser session
            if (driver != null) {
                driver.quit();
                System.out.println(
                        browser + " browser session closed."
                );
            }
        }
    }
}
