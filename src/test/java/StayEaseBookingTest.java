
import java.net.URL;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class StayEaseBookingTest {

    private static final String GRID_URL =
            "http://localhost:4444";

    private static final String WEBSITE =
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
                driver = new RemoteWebDriver(
                        new URL(GRID_URL),
                        new ChromeOptions()
                );
            } else {
                driver = new RemoteWebDriver(
                        new URL(GRID_URL),
                        new FirefoxOptions()
                );
            }

            WebDriverWait wait = new WebDriverWait(
                    driver, Duration.ofSeconds(20)
            );

            // Test 1: Website opens.
            driver.get(WEBSITE);

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.tagName("body")
            ));

            Assertions.assertTrue(
                    driver.getTitle() != null,
                    "Website title should be available"
            );

            System.out.println("PASS: Website opened in " + browser);

            // Test 2: Search without a destination.
            // The documented test plan uses error-city.
            WebElement searchButton = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.cssSelector(
                                    "button[type='submit'], input[type='submit']"
                            )
                    )
            );

            searchButton.click();

            WebElement cityError = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.id("error-city")
                    )
            );

            Assertions.assertTrue(
                    cityError.isDisplayed(),
                    "Destination validation should be displayed"
            );

            System.out.println(
                    "PASS: Empty destination validation in " + browser
            );

        } catch (Exception e) {
            System.err.println(
                    "FAIL: " + browser + " test failed"
            );
            throw e;

        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
}
