package main.cl.dagserver.integration.pom.segments;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class KeystoreImportModal {
    private WebDriver driver;

    public KeystoreImportModal(WebDriver driver) {
        this.driver = driver;
        WebElement uploader = driver.findElement(By.id("uploader-keystore"));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOf(uploader));
    }
    public void importKeystore(String uploadFile) throws InterruptedException {
        String uploadFileReal = "/selenium/"+uploadFile;
		WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"uploader-keystore\"]/div[2]/div/div[2]/div/input")));
		driver.findElement(By.xpath("//*[@id=\"uploader-keystore\"]/div[2]/div/div[2]/div/input")).sendKeys(uploadFileReal);
        Thread.sleep(2000);
        driver.findElement(By.xpath("//*[@id=\"uploader-keystore\"]/div[2]/div/div[3]/button[1]")).click();
        
        WebDriverWait wait3 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait3.until(ExpectedConditions.invisibilityOfElementLocated(By.id("uploader-keystore")));
        
    }
}
