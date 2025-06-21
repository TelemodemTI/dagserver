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

import main.cl.dagserver.integration.pom.segments.ImportPropertiesModal;
import main.cl.dagserver.integration.pom.segments.NewPropertiesModal;

public class PropertiesPage {
    private WebDriver driver;
    public PropertiesPage(WebDriver driver) {
        this.driver = driver;
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(30));
        var header = By.xpath("//*[@id=\"page-wrapper\"]/div/div[1]/div/h1");
        wait2.until(ExpectedConditions.visibilityOfElementLocated(header));
        if(!driver.findElement(header).getText().equals("Properties")){
            throw new TestException("no desplego las properties??");
        }
    }

    public List<Map<String, String>> getActualProps(){
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTables-props")));
        WebElement tabla = driver.findElement(By.id("dataTables-props"));
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
    public void search(String propkey) throws InterruptedException{
    	Thread.sleep(3000);
    	WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"dataTables-props_filter\"]/label/input")));
	    driver.findElement(By.xpath("//*[@id=\"dataTables-props_filter\"]/label/input")).clear();
	    driver.findElement(By.xpath("//*[@id=\"dataTables-props_filter\"]/label/input")).sendKeys(propkey);
	    Thread.sleep(3000);
    }    
    public Boolean existProp(String propkey){
        Boolean found = false;
        var data = this.getActualProps();
        for (Iterator<Map<String, String>> iterator = data.iterator(); iterator.hasNext();) {
            Map<String, String> map =  iterator.next();
            if(map.containsValue(propkey)) {
                found = true;
                break;
            }
        }
        return found;
    }
    public ImportPropertiesModal importPropertiesModal(){
	    driver.findElement(By.xpath("//*[@id=\"page-wrapper\"]/div/div[2]/div/div/div[2]/button[3]")).click();
        return new ImportPropertiesModal(driver);
    }
    public NewPropertiesModal createNewPropertyModal(){
        driver.findElement(By.xpath("//*[@id=\"page-wrapper\"]/div/div[2]/div/div/div[2]/button[1]")).click();
        return new NewPropertiesModal(driver);
    }
    public void deleteProp(String propkey){
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTables-props")));
        WebElement tabla = driver.findElement(By.id("dataTables-props"));
        List<WebElement> filas = tabla.findElements(By.tagName("tr"));
        for (int i = 1; i < filas.size(); i++) {
            WebElement fila = filas.get(i);
            List<WebElement> columnas = fila.findElements(By.tagName("td"));
            WebElement userColumn = columnas.get(1);
            if(userColumn.getText().equals(propkey)) {
            	driver.findElement(By.xpath("//*[@id=\"dataTables-props\"]/tbody/tr["+i+"]/td[5]/button[1]")).click();
            	break;
            }
        }
    }
    public void exportSelectedProperties(){
        driver.findElement(By.xpath("//*[@id=\"page-wrapper\"]/div/div[2]/div/div/div[2]/button[2]")).click();
        driver.findElement(By.xpath("//*[@id=\"page-wrapper\"]/div/div[2]/div/div/div[2]/a")).click();
    }

    public void deleteByGroup(String group){
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTables-props")));
        WebElement tabla = driver.findElement(By.id("dataTables-props"));
        List<WebElement> filas = tabla.findElements(By.tagName("tr"));
        for (int i = 1; i < filas.size(); i++) {
            WebElement fila = filas.get(i);
            List<WebElement> columnas = fila.findElements(By.tagName("td"));
            WebElement userColumn = columnas.get(1);
            if(userColumn.getText().equals(group)) {
                driver.findElement(By.xpath("//*[@id=\"dataTables-props\"]/tbody/tr["+i+"]/td[5]/button[2]")).click();
                break;
            }
        }
    }
}
