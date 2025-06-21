package main.cl.dagserver.integration.pom.segments;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class JarDetailStepViewerModal {

    private WebDriver driver;
    public JarDetailStepViewerModal(WebDriver driver){
        this.driver = driver;
        WebDriverWait wait = new WebDriverWait(this.driver,Duration.ofSeconds(3));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"param-modaljardetailj\"]")));
        WebDriverWait wait2 = new WebDriverWait(this.driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"param-modaljardetailj\"]")));
    }
    public void close(){
        driver.findElement(By.xpath("//*[@id=\"param-modaljardetailj\"]/div[2]/div/div[3]/button[1]")).click();
        WebDriverWait wait3 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait3.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id=\"param-modaljardetailj\"]")));
    }
}
