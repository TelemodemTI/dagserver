package main.cl.dagserver.integration.pom.segments;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class JarPreExecutionModal {
    private WebDriver driver;
    public JarPreExecutionModal(WebDriver driver) throws InterruptedException{
        this.driver = driver;
        WebDriverWait wait2 = new WebDriverWait(this.driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"compiled-args-value-inputer\"]")));
	    Thread.sleep(1000);
    }

    public JarResultExecutionModal save() {
        driver.findElement(By.xpath("//*[@id=\"compiled-args-value-inputer\"]/div[2]/div/div[3]/button[2]")).click();
        WebDriverWait wait3 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait3.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id=\"compiled-args-value-inputer\"]")));
        return new JarResultExecutionModal(driver);
    }
    public void setArgs(String args) throws InterruptedException {
        var input = By.xpath("//*[@id=\"compiled-args-value-inputer\"]/div[2]/div/div[2]/div/input");
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(input));
        driver.findElement(input).clear();
        driver.findElement(input).sendKeys(args);
        Thread.sleep(3000);
    }
}
