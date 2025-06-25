package main.cl.dagserver.integration.pom.segments;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class NewKeystoreEntryModal {
    private WebDriver driver;

    public NewKeystoreEntryModal(WebDriver driver){
        this.driver = driver;
        WebDriverWait wait = new WebDriverWait(this.driver,Duration.ofSeconds(3));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"addKeyModal\"]")));
    }

    public void create(String alias, String username, String password) throws InterruptedException{
        
        driver.findElement(By.xpath("//*[@id=\"namepropkeyinput\"]")).clear();
        driver.findElement(By.xpath("//*[@id=\"namepropkeyinput\"]")).sendKeys(alias);
        driver.findElement(By.xpath("//*[@id=\"usernamepropkeyinput\"]")).clear();
        driver.findElement(By.xpath("//*[@id=\"usernamepropkeyinput\"]")).sendKeys(username);
        driver.findElement(By.xpath("//*[@id=\"pwdpropkeyinput\"]")).clear();
        driver.findElement(By.xpath("//*[@id=\"pwdpropkeyinput\"]")).sendKeys(password);
        Thread.sleep(3000);        
        driver.findElement(By.xpath("//*[@id=\"addKeyModal\"]/div[2]/div/div[3]/button[2]")).click();
        WebDriverWait wait = new WebDriverWait(this.driver,Duration.ofSeconds(3));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id=\"addKeyModal\"]")));
    }
}
