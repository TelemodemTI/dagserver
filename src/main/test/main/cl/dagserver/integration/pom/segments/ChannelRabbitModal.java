package main.cl.dagserver.integration.pom.segments;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ChannelRabbitModal {
    private WebDriver driver;

    public ChannelRabbitModal(WebDriver driver) {
        this.driver = driver;
        WebDriverWait wait2 = new WebDriverWait(this.driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"rabbitModal\"]")));
    }
    public void configuraServer(String host,Integer port, String keystore) throws InterruptedException{
        this.driver.findElement(By.xpath("//*[@id=\"rabbitModal\"]/div[2]/div/div[2]/div[1]/input")).clear();
        this.driver.findElement(By.xpath("//*[@id=\"rabbitModal\"]/div[2]/div/div[2]/div[1]/input")).sendKeys(host);

        this.driver.findElement(By.xpath("//*[@id=\"rabbitModal\"]/div[2]/div/div[2]/div[2]/input")).clear();
        this.driver.findElement(By.xpath("//*[@id=\"rabbitModal\"]/div[2]/div/div[2]/div[2]/input")).sendKeys(port.toString());
        
        Select select = new Select(driver.findElement(By.xpath("//*[@id=\"rabbitModal\"]/div[2]/div/div[2]/div[3]/select")));
        select.selectByValue(keystore);

        this.driver.findElement(By.xpath("//*[@id=\"rabbitModal\"]/div[2]/div/div[2]/button")).click();
        Thread.sleep(3000);
    }

    public void bindQueue(String queue){
        this.driver.findElement(By.xpath("//*[@id=\"props-collapser-son-1\"]")).click();
        WebDriverWait wait2 = new WebDriverWait(this.driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"collapseNewQueue\"]/div[1]/input")));
        this.driver.findElement(By.xpath("//*[@id=\"collapseNewQueue\"]/div[1]/input")).clear();
        this.driver.findElement(By.xpath("//*[@id=\"collapseNewQueue\"]/div[1]/input")).sendKeys(queue);
    }
}
