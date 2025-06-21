package main.cl.dagserver.integration.pom;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {
    private WebDriver driver;
    
    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public Boolean login(String username, String password) {
    	By selector = By.xpath("/html/body/app-root/app-login/div/div/div/div/div[2]/fieldset[1]/div[1]/input");
    	WebElement inputElement = driver.findElement(selector);
    	WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
    	wait2.until(ExpectedConditions.elementToBeClickable(selector));
    	inputElement.sendKeys(username);
        WebElement pwdElement = driver.findElement(By.xpath("/html/body/app-root/app-login/div/div/div/div/div[2]/fieldset[1]/div[2]/input"));
        pwdElement.sendKeys(password);
        driver.findElement(By.xpath("/html/body/app-root/app-login/div/div/div/div/div[2]/fieldset[1]/button")).click();
        try {
        	WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(3));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"wrapper\"]/nav/ul[1]/li/a")));
            return true;
		} catch (Exception e) {
			WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(30));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/app-root/app-login/div/div/div/div/div[2]/fieldset[1]/div[3]")));
            return false;
		}
    }


}
