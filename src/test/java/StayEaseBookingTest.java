
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

public class StayEaseBookingTest {

    private static final String GRID_URL = "http://localhost:4444";
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
                        new URL(GRID_URL),
                        new ChromeOptions()
                );
            } else {
                driver = new RemoteWebDriver(
                        new URL(GRID_URL),
                        new FirefoxOptions()
                );
            }

            driver.get(WEBSITE);

            String title = driver.getTitle();
            System.out.println(
                    "Browser: " + browser + " | Page title: " + title
            );

            assertTrue(
                    title != null && !title.isEmpty(),
                    "Hotel page should have a title in " + browser
            );

        } catch (Throwable failure) {
            if (driver != null) {
                try {
                    Path folder = Path.of("target", "screenshots");
                    Files.createDirectories(folder);

                    String timestamp = LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")
                    );

                    File screenshot = ((TakesScreenshot) driver)
                            .getScreenshotAs(OutputType.FILE);

                    Files.copy(
                            screenshot.toPath(),
                            folder.resolve(
                                    browser + "-failure-" + timestamp + ".png"
                            )
                    );

                    System.out.println(
                            "Failure screenshot saved in " + folder
                    );
                } catch (Exception screenshotError) {
                    System.out.println(
                            "Could not save screenshot: "
                                    + screenshotError.getMessage()
                    );
                }
            }

            throw failure;

        } finally {
            if (driver != null) {
                Thread.sleep(15000);
                driver.quit();
            }
        }
    }
}
