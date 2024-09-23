package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Assignment {

    private static WebDriver driver;
    private static JavascriptExecutor js;
    private static WebDriverWait wait;

    public static void main(String[] args) throws InterruptedException {

        Setup();
        driver.get("https://ecommerce.tealiumdemo.com/");
        Thread.sleep(4000);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PerformLogin();
        UpdateBilling();
    }

    //Setup selenium
    public static void Setup(){
        WebDriverManager
                .chromedriver()
                .clearDriverCache()
                .driverVersion("128.0.6613.120")
                .setup();

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        js = (JavascriptExecutor) driver;
    }

    //Perform Login
    public static void PerformLogin() throws InterruptedException {
        WebElement account = driver.findElement(By.linkText("ACCOUNT"));
        account.click();

        WebElement loginButton = driver.findElement(By.xpath("//*[@title = \"Log In\"]"));
        Thread.sleep(2000);
        loginButton.click();

        Thread.sleep(6000);
        WebElement emailTextBox = driver.findElement(By.id("email"));
        WebElement passwordTextBox = driver.findElement(By.id("pass"));

        emailTextBox.sendKeys("srijan33@gmail.com");
        passwordTextBox.sendKeys("kathmandu123");

        js.executeScript("window.scrollBy(0,300);");
        WebElement submitButton = driver.findElement(By.id("send2"));
        submitButton.click();
    }

    public static void UpdateBilling(){
        js.executeScript("window.scrollBy(0,400);");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("EDIT ADDRESS")));


        List<WebElement> elements = driver.findElements(By.linkText("EDIT ADDRESS"));
        WebElement account = elements.get(0);
        account.click();

        WebElement telephone = driver.findElement(By.id("telephone"));
        telephone.sendKeys("123-456-7890");

        WebElement street = driver.findElement(By.id("street_1"));
        street.sendKeys("Manbhawan , lalitpur");

        WebElement city = driver.findElement(By.id("city"));
        city.sendKeys("Kathmandu");

        WebElement selectElement = driver.findElement(By.id("region_id"));
        Select select = new Select(selectElement);
        select.selectByValue("5");

        WebElement zip = driver.findElement(By.id("zip"));
        zip.sendKeys("56000");

        WebElement country = driver.findElement(By.id("country_id"));
        Select selectCountry = new Select(country);
        selectCountry.selectByValue("US");

        WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
        submitButton.click();

      
    }

}
