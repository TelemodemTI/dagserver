package main.cl.dagserver.integration.pom.segments;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DesignNewPreExecutionModal {
    private WebDriver driver;
    public DesignNewPreExecutionModal(WebDriver driver){
        this.driver = driver;
        WebDriverWait wait4 = new WebDriverWait(this.driver,Duration.ofSeconds(30));
        wait4.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"uncompiled-args-value-inputer\"]/div[2]/div/div[3]/button[2]")));
    }

    public DesignNewResultModal save(){
        driver.findElement(By.xpath("//*[@id=\"uncompiled-args-value-inputer\"]/div[2]/div/div[3]/button[2]")).click();
        return new DesignNewResultModal(driver);
    }
}
