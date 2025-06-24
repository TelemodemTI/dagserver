package main.cl.dagserver.integration.pom.segments;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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

    public void bindQueue(String queue, String jarfile, String dagname) throws InterruptedException{
        this.driver.findElement(By.xpath("//*[@id=\"props-collapser-son-1\"]")).click();
        WebDriverWait wait2 = new WebDriverWait(this.driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"collapseNewQueue\"]/div[1]/input")));
        this.driver.findElement(By.xpath("//*[@id=\"collapseNewQueue\"]/div[1]/input")).clear();
        this.driver.findElement(By.xpath("//*[@id=\"collapseNewQueue\"]/div[1]/input")).sendKeys(queue);
        Thread.sleep(1000);
        Select select = new Select(driver.findElement(By.xpath("//*[@id=\"collapseNewQueue\"]/div[2]/select")));
        select.selectByValue(jarfile);
        Thread.sleep(3000);
        Select select2 = new Select(driver.findElement(By.xpath("//*[@id=\"collapseNewQueue\"]/div[3]/select")));
        select2.selectByValue(dagname);
        Thread.sleep(1000);
        this.driver.findElement(By.xpath("//*[@id=\"collapseNewQueue\"]/button")).click();
        Thread.sleep(3000);
    }

    public List<Map<String, String>> getActualBindings(){
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("rabbit-tbl")));
        WebElement tabla = driver.findElement(By.id("rabbit-tbl"));
        List<Map<String, String>> datosTabla = new ArrayList<>();
        List<WebElement> filas = tabla.findElements(By.tagName("tr"));
        List<WebElement> titulos = tabla.findElements(By.tagName("th"));
        for (int i = 1; i < filas.size(); i++) {
            WebElement fila = filas.get(i);
            List<WebElement> columnas = fila.findElements(By.tagName("td"));
            Map<String, String> filaDatos = new HashMap<>();
            for (int j = 0; j < columnas.size(); j++) {
                WebElement columna = columnas.get(j);
                WebElement titulo = titulos.get(j);
                String nombreColumna = titulo.getText(); 
                String valorCelda = columna.getText();
                filaDatos.put(nombreColumna, valorCelda);
            }	            
            datosTabla.add(filaDatos);
        }
        return datosTabla;
    }
    public void close() {
        this.driver.findElement(By.xpath("//*[@id=\"rabbitModal\"]/div[2]/div/div[3]/button")).click();
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id=\"rabbitModal\"]")));
    }

}
