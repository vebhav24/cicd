package com.novelvista;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.junit.jupiter.api.*;

public class UITest {
    WebDriver driver;

    @BeforeEach
    public void setup() {
        // Automatically download the correct ChromeDriver version
        WebDriverManager.chromedriver().setup();

        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testTitle() {
        driver.get("http://localhost:8080/myapp");
        String title = driver.getTitle();
        Assertions.assertTrue(title.contains("MyApp"));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
