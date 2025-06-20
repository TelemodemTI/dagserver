package main.cl.dagserver.integration.pom.segments;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RenameJarModal {
    private WebDriver driver;
    public RenameJarModal(WebDriver driver){
        this.driver = driver;
        WebDriverWait wait2 = new WebDriverWait(this.driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"existinj-value-inputer\"]")));
    }

    public void rename(String newjarname){
        driver.findElement(By.xpath("//*[@id=\"existinj-value-inputer\"]/div[2]/div/div[2]/div/input")).clear();
		driver.findElement(By.xpath("//*[@id=\"existinj-value-inputer\"]/div[2]/div/div[2]/div/input")).sendKeys(newjarname);
		driver.findElement(By.xpath("//*[@id=\"existinj-value-inputer\"]/div[2]/div/div[3]/button[2]")).click();
		WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id=\"value-inputer\"]")));
    }
}
