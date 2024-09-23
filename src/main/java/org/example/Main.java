package org.example;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Set;

public class Main {

    private static WebDriver driver;
    private static JavascriptExecutor js;
    private static WebDriverWait wait;
    private static ExtentTest test;
    private static ExtentReports extent;

    public static void main(String[] args) throws InterruptedException , IOException{
       setupDriver();
       driver.get("https://ecommerce.tealiumdemo.com/");
       Thread.sleep(4000);

       wait = new WebDriverWait(driver,Duration.ofSeconds(20));
try {
    initializeReport();
    performLogin();
    verifyLogin();
    searchAndSelectItem();
    handleMultipleWindow();
} catch (Exception e) {
    test.fatal(e.toString());
}finally {
    cleanup();
}

       driver.close();
    }

    public static void initializeReport(){
        String timeStamp = new SimpleDateFormat("yy.MM.dd.HH.mm.ss").format(new Date());
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") +"\\target\\ExtentReports\\ExtentReport.html");
        htmlReporter.config().setDocumentTitle("Automation Report - "+timeStamp);
        htmlReporter.config().setReportName("Login Test Report - "+timeStamp);
        htmlReporter.config().setTheme(Theme.STANDARD);

        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);

        test = extent.createTest("E-commerce Login Test", "Sample test for login functionality");
    }

    public static String captureScreen(){
         TakesScreenshot screenshot = (TakesScreenshot) driver;
         System.out.println("Screenshot taken sucessfully");
         return screenshot.getScreenshotAs(OutputType.BASE64);
    }

    public static String takenScreenshots(String fileName){
        File fileSrc = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        String screenshotDir = System.getProperty("user.dir") + "/target/Screenshot/";
        createDirectory(screenshotDir);

        String filePath = screenshotDir + fileName;
        try {
            FileUtils.copyFile(fileSrc,new File(filePath));
        }catch (IOException e){
            System.out.println(e);
        }
        return filePath;
    }

    public static void createDirectory(String dirPath) {
        File directory = new File(dirPath);
        if(!directory.exists()){
            directory.mkdirs();
        }
    }

    public static void setupDriver() {
        WebDriverManager
                 .chromedriver()
                 .clearDriverCache()
                 .driverVersion("128.0.6613.120")
                 .setup();

        driver = new ChromeDriver();
        driver.manage().window().maximize();

        js = (JavascriptExecutor) driver;
     }

     public static void cleanup(){
         driver.quit();
         test.info("Driver quit");
         extent.flush();
     }

     public static void performLogin() throws InterruptedException {

         WebElement account = driver.findElement(By.linkText("ACCOUNT"));
         account.click();

         WebElement loginButton = driver.findElement(By.xpath("//*[@title = \"Log In\"]"));
         Thread.sleep(2000);
         loginButton.click();

         Thread.sleep(6000);
         WebElement emailTextBox = driver.findElement(By.id("email"));
         WebElement passwordTextBox = driver.findElement(By.id("pass"));

         emailTextBox.sendKeys("srijan33@gmail.com");
         passwordTextBox.sendKeys("kathmandu12");

         js.executeScript("window.scrollBy(0,300);");
         WebElement submitButton = driver.findElement(By.id("send2"));
         submitButton.click();
     }

     public static void verifyLogin() throws InterruptedException, IOException {
        Thread.sleep(5000);
        String welcomeMessage = driver.findElement(By.className("welcome-msg")).getText();
        try {
            Assert.assertEquals("WELCOME, SRIJAN KC!",welcomeMessage.toUpperCase());
            System.out.println("Login Successful!");
            test.pass("Login Successful!");
        }catch (AssertionError e)
        {

            System.out.println("Login Failed , Test failed");

            String screenshotPath = takenScreenshots("LoginFailure.png");
            test.fail("Login Failed!" + e.getMessage(), MediaEntityBuilder.createScreenCaptureFromBase64String(captureScreen()).build());
            extent.flush();

        }
     }

     public static void searchAndSelectItem() throws InterruptedException {

         WebElement searchField = driver.findElement(By.xpath("//input[@id='search']"));
         searchField.click();
         searchField.sendKeys("shirt");
         searchField.sendKeys(Keys.ENTER);
         System.out.println("Shirt entered.");

         wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'Core Striped Sport Shirt')]")));

         js.executeScript("window.scrollBy(0,300);");

         WebElement desiredShirt = driver.findElement(By.xpath("//a[contains(text(),'Core Striped Sport Shirt')]"));
         desiredShirt.click();

         wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='Core Striped Sport Shirt']")));

         System.out.println("Core Striped Sport Shirt selected");
         WebElement shirtHeading=driver.findElement(By.xpath("//span[text()='Core Striped Sport Shirt']"));
         String shirtHeadingCoreStriped = shirtHeading.getText();
         if(shirtHeadingCoreStriped.equalsIgnoreCase("Core Striped Sport Shirt")){
             System.out.println("Heading is appearing");
         }
         else {
             System.out.println("Heading is not present. Test Failed");
         }
     }

     public static void handleMultipleWindow() {
         String parentWindow = driver.getWindowHandle();
         js.executeScript("window.scrollBy(0,400);");
         WebElement footerMyAccount = driver.findElement(By.xpath("//div[@class='block-title']/following-sibling::ul[1]//a[@title='My Account']"));
         String openInNewTab = Keys.chord(Keys.CONTROL,Keys.RETURN);
         footerMyAccount.sendKeys(openInNewTab);
         wait.until(ExpectedConditions.numberOfWindowsToBe(2));
         switchToNewWindow(parentWindow);
     }

     public static void switchToNewWindow(String parentWindow){
        Set<String> allWindowHandles = driver.getWindowHandles();
         for(String windowHandle : allWindowHandles) {
              if(windowHandle.equals(parentWindow)) {
                  driver.switchTo().window(windowHandle);
                  break;
              }
         }
     }

}