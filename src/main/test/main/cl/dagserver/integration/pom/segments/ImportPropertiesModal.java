package main.cl.dagserver.integration.pom.segments;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ImportPropertiesModal {
    private WebDriver driver;
    public ImportPropertiesModal(WebDriver driver) {
        this.driver = driver;
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
	    wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"importPropertiesModal\"]")));
    }
    public void importProperties(String filename) throws InterruptedException{
        String uploadFileReal = "/selenium/"+filename;
        driver.findElement(By.xpath("//*[@id=\"importPropertiesModal\"]/div[2]/div/div[2]/div/input")).sendKeys(uploadFileReal);

	    Thread.sleep(3000);
        driver.findElement(By.xpath("//*[@id=\"importPropertiesModal\"]/div[2]/div/div[3]/button[1]")).click();
    }
}
