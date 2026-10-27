
import java.net.URL;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class StayEaseBookingTest {

    // Jenkins is running on Windows, so use localhost.
    private static final String GRID_URL = "http://localhost:4444";

    private static final String WEBSITE =
            "https://www.practiceqaautomation.com/apps/hotel";

    @Test
    public void testHotelBookingInChrome() throws Exception {
        runBrowserTest("chrome");
    }

    @Test
    public void testHotelBookingInFirefox() throws Exception {
        runBrowserTest("firefox");
    }

    private void runBrowserTest(String browser) throws Exception {

        WebDriver driver = null;

        try {
            System.out.println("Starting " + browser + " test...");

            // Create a remote browser session on Selenium Grid.
            if (browser.equalsIgnoreCase("chrome")) {
                driver = new RemoteWebDriver(
                        new URL(GRID_URL),
                        new ChromeOptions()
                );
            } else if (browser.equalsIgnoreCase("firefox")) {
                driver = new RemoteWebDriver(
                        new URL(GRID_URL),
                        new FirefoxOptions()
                );
            } else {
                throw new IllegalArgumentException(
                        "Unsupported browser: " + browser
                );
            }

            WebDriverWait wait =
                    new WebDriverWait(driver, Duration.ofSeconds(30));

            // Open the hotel booking website.
            driver.get(WEBSITE);

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.tagName("body")
            ));

            System.out.println("Browser: " + browser);
            System.out.println("Website opened successfully.");
            System.out.println("Page title: " + driver.getTitle());
            System.out.println("Current URL: " + driver.getCurrentUrl());

            // Display dropdown options.
            for (WebElement dropdown :
                    driver.findElements(By.tagName("select"))) {

                Select select = new Select(dropdown);

                System.out.println("Dropdown options:");

                for (WebElement option : select.getOptions()) {
                    System.out.println(
                            option.getText()
                                    + " | value="
                                    + option.getAttribute("value")
                    );
                }
            }

            // Keep the browser open for one minute
            // so you can observe it through noVNC.
            System.out.println(
                    "Browser will remain open for 60 seconds..."
            );

            Thread.sleep(60000);

            System.out.println(browser + " test completed.");

        } catch (Exception e) {
            System.err.println(
                    "Test failed for browser: " + browser
            );
            e.printStackTrace();
            throw e;

        } finally {
            if (driver != null) {
                System.out.println(
                        "Closing " + browser + " browser..."
                );
                driver.quit();
            }
        }
    }
}

