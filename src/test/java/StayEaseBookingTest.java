
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

    private static final String GRID_URL = "http://selenium-hub:4444";
    private static final String WEBSITE =
            "https://www.practiceqaautomation.com/apps/hotel";

    @Test
    public void testHotelBookingInChrome() throws Throwable {
        runBrowserTest("chrome");
    }

    @Test
    public void testHotelBookingInFirefox() throws Throwable {
        runBrowserTest("firefox");
    }

    private void runBrowserTest(String browser) throws Throwable {
        WebDriver driver = null;

        try {
            if (browser.equalsIgnoreCase("chrome")) {
                driver = new RemoteWebDriver(
                        new URL(GRID_URL), new ChromeOptions());
            } else {
                driver = new RemoteWebDriver(
                        new URL(GRID_URL), new FirefoxOptions());
            }

            WebDriverWait wait =
                    new WebDriverWait(driver, Duration.ofSeconds(20));

            driver.get(WEBSITE);
            wait.until(ExpectedConditions
                    .visibilityOfElementLocated(By.tagName("body")));

            System.out.println("Opened website in " + browser);
            System.out.println("Title: " + driver.getTitle());

            // Print the available dropdown options so the correct
            // destination can be selected.
            for (WebElement dropdown :
                    driver.findElements(By.tagName("select"))) {
                Select select = new Select(dropdown);
                System.out.println("Dropdown options:");
                for (WebElement option : select.getOptions()) {
                    System.out.println(
                            option.getText() + " | " + option.getAttribute("value"));
                }
            }

            // Keep the browser open so you can inspect the form.
            Thread.sleep(60000);

        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
}
