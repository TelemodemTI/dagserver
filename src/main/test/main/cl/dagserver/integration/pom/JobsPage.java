package main.cl.dagserver.integration.pom;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import main.cl.dagserver.integration.pom.segments.CanvasDagEditor;
import main.cl.dagserver.integration.pom.segments.JobsCompiledTab;
import main.cl.dagserver.integration.pom.segments.JobsUncompiledTab;

public class JobsPage {

    private WebDriver driver;
    
    public JobsPage(WebDriver driver) {
        this.driver = driver;
    }
    
    public CanvasDagEditor createNewJobCanvas(){
        driver.findElement(By.xpath("//*[@id=\"page-wrapper\"]/div/div[2]/div/div/div[2]/a")).click();
        return new CanvasDagEditor(driver);
    }


    public JobsUncompiledTab goToUncompiledTab() throws InterruptedException{
        Thread.sleep(1000);
        WebDriverWait wait3 = new WebDriverWait(driver,Duration.ofSeconds(10));
        wait3.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"page-wrapper\"]/div/div[2]/div/div/div[2]/ul/li[2]/a")));
        driver.findElement(By.xpath("//*[@id=\"page-wrapper\"]/div/div[2]/div/div/div[2]/ul/li[2]/a")).click();
        return new JobsUncompiledTab(driver);
    }

    public JobsCompiledTab goToCompiledTab() throws InterruptedException{
        Thread.sleep(1000);
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"page-wrapper\"]/div/div[2]/div/div/div[2]/ul/li[1]/a")));
		driver.findElement(By.xpath("//*[@id=\"page-wrapper\"]/div/div[2]/div/div/div[2]/ul/li[1]/a")).click();
        return new JobsCompiledTab(driver);
    }

}
