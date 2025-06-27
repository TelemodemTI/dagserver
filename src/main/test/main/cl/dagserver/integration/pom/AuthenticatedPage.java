package main.cl.dagserver.integration.pom;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AuthenticatedPage {
    private WebDriver driver;
    public AuthenticatedPage(WebDriver driver) {
        this.driver = driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"page-wrapper\"]/div/div/div/h1")));
    }

    public PropertiesPage goToProperties(){
        driver.findElement(By.xpath("//*[@id=\"wrapper\"]/nav/ul/li[1]/a")).click();
        var propsLink = By.xpath("//*[@id=\"wrapper\"]/nav/ul/li[1]/ul/li[1]/a/div");
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait.until(ExpectedConditions.visibilityOfElementLocated(propsLink));
        driver.findElement(By.xpath("//*[@id=\"wrapper\"]/nav/ul/li[1]/ul/li[1]/a")).click();
        return new PropertiesPage(driver);
    }

    public CredentialsPage goToCredentials(){
        By title = By.xpath("//*[@id=\"page-wrapper\"]/div/div[1]/div/h1");
        By credentialsLink = By.xpath("//*[@id=\"side-menu\"]/li[2]/ul/li/a");
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait.until(ExpectedConditions.visibilityOfElementLocated(credentialsLink));
        driver.findElement(credentialsLink).click();
        return new CredentialsPage(driver,title);
    }
    public JobsPage goToJobs(){
        By jobsLink = By.xpath("//*[@id=\"side-menu\"]/li[1]/ul/li[1]/a");
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait.until(ExpectedConditions.elementToBeClickable(jobsLink));
        driver.findElement(jobsLink).click();
        return new JobsPage(driver);
    }

    public void logout(){

        By elem = By.xpath("//*[@id=\"logout-menu-btn\"]");
        WebDriverWait wait3 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait3.until(ExpectedConditions.elementToBeClickable(elem));
        driver.findElement(elem).click();
        By logoutBtn = By.xpath("//*[@id=\"logout-btn\"]");
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait.until(ExpectedConditions.visibilityOfElementLocated(logoutBtn));
        driver.findElement(logoutBtn).click();
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.invisibilityOfElementLocated(logoutBtn));
    }
        
    public KeystorePage goToKeystore(){
        driver.findElement(By.xpath("//*[@id=\"wrapper\"]/nav/ul/li[1]/a")).click();
        var propsLink = By.xpath("//*[@id=\"wrapper\"]/nav/ul/li[1]/ul/li[3]/a");
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait.until(ExpectedConditions.visibilityOfElementLocated(propsLink));
        driver.findElement(By.xpath("//*[@id=\"wrapper\"]/nav/ul/li[1]/ul/li[3]/a")).click();
        return new KeystorePage(driver);
    }

    public ApiKeyPage goToApiKey() {
        driver.findElement(By.xpath("//*[@id=\"wrapper\"]/nav/ul/li[1]/a")).click();
        var propsLink = By.xpath("//*[@id=\"wrapper\"]/nav/ul/li[1]/ul/li[2]/a/div");
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait.until(ExpectedConditions.visibilityOfElementLocated(propsLink));
        driver.findElement(By.xpath("//*[@id=\"wrapper\"]/nav/ul/li[1]/ul/li[2]/a")).click();
        return new ApiKeyPage(driver);        
    }
}
