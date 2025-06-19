package main.cl.dagserver.integration.pom;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class JobLogsDetailPage {
    private WebDriver driver;

    public JobLogsDetailPage(WebDriver driver){
        this.driver = driver;
        WebDriverWait wait = new WebDriverWait(this.driver,Duration.ofSeconds(3));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"page-wrapper\"]/div/div[1]/div/h1")));
    }
    
}
