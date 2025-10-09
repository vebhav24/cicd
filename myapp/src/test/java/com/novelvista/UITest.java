package com.novelvista;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

public class UITest {
    private WebDriver driver;
    private final String baseUrl = System.getProperty("baseUrl", "http://localhost:8080/myapp");

    @BeforeEach
    void setup() {
        WebDriverManager.chromedriver().setup(); // ✅ auto-matches Chrome/Driver

        ChromeOptions opts = new ChromeOptions();
        // Headless is recommended for Jenkins:
        opts.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(opts);
    }

    @AfterEach
    void teardown() {
        if (driver != null) driver.quit();
    }

    @Test
    void testTitle() {
        driver.get(baseUrl);
        Assertions.assertTrue(driver.getTitle() != null);
    }
}
