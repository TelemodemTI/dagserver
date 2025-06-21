package main.cl.dagserver.integration.pom.segments;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ErrorWhenCompileModal {
    private WebDriver driver;
    public ErrorWhenCompileModal(WebDriver driver){
        this.driver = driver;
        WebDriverWait wait = new WebDriverWait(this.driver,Duration.ofSeconds(3));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"errorUncompiled\"]")));
        WebDriverWait wait9 = new WebDriverWait(this.driver,Duration.ofSeconds(3));
		wait9.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"errorUncompiled\"]")));
    }

    public void close(){
        driver.findElement(By.xpath("//*[@id=\"errorUncompiled\"]/div[2]/div/div[3]/button")).click();
		WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id=\"errorUncompiled\"]")));
    }
}
