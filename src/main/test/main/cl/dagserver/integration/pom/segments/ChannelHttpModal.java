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
import org.openqa.selenium.support.ui.WebDriverWait;

public class ChannelHttpModal {
    private WebDriver driver;

    public ChannelHttpModal(WebDriver driver) {
        this.driver = driver;
        WebDriverWait wait2 = new WebDriverWait(this.driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"httpModal\"]")));
    }
    public void createApiKey(String name) throws InterruptedException{
        this.driver.findElement(By.xpath("//*[@id=\"props-collapser-son\"]")).click();
        Thread.sleep(3000);
        this.driver.findElement(By.xpath("//*[@id=\"collapseNewHttp\"]/div/input")).clear();
        this.driver.findElement(By.xpath("//*[@id=\"collapseNewHttp\"]/div/input")).sendKeys(name);
        this.driver.findElement(By.xpath("//*[@id=\"collapseNewHttp\"]/button")).click();
    }

    public List<Map<String, String>> getActualApiKeys(){
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("github-tbl")));
        WebElement tabla = driver.findElement(By.id("github-tbl"));
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

    public void deleteApiKey(String alias) throws InterruptedException {
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("github-tbl")));
        WebElement tabla = driver.findElement(By.id("github-tbl"));
        List<WebElement> filas = tabla.findElements(By.tagName("tr"));
        for (int i = 1; i < filas.size(); i++) {
            WebElement fila = filas.get(i);
            List<WebElement> columnas = fila.findElements(By.tagName("td"));
            WebElement idColumn = columnas.get(0);
            if(idColumn.getText().equals(alias)) {
            	driver.findElement(By.xpath("//*[@id=\"github-tbl\"]/tbody/tr["+i+"]/td[3]/button")).click();
            	break;
            }
        }
        Thread.sleep(3000);
    }
}
