package main.cl.dagserver.integration.pom.segments;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CanvasDagEditor {
    
    private WebDriver driver;

    public CanvasDagEditor(WebDriver driver){
        this.driver = driver;        
        WebDriverWait wait3 = new WebDriverWait(driver,Duration.ofSeconds(3));
		wait3.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"page-wrapper\"]/div/div[2]/div/div/div[1]")));
    }
    
    @SuppressWarnings("unchecked")
	public void generate(){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, 0);");
            
        WebDriverWait wait4 = new WebDriverWait(driver,Duration.ofSeconds(3));
	    wait4.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"create-dag-btn\"]")));
		driver.findElement(By.xpath("//*[@id=\"create-dag-btn\"]")).click();
		
		WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(5));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@id,'props-collapser')]")));
        
        List<String> arr = (List<String>) js.executeScript("let arr = [];jQuery('.tabpill').each(function () {arr.push(jQuery(this).text());}); return arr;");
        String name = arr.get(arr.size() - 1);
        driver.findElement(By.xpath("//*[@id=\"canvas-"+name+"\"]/a")).click();
        
        WebDriverWait wait3 = new WebDriverWait(driver,Duration.ofSeconds(5));
        wait3.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"props-collapser-"+name+"\"]")));
        driver.findElement(By.xpath("//*[@id=\"props-collapser-"+name+"\"]")).click();
    }

    @SuppressWarnings("unchecked")
	public void createCronDag(String jarname,String dagname, String group, String cronexpr) throws InterruptedException{
        this.generate();
        JavascriptExecutor js = (JavascriptExecutor) driver;
  		List<String> arr = (List<String>) js.executeScript("let arr = [];jQuery('.tabpill').each(function () {arr.push(jQuery(this).text());}); return arr;");
        String name = arr.get(arr.size() - 1);
		WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='daggroupinput-"+name+"']")));
        driver.findElement(By.xpath("//*[@id='daggroupinput-"+name+"']")).clear();
        driver.findElement(By.xpath("//*[@id='daggroupinput-"+name+"']")).sendKeys(group);
        driver.findElement(By.xpath("//*[@id='dagcroninput-"+name+"']")).clear();
        driver.findElement(By.xpath("//*[@id='dagcroninput-"+name+"']")).sendKeys(cronexpr);
        driver.findElement(By.xpath("//*[@id='dagnameinput-"+name+"']")).clear();
        driver.findElement(By.xpath("//*[@id='dagnameinput-"+name+"']")).sendKeys(dagname);
		driver.findElement(By.xpath("//*[@id=\"page-wrapper\"]/div/div[2]/div/div/div[2]/div[1]/div[1]/div/input")).click();
		Thread.sleep(3000);
		WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(10));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"canvas-"+dagname+"\"]/a")));
		driver.findElement(By.xpath("//*[@id=\"canvas-"+dagname+"\"]/a")).click();
		Thread.sleep(1000);
		WebDriverWait wait3 = new WebDriverWait(driver,Duration.ofSeconds(5));
		wait3.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"collapseOne"+dagname+"-det\"]/div[4]/button[1]")));
		driver.findElement(By.xpath("//*[@id=\"collapseOne"+dagname+"-det\"]/div[4]/button[1]")).click();
    }

    public void setName(String name) {
        By jarname = By.xpath("//*[@id=\"page-wrapper\"]/div/div[2]/div/div/div[2]/div[1]/div[1]/div/input");
		driver.findElement(jarname).clear();
		driver.findElement(jarname).sendKeys(name);
    }

    public void addStep(String dagname, String step1, String operator) throws InterruptedException {
        driver.findElement(By.xpath("//*[@id=\"props-collapser-son-"+dagname+"\"]")).click();
        Thread.sleep(3000);
        driver.findElement(By.xpath("//*[@id=\"stepinput-"+dagname+"\"]")).clear();
        driver.findElement(By.xpath("//*[@id=\"stepinput-"+dagname+"\"]")).sendKeys(step1);
    
        WebElement combo = driver.findElement(By.xpath("//*[@id=\"steptype-"+dagname+"\"]"));
        Select select = new Select(combo);
        select.selectByValue(operator);
        driver.findElement(By.xpath("//*[@id=\"collapseOne"+dagname+"\"]/div/a")).click();	
    }

    public EditorParamModal selectStage(String stepName) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        String str = "jQuery(\"#canvas-new-det\").val(\"{\\\"stepname\\\":\\\""+stepName+"\\\"}\");";
	    jsExecutor.executeScript(str);
	    String str2 = "jQuery(\"#canvas-new-det\").trigger(\"change\");";
	 
	     jsExecutor.executeScript(str2);
	     return new EditorParamModal(driver);
    }

    public void saveJar() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript("window.scrollTo(0, 0);"); 
        By button = By.xpath("//*[@id=\"save-jar-btn\"]");
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(3));
        wait.until(ExpectedConditions.elementToBeClickable(button));
        driver.findElement(button).click();
    }

    public void selectDag(String dagname) throws InterruptedException {
        Thread.sleep(3000);
        WebDriverWait wait2 = new WebDriverWait(driver,Duration.ofSeconds(5));
        wait2.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"canvas-"+dagname+"\"]/a")));
        driver.findElement(By.xpath("//*[@id=\"canvas-"+dagname+"\"]/a")).click();
        Thread.sleep(3000);
    }

    public void save() throws InterruptedException {
        Thread.sleep(3000);
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript("window.scrollTo(0, 0);"); 

		WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(3));
	    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"page-wrapper\"]/div/div[2]/div/div/div[2]/div[1]/div/button[2]")));
		driver.findElement(By.xpath("//*[@id=\"page-wrapper\"]/div/div[2]/div/div/div[2]/div[1]/div/button[2]")).click();
		Thread.sleep(2000);
		
		// Wait for the alert to be present and accept it
	    wait.until(ExpectedConditions.alertIsPresent());
	    driver.switchTo().alert().accept();
	    
	    Thread.sleep(2000);   
    }

    public void close() throws InterruptedException {
        Thread.sleep(2000);
        driver.findElement(By.xpath("//*[@id=\"page-wrapper\"]/div/div[2]/div/div/div[2]/div[1]/div/button[5]")).click();
    }

    public void createNoneDag(String dagname, String group) throws InterruptedException {
        this.generate();
		WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(5));
	    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(@id,'dagnameinput-')]")));
		
        driver.findElement(By.xpath("//*[contains(@id,'dagnameinput-')]")).clear();
        driver.findElement(By.xpath("//*[contains(@id,'dagnameinput-')]")).sendKeys(dagname);
        Thread.sleep(1000);
        driver.findElement(By.xpath("//*[contains(@id,'canvas-')]/a")).click();
        Thread.sleep(1000);

        driver.findElement(By.xpath("//*[contains(@id,'daggroupinput-')]")).clear();
        driver.findElement(By.xpath("//*[contains(@id,'daggroupinput-')]")).sendKeys(group);
		driver.findElement(By.xpath("//*[@id=\"none-type-link\"]")).click();
        Thread.sleep(5000);
    }

    public void copyProp(String dagname) throws InterruptedException {
        Thread.sleep(1000);
	    var button = By.xpath("//*[@id=\"params-collapser-" + dagname + "\"]");
	    driver.findElement(button).click();
	    Thread.sleep(1000);
	    WebElement elementToClick = driver.findElement(By.xpath("//*[@id=\"copy-prop\"]"));
	    elementToClick.click();
	    Thread.sleep(1000);
	    
	    WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(3));
	    wait.until(ExpectedConditions.alertIsPresent());
	    driver.switchTo().alert().accept();
	    Thread.sleep(3000);
	    driver.findElement(button).click();
	    Thread.sleep(1000);
    }

    public void copyOpts(String dagname) throws InterruptedException {
        Thread.sleep(1000);
		var button = By.xpath("//*[@id=\"params-collapser-"+dagname+"\"]");
		driver.findElement(button).click();
		Thread.sleep(1000);
		WebElement elementToClick = driver.findElement(By.xpath("//*[@id=\"copy-opts\"]"));
		elementToClick.click();
		Thread.sleep(1000);
		
		WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(3));
	    wait.until(ExpectedConditions.alertIsPresent());
	    driver.switchTo().alert().accept();

	    Thread.sleep(3000);
		driver.findElement(button).click();
	    Thread.sleep(1000);
    }

}
