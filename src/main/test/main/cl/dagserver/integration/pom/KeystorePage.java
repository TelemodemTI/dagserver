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

import main.cl.dagserver.integration.pom.segments.KeystoreImportModal;
import main.cl.dagserver.integration.pom.segments.NewKeystoreEntryModal;

public class KeystorePage {
	private WebDriver driver;
	public KeystorePage(WebDriver driver) {
		this.driver = driver;
		WebDriverWait wait = new WebDriverWait(this.driver,Duration.ofSeconds(3));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"page-wrapper\"]/div/div[1]/div/h1")));
	}
	public List<Map<String, String>> getActualKeystores(){
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTables-keystore")));
        WebElement tabla = driver.findElement(By.id("dataTables-keystore"));
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
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"dataTables-keystore_filter\"]/label/input")));
	    driver.findElement(By.xpath("//*[@id=\"dataTables-keystore_filter\"]/label/input")).clear();
	    driver.findElement(By.xpath("//*[@id=\"dataTables-keystore_filter\"]/label/input")).sendKeys(propkey);
	    Thread.sleep(3000);
    }    
    public Boolean existProp(String propkey){
        Boolean found = false;
        var data = this.getActualKeystores();
        for (Iterator<Map<String, String>> iterator = data.iterator(); iterator.hasNext();) {
            Map<String, String> map =  iterator.next();
            if(map.containsValue(propkey)) {
                found = true;
                break;
            }
        }
        return found;
    }

	public NewKeystoreEntryModal openNewKeystoreEntryModal(){
		driver.findElement(By.xpath("//*[@id=\"page-wrapper\"]/div/div[2]/div/div/div[2]/button[1]")).click();
		return new NewKeystoreEntryModal(driver);
	}
    public void downloadKeystore() throws InterruptedException {
        driver.findElement(By.xpath("//*[@id=\"page-wrapper\"]/div/div[2]/div/div/div[2]/button[2]")).click();
        Thread.sleep(1000);
    }
    public KeystoreImportModal openImportKeystoreModal() {
        driver.findElement(By.xpath("//*[@id=\"page-wrapper\"]/div/div[2]/div/div/div[2]/button[3]")).click();
        return new KeystoreImportModal(driver);
    }
    public void deleteKeystore(String keystoreAlias) throws InterruptedException {
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTables-keystore")));
        WebElement tabla = driver.findElement(By.id("dataTables-keystore"));
        List<WebElement> filas = tabla.findElements(By.tagName("tr"));
        for (int i = 1; i < filas.size(); i++) {
            WebElement fila = filas.get(i);
            List<WebElement> columnas = fila.findElements(By.tagName("td"));
            WebElement idColumn = columnas.get(0);
            if(idColumn.getText().equals(keystoreAlias)) {
            	driver.findElement(By.xpath("//*[@id=\"dataTables-keystore\"]/tbody/tr["+i+"]/td[3]/button")).click();
            	break;
            }
        }
        Thread.sleep(3000);
    }
}
