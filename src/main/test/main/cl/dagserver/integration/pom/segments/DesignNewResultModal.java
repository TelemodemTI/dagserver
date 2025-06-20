package main.cl.dagserver.integration.pom.segments;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DesignNewResultModal {
    private WebDriver driver;
    public DesignNewResultModal(WebDriver driver){
        this.driver = driver;
        WebDriverWait wait2 = new WebDriverWait(this.driver,Duration.ofSeconds(30));
        wait2.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"result-step-modal\"]/div[2]/div/div[2]/div[1]")));
        WebDriverWait wait3 = new WebDriverWait(driver,Duration.ofSeconds(10));
        wait3.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"result-step-modal\"]/div[2]/div/div[2]/div[1]")));	
    }

    public void close(){
        driver.findElement(By.xpath("//*[@id=\"result-step-modal\"]/div[2]/div/div[2]/div[3]/button")).click();
		WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id=\"result-step-modal\"]")));
    }
}
