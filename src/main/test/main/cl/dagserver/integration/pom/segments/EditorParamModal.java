package main.cl.dagserver.integration.pom.segments;

import java.time.Duration;
import java.util.Base64;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.TestException;

public class EditorParamModal {
    private WebDriver driver;
    public EditorParamModal(WebDriver driver){
        this.driver = driver;
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(3));
	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"param-modalexistingj\"]")));
    }

    public void selectTab(String tabSelector) throws InterruptedException {
        Thread.sleep(3000);
		WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(5));
        wait2.until(ExpectedConditions.elementToBeClickable(By.xpath(tabSelector)));
		driver.findElement(By.xpath(tabSelector)).click();
		Thread.sleep(3000);
    }
    public void sendParameter(String name, String value, String type) throws InterruptedException {
        Thread.sleep(1000);
		String inputName = "//*[@id=\"param-"+name+"-value\"]";
		WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(5));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(inputName)));
		if(type.equals("input")) {
			driver.findElement(By.xpath(inputName)).clear();
			driver.findElement(By.xpath(inputName)).sendKeys(value);	
		} else if(type.equals("list")) {
			Select select = new Select(driver.findElement(By.xpath(inputName)));
	        select.selectByValue(value);
		}
    }

    public void selectFile(String file) throws InterruptedException {
        Thread.sleep(3000);
        driver.findElement(By.xpath("//*[@id=\"filer\"]/div/table/tbody/tr/td[2]/button")).click();
        Thread.sleep(3000);
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        String str = "jQuery(\"#file-selector-filepath\").val(\""+file+"\");";
        jsExecutor.executeScript(str);
        String str2 = "jQuery(\"#file-selector-filepath\").trigger(\"change\");";
        jsExecutor.executeScript(str2);
        Thread.sleep(3000);
        String text = driver.findElement(By.xpath("//*[@id=\"filer\"]/div[1]/table/tbody/tr/td[3]/b")).getText();
        if(!text.equals("Configured")){
            throw new TestException("no se configuro el archivo??");
        }
    }
    public void save(){
        driver.findElement(By.xpath("//*[@id=\"param-modalexistingj\"]/div[2]/div/div[3]/button[3]")).click();
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id=\"param-modalexistingj\"]")));
    }

    public void remove() {
        driver.findElement(By.xpath("//*[@id=\"param-modalexistingj\"]/div[2]/div/div[3]/button[1]")).click();
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id=\"param-modalexistingj\"]")));        
    }

    public void sendScript(String cmd1) throws InterruptedException {
        Thread.sleep(2000);
		JavascriptExecutor js = (JavascriptExecutor)driver;
		StringBuilder str = new StringBuilder();
		String script = Base64.getEncoder().encodeToString(cmd1.getBytes());
		if(script.contains("'")) {
			str.append("jQuery('#canvas-codemirror-new-det').val(\""+script+"\");");
		} else {
			str.append("jQuery('#canvas-codemirror-new-det').val('"+script+"');");
		}
	    js.executeScript(str.toString());
	    String str2 = "jQuery('#canvas-codemirror-new-det').trigger('change');";
	    js.executeScript(str2);
    }

    public void setInputStatusType(String string) throws InterruptedException {
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(5));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"settings\"]/div[2]/select")));
        Select select = new Select(driver.findElement(By.xpath("//*[@id=\"settings\"]/div[2]/select")));
        select.selectByValue(string);
		Thread.sleep(1000);
    }

    public void sendRemote(String action, String remoter1 , String local1) throws InterruptedException {
		WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait2.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"remoter-action-selector\"]")));
        Select select = new Select(driver.findElement(By.xpath("//*[@id=\"remoter-action-selector\"]")));
        select.selectByValue(action);
        
        WebDriverWait wait3 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait3.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"remoter-file-1\"]")));
        driver.findElement(By.xpath("//*[@id=\"remoter-file-1\"]")).clear();
		driver.findElement(By.xpath("//*[@id=\"remoter-file-1\"]")).sendKeys(remoter1);	
		
		if(local1 != null && !local1.isEmpty()) {
			WebDriverWait wait5 = new WebDriverWait(driver,Duration.ofSeconds(3));
	        wait5.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"remoter-file-2\"]")));
	        driver.findElement(By.xpath("//*[@id=\"remoter-file-2\"]")).clear();
			driver.findElement(By.xpath("//*[@id=\"remoter-file-2\"]")).sendKeys(local1);	
		}
		
		
		WebDriverWait wait4 = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait4.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"remoter-file-btn\"]")));
        driver.findElement(By.xpath("//*[@id=\"remoter-file-btn\"]")).click();
        Thread.sleep(1000);
	}
}
