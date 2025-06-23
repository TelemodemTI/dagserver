package main.cl.dagserver.integration.pom;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.TestException;

import main.cl.dagserver.integration.pom.segments.ChannelActiveMQModal;
import main.cl.dagserver.integration.pom.segments.ChannelHttpModal;
import main.cl.dagserver.integration.pom.segments.ChannelKafkaModal;
import main.cl.dagserver.integration.pom.segments.ChannelRabbitModal;
import main.cl.dagserver.integration.pom.segments.ChannelRedisModal;

public class ChannelsPage {
    private WebDriver driver;

    public ChannelsPage(WebDriver driver) throws InterruptedException {
        this.driver = driver;
        WebDriverWait wait2 = new WebDriverWait(this.driver,Duration.ofSeconds(30));
        var header = By.xpath("//*[@id=\"page-wrapper\"]/div/div[1]/div/h1");
        wait2.until(ExpectedConditions.visibilityOfElementLocated(header));
        if(!this.driver.findElement(header).getText().equals("Channels")){
            throw new TestException("no desplego los channels inputs??");
        }
        Thread.sleep(3000);
    }
    public ChannelHttpModal openChannelHttpModal() throws InterruptedException{
    	Thread.sleep(3000);
        this.driver.findElement(By.xpath("//*[@id=\"HTTP_ENDPOINT-opener-dialog\"]")).click();
        return new ChannelHttpModal(this.driver);
    }
    public ChannelRabbitModal openChannelRabbitModal(){
        this.driver.findElement(By.xpath("//*[@id=\"RABBIT_PROPS-opener-dialog\"]")).click();
        return new ChannelRabbitModal(this.driver);
    }
    public ChannelRedisModal openChannelRedisModal(){
        this.driver.findElement(By.xpath("//*[@id=\"REDIS_PROPS-opener-dialog\"]")).click();
        return new ChannelRedisModal(this.driver);
    }
    public ChannelKafkaModal openChannelKafkaModal(){
        this.driver.findElement(By.xpath("//*[@id=\"KAFKA_CONSUMER-opener-dialog\"]")).click();
        return new ChannelKafkaModal(this.driver);
    }
    public ChannelActiveMQModal openChannelActiveMQModal(){
        this.driver.findElement(By.xpath("//*[@id=\"ACTIVEMQ_PROPS-opener-dialog\"]")).click();
        return new ChannelActiveMQModal(this.driver);
    }
}
