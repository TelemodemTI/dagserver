package main.cl.dagserver.integration.pom.segments;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class JarResultExecutionModal {
    private WebDriver driver;
    public JarResultExecutionModal(WebDriver driver){
        this.driver = driver;
        WebDriverWait wait4 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait4.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"propertyNotFoundModal\"]")));
    }

    public void close() {
        driver.findElement(By.xpath("//*[@id=\"propertyNotFoundModal\"]/div[2]/div/div[3]/button")).click();
        WebDriverWait wait5 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait5.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id=\"propertyNotFoundModal\"]")));
    }
}