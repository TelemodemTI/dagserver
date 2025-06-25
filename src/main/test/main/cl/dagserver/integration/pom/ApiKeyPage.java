package main.cl.dagserver.integration.pom;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.TestException;

public class ApiKeyPage {
    private WebDriver driver;
    public ApiKeyPage(WebDriver driver) {
        this.driver = driver;
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(30));
        var header = By.xpath("//*[@id=\"page-wrapper\"]/div/div[1]/div/h1");
        wait2.until(ExpectedConditions.visibilityOfElementLocated(header));
        if(!driver.findElement(header).getText().equals("Api Keys")){
            throw new TestException("no desplego las api keys??");
        }
    }

    public List<Map<String, String>> getActualApiKeys(){
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTables-apikey")));
        WebElement tabla = driver.findElement(By.id("dataTables-apikey"));
        List<Map<String, String>> datosTabla = new ArrayList<>();
        List<WebElement> filas = tabla.findElements(By.tagName("tr"));
        List<WebElement> titulos = driver.findElements(By.tagName("th"));
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
        
    public Boolean existApikey(String propkey){
        Boolean found = false;
        var data = this.getActualApiKeys();
        for (Iterator<Map<String, String>> iterator = data.iterator(); iterator.hasNext();) {
            Map<String, String> map =  iterator.next();
            if(map.containsValue(propkey)) {
                found = true;
                break;
            }
        }
        return found;
    }
    public void createApiKey(String apiKey){
        driver.findElement(By.xpath("//*[@id=\"page-wrapper\"]/div/div[2]/div/div/div[2]/button")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.alertIsPresent());
        var alert = driver.switchTo().alert();
        alert.sendKeys(apiKey);
        alert.accept();
        driver.switchTo().defaultContent();
    }
    public void deleteApiKey(String apikey){
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTables-apikey")));
        WebElement tabla = driver.findElement(By.id("dataTables-apikey"));
        List<WebElement> filas = tabla.findElements(By.tagName("tr"));
        for (int i = 1; i < filas.size(); i++) {
            WebElement fila = filas.get(i);
            List<WebElement> columnas = fila.findElements(By.tagName("td"));
            WebElement userColumn = columnas.get(0);
            if(userColumn.getText().equals(apikey)) {
            	driver.findElement(By.xpath("//*[@id=\"dataTables-apikey\"]/tbody/tr["+i+"]/td[3]/button[1]")).click();
                break;
            }
        }
    }
}
