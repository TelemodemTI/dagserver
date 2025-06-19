package main.cl.dagserver.integration.pom;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class JarDetailPage {
    private WebDriver driver;

    public JarDetailPage(WebDriver driver){
        this.driver = driver;
    }

    public void selectTab(String tabSelector) throws InterruptedException {
        Thread.sleep(3000);
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(5));
        wait2.until(ExpectedConditions.elementToBeClickable(By.xpath(tabSelector)));
        driver.findElement(By.xpath(tabSelector)).click();
        Thread.sleep(3000);
    }

    public void selectStage(String dagname, String stepName) throws InterruptedException {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        String strcd = "jQuery(\"#canvas-det\").val(\""+dagname+";"+stepName+"\");";
        jsExecutor.executeScript(strcd);
        String str2 = "jQuery(\"#canvas-det\").trigger(\"change\");";
        jsExecutor.executeScript(str2);
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"param-modaljardetailj\"]")));
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"param-modaljardetailj\"]")));
        
        driver.findElement(By.xpath("//*[@id=\"param-modaljardetailj\"]/div[2]/div/div[3]/button[1]")).click();
        WebDriverWait wait3 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait3.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id=\"param-modaljardetailj\"]")));
    }
}
