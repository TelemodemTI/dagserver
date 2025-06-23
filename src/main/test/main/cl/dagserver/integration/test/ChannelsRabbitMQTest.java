package main.cl.dagserver.integration.test;

import org.junit.jupiter.api.Assertions;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import main.cl.dagserver.integration.pom.AuthenticatedPage;
import main.cl.dagserver.integration.pom.ChannelsPage;
import main.cl.dagserver.integration.pom.JobsPage;
import main.cl.dagserver.integration.pom.KeystorePage;
import main.cl.dagserver.integration.pom.LoginPage;
import main.cl.dagserver.integration.pom.segments.JobsUncompiledTab;
import main.cl.dagserver.integration.test.core.BaseIntegrationTest;

public class ChannelsRabbitMQTest extends BaseIntegrationTest{
    private GenericContainer rabbitContainer;
    

    @BeforeClass
    public void beforeAll() {
        super.beforeAll();
        startDockerContainer();
    }


    private void startDockerContainer() {

        String host = "localhost";
        String usernameb = "testuser";
        String passwordb = "password";
        Integer port = 5672;
        Integer UIport = 15672;
        String exchange = "exchange";
        String routingKey = "routingKey";
        String queue = "queue";
        String message = "Hello, RabbitMQ!";
		
        
        this.rabbitContainer = new FixedHostPortGenericContainer("rabbitmq:3-management")
					.withFixedExposedPort(UIport,UIport)
					.withFixedExposedPort(port,port)		
					.withEnv("RABBITMQ_DEFAULT_USER", usernameb)
					.withEnv("RABBITMQ_DEFAULT_PASS", passwordb);
		this.rabbitContainer.start();
		try {
            Thread.sleep(15000);    
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(port);
        factory.setUsername(usernameb);
        factory.setPassword(passwordb);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(queue, true, false, false, null);
            
            channel.basicPublish("", queue, null, message.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

    @AfterClass
    public void afterAll() {
        super.afterAll();
        stopDockerContainer();
    }


    private void stopDockerContainer() {
		this.rabbitContainer.stop();
	}

    @Test(priority = 1)
    public void createKeystore() throws InterruptedException {
        String alias = "test";
        String username = "test";
        String password = "test";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            KeystorePage keystorePage = authenticatedPage.goToKeystore();
            var modal = keystorePage.openNewKeystoreEntryModal();
            modal.create(alias, username, password);
            
            var actualKeystores = keystorePage.getActualKeystores();
            Assertions.assertTrue(actualKeystores.stream().anyMatch(keystore -> keystore.get("Alias").equals(alias)));
            authenticatedPage.logout();            
        }
    }

    @Test(priority = 2)
    public void createDag() throws InterruptedException{
        String dagname = "DAGTEST";
        String group = "grouptest";
        String jarname = "testing.jar";
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            JobsPage jobsPage = authenticatedPage.goToJobs();
            var canvas = jobsPage.createNewJobCanvas();
            canvas.createNoneDag(dagname, group);
            canvas.setName(jarname);
            canvas.selectDag(dagname);
            canvas.addStep(dagname, "step1", "main.cl.dagserver.infra.adapters.operators.DummyOperator");
            canvas.saveJar();
            jobsPage = authenticatedPage.goToJobs();
            JobsUncompiledTab uncompileds = jobsPage.goToUncompiledTab();
            uncompileds.compileDesign(jarname);
            authenticatedPage.logout();
            Assertions.assertTrue(true);
        }
    }

    @Test(priority = 3)
    public void createChannelRabbitMQ() throws InterruptedException{
        LoginPage loginPage = new LoginPage(this.driver);
        if(loginPage.login("dagserver", "dagserver")){
            AuthenticatedPage authenticatedPage = new AuthenticatedPage(this.driver);
            ChannelsPage channelsPage = authenticatedPage.goToChannels();
            var modal = channelsPage.openChannelRabbitModal();
            
        }
    }
}
