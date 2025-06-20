package main.cl.dagserver.integration.pom.segments;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ImportJsonDesignModal {
    private WebDriver driver;
    public ImportJsonDesignModal(WebDriver driver){
        this.driver = driver;
        WebDriverWait wait2 = new WebDriverWait(this.driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("importUncompiledModal")));
        WebDriverWait wait3 = new WebDriverWait(this.driver,Duration.ofSeconds(3));
        wait3.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"importUncompiledModal\"]")));
    }
    public void importDesign(String uploadFile) throws InterruptedException {
        String uploadFileReal = "/selenium/"+uploadFile;
		WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"importUncompiledModal\"]/div[2]/div/div[2]/div/input")));
		driver.findElement(By.xpath("//*[@id=\"importUncompiledModal\"]/div[2]/div/div[2]/div/input")).sendKeys(uploadFileReal);
        Thread.sleep(2000);
        driver.findElement(By.xpath("//*[@id=\"importUncompiledModal\"]/div[2]/div/div[3]/button[1]")).click();
    }
    
}