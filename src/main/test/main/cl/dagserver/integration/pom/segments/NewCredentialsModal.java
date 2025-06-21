package main.cl.dagserver.integration.pom.segments;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class NewCredentialsModal {
    private WebDriver driver;
    
    public NewCredentialsModal(WebDriver driver){
        this.driver = driver;
		WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
		wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"addUserModal\"]")));
    }

    public void create(String username, String pwd, String accountType){
        driver.findElement(By.xpath("//*[@id=\"usernamepropinput\"]")).sendKeys(username);
		driver.findElement(By.xpath("//*[@id=\"pwdpropinput\"]")).sendKeys(pwd);
		driver.findElement(By.xpath("//*[@id=\"repwdpropinput\"]")).sendKeys(pwd);
		Select select = new Select(driver.findElement(By.xpath("//*[@id=\"accountTypeCombo\"]")));
        select.selectByValue(accountType);
        driver.findElement(By.xpath("//*[@id=\"addUserModal\"]/div[2]/div/div[3]/button[2]")).click();
    }
}
