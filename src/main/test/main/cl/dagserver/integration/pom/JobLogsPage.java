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

public class JobLogsPage {
    private WebDriver driver;

    public JobLogsPage(WebDriver driver){
        this.driver = driver;
    }

    public List<Map<String, String>> getActualLogs() {
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTables-logs")));
        WebElement tabla = driver.findElement(By.id("dataTables-logs"));
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

    public void deleteById(String id) throws InterruptedException{
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTables-logs")));
        WebElement tabla = driver.findElement(By.id("dataTables-logs"));
        List<WebElement> filas = tabla.findElements(By.tagName("tr"));
        for (int i = 1; i < filas.size(); i++) {
            WebElement fila = filas.get(i);
            List<WebElement> columnas = fila.findElements(By.tagName("td"));
            WebElement idColumn = columnas.get(0);
            if(idColumn.getText().equals(id)) {
            	driver.findElement(By.xpath("//*[@id=\"dataTables-logs\"]/tbody/tr["+i+"]/td[4]/button[2]")).click();
            	break;
            }
        }
        Thread.sleep(3000);
    }

    public boolean existLog(String id) {
        Boolean found = false;
		var data = this.getActualLogs();
		for (Iterator<Map<String, String>> iterator = data.iterator(); iterator.hasNext();) {
			Map<String, String> map =  iterator.next();
			if(map.containsValue(id)) {
				found = true;
				break;
			}
		}
		return found;
    }

    public JobLogsDetailPage viewLog(String id) {
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTables-logs")));
        WebElement tabla = driver.findElement(By.id("dataTables-logs"));
        List<WebElement> filas = tabla.findElements(By.tagName("tr"));
        for (int i = 1; i < filas.size(); i++) {
            WebElement fila = filas.get(i);
            List<WebElement> columnas = fila.findElements(By.tagName("td"));
            WebElement idColumn = columnas.get(0);
            if(idColumn.getText().equals(id)) {
            	
            	WebDriverWait wait3 = new WebDriverWait(driver,Duration.ofSeconds(3));
                wait3.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"dataTables-logs\"]/tbody/tr["+i+"]/td[4]/button[1]")));
            	
            	driver.findElement(By.xpath("//*[@id=\"dataTables-logs\"]/tbody/tr["+i+"]/td[4]/button[1]")).click();
            	return new JobLogsDetailPage(driver);
            }
        }
        throw new TestException("No se encontró el log con el ID: " + id);
    }

    public void deleteAll() throws InterruptedException {
        driver.findElement(By.xpath("//*[@id=\"page-wrapper\"]/div/div[2]/div/div/div[2]/button[4]")).click();
		Thread.sleep(3000);
    }
}
