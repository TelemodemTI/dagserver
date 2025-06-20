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
import org.testng.TestException;

public class JobsUncompiledTab {

    private WebDriver driver;

    public JobsUncompiledTab(WebDriver driver){
        this.driver = driver;
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"dataTables-uncompiledjobs_filter\"]/label/input")));
    }

    public List<Map<String,String>> getActualDesigns(){
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTables-uncompiledjobs")));
        WebElement tabla = driver.findElement(By.id("dataTables-uncompiledjobs"));
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


    public Boolean existDesign(String designName){
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTables-uncompiledjobs")));
		Boolean found = false;
		var data = this.getActualDesigns();
		for (Iterator<Map<String, String>> iterator = data.iterator(); iterator.hasNext();) {
			Map<String, String> map =  iterator.next();
			if(map.containsValue(designName)) {
				found = true;
				break;
			}
		}
		return found;
    }

    
    public void searchUncompiled(String jarname) throws InterruptedException{
        Thread.sleep(3000);	
        driver.findElement(By.xpath("//*[@id=\"dataTables-uncompiledjobs_filter\"]/label/input")).clear();
		driver.findElement(By.xpath("//*[@id=\"dataTables-uncompiledjobs_filter\"]/label/input")).sendKeys(jarname);
		Thread.sleep(3000);	
    }

    public void deleteDesign(String designName){
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTables-uncompiledjobs")));
        WebElement tabla = driver.findElement(By.id("dataTables-uncompiledjobs"));
        List<WebElement> filas = tabla.findElements(By.tagName("tr"));
        for (int i = 1; i < filas.size(); i++) {
            WebElement fila = filas.get(i);
            List<WebElement> columnas = fila.findElements(By.tagName("td"));
            WebElement userColumn = columnas.get(1);
            if(userColumn.getText().equals(designName)) {
            	driver.findElement(By.xpath("//*[@id=\"dataTables-uncompiledjobs\"]/tbody/tr["+i+"]/td[4]/button[4]")).click();
            	break;
            }
        }
    }

    public CanvasDagEditor editDesign(String jarname) {
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTables-uncompiledjobs")));
        WebElement tabla = driver.findElement(By.id("dataTables-uncompiledjobs"));
        List<WebElement> filas = tabla.findElements(By.tagName("tr"));
        for (int i = 1; i < filas.size(); i++) {
            WebElement fila = filas.get(i);
            List<WebElement> columnas = fila.findElements(By.tagName("td"));
            WebElement userColumn = columnas.get(1);
            if(userColumn.getText().equals(jarname)) {
            	driver.findElement(By.xpath("//*[@id=\"dataTables-uncompiledjobs\"]/tbody/tr["+i+"]/td[4]/button[1]")).click();
                return new CanvasDagEditor(driver);
            }
        }
        throw new TestException("no se encontro el dag?");
    }

    public ImportJsonDesignModal importJarModal() {
        driver.findElement(By.xpath("//*[@id=\"templates\"]/app-uncompiled-tab/button")).click();
        return new ImportJsonDesignModal(driver);
    }

    public void compileDesign(String jarname) {
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTables-uncompiledjobs")));
        WebElement tabla = driver.findElement(By.id("dataTables-uncompiledjobs"));
        List<WebElement> filas = tabla.findElements(By.tagName("tr"));
        for (int i = 1; i < filas.size(); i++) {
            WebElement fila = filas.get(i);
            List<WebElement> columnas = fila.findElements(By.tagName("td"));
            WebElement userColumn = columnas.get(1);
            if(userColumn.getText().equals(jarname)) {
              if(i == 1){
                driver.findElement(By.xpath("//*[@id=\"dataTables-uncompiledjobs\"]/tbody/tr/td[4]/button[3]")).click();
              } else {
                driver.findElement(By.xpath("//*[@id=\"dataTables-uncompiledjobs\"]/tbody/tr["+i+"]/td[4]/button[3]")).click();
              }
            break;
            }
        }
        try {
        	WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(3));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"errorUncompiled\"]")));
            WebDriverWait wait9 = new WebDriverWait(driver,Duration.ofSeconds(3));
			wait9.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"errorUncompiled\"]")));
		} catch (Exception e) {
			
		}
    }
}
