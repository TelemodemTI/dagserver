package main.cl.dagserver.integration.pom.segments;

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

public class JobsCompiledTab {

    private WebDriver driver;

    public JobsCompiledTab(WebDriver driver){
        this.driver = driver;
    }

    public List<Map<String,String>> getActualJobs(){
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTables-jobs")));
        WebElement tabla = driver.findElement(By.id("dataTables-jobs"));
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
    public Boolean existJob(String propkey){
        Boolean found = false;
		var data = this.getActualJobs();
		for (Iterator<Map<String, String>> iterator = data.iterator(); iterator.hasNext();) {
			Map<String, String> map =  iterator.next();
			if(map.containsValue(propkey)) {
				found = true;
				break;
			}
		}
		return found;
    }
    public void selectOption(String dagname, Integer botonIndex) throws InterruptedException{
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTables-jobs")));
        WebElement tabla = driver.findElement(By.id("dataTables-jobs"));
        List<WebElement> filas = tabla.findElements(By.tagName("tr"));
        for (int i = 1; i < filas.size(); i++) {
            WebElement fila = filas.get(i);
            List<WebElement> columnas = fila.findElements(By.tagName("td"));
            WebElement dagColumn = columnas.get(4);
            if(dagColumn.getText().equals(dagname)) {
            	driver.findElement(By.xpath("//*[@id=\"dataTables-jobs\"]/tbody/tr["+i+"]/td[9]/button["+botonIndex+"]")).click();
            	break;
            }
        }
        Thread.sleep(3000);
    }

    public String getSchedulerActive(String dagname) {
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTables-jobs")));
        WebElement tabla = driver.findElement(By.id("dataTables-jobs"));
        List<WebElement> filas = tabla.findElements(By.tagName("tr"));
        for (int i = 1; i < filas.size(); i++) {
            
            WebElement fila = filas.get(i);
            List<WebElement> columnas = fila.findElements(By.tagName("td"));
            WebElement dagColumn = columnas.get(4);
            if(dagColumn.getText().equals(dagname)) {
              return columnas.get(5).getText();
            }
        }
		return null;
    }
    
    

    
}
