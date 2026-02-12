package com.selenium.miniproject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class HandleDropdownsTest {

    static WebDriver driver;
    static WebDriverWait wait;
    static File screenshotDir;

    @BeforeClass
    @Parameters({"browser"})
    public void launchBrowser(@org.testng.annotations.Optional("chrome") String browser) {
        switch (browser.toLowerCase()) {
            case "chrome":
                driver = new ChromeDriver();
                break;
            case "edge":
                driver = new EdgeDriver();
                break;
            case "firefox":
                driver = new FirefoxDriver();
                break;
            default:
                driver = new ChromeDriver();
        }

        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        screenshotDir = new File(System.getProperty("user.dir"), "screenshots");
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs();
        }
        System.out.println("Screenshots folder: " + screenshotDir.getAbsolutePath());
    }

    @Test(priority = 1)
    public void openFacebookPage() {
        openFacebook();
    }

    @Test(priority = 2)
    public void createAccountPage() {
        clickCreateAccount();
    }

    @Test(priority = 3)
    public void fillDetails() {
        fillFormDetails("Eswari", "Yegireddi", "24", 2, "2004", "1", "9876543201");
    }

    @Test(priority = 4)
    public void submitFormAndValidateErrors() throws Exception {
        clickSignUp();

        SoftAssert softAssert = new SoftAssert();

        String passwordError = capturePasswordError();


        System.out.println("Password Error: " + passwordError);

        softAssert.assertTrue(passwordError.contains("Enter") || !passwordError.equals("Password error not displayed"),
                "Expected password error message not displayed");

        softAssert.assertAll();


    }

    public static void openFacebook() {
        driver.get("https://www.fb.com");
    }

    public static void clickCreateAccount() {
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Create new account"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("firstname")));
    }

    public static void fillFormDetails(String firstName, String lastName, String day, int monthIndex,
                                       String year, String genderValue, String phone) {
        driver.findElement(By.name("firstname")).sendKeys(firstName);
        driver.findElement(By.name("lastname")).sendKeys(lastName);

        new Select(driver.findElement(By.id("day"))).selectByVisibleText(day);
        new Select(driver.findElement(By.id("month"))).selectByIndex(monthIndex);
        new Select(driver.findElement(By.id("year"))).selectByValue(year);

        WebElement genderRadio = driver.findElement(By.xpath("//input[@name='sex' and @value='" + genderValue + "']"));
        genderRadio.click();

        driver.findElement(By.name("reg_email__")).sendKeys(phone);
    }

    public static void clickSignUp() {
        driver.findElement(By.name("websubmit")).click();
    }




    public static String capturePasswordError() {
        try {
            WebElement passwordErrorElement = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//div[@class='_5633 _5634 _53ij' and contains(text(),'Enter')]")
                    )
            );
            takeScreenshot("password_error_screenshot");
            return passwordErrorElement.getText();
        } catch (Exception e) {
            return "Password error not displayed";
        }
    }

    public static String takeScreenshot(String label) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = label + "_" + timestamp + ".png";
            File dest = new File(screenshotDir, fileName);
            Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Saved screenshot: " + dest.getAbsolutePath());
            return dest.getAbsolutePath();
        } catch (IOException e) {
            System.out.println("Failed to save screenshot: " + e.getMessage());
            return "";
        } catch (Exception e) {
            System.out.println("Screenshot error: " + e.getMessage());
            return "";
        }
    }

    @AfterClass
    public void quitBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }
}
