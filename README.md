# dagserver
Dag server based on quartz, allows to execute batch processes modeled as DAG (Direct Acyclic graph). Inspired by Apache Airflow

## Requirements

You must have a Quartz-compatible database installation. Currently only tested using Mysql type (MariaDB).

## Installation

Once this war is compiled, it can be run on the latest version of TOMCAT 9. 
Docker image is provided [Docker-Hub](https://hub.docker.com/r/maximolira/dagserver) 
DockerFile used is provided here in repository for custom Docker deployment support.

## Basic configuration

The following are the basic configurations that are needed to run the application:

application.properties:  
- param.jwt_secret: Secret of token JWT for authentication  
- param.jwt_signer: Signer of token  
- param.jwt_subject: Subject of the token  
- param.jwt_ttl: Time to live  
- param.folderpath: Path from where the JAR files will be loaded, which contain the implementation of the DAGs to be executed  
	  
log4j.properties:  
- log4j.appender.file.File: Path from where the execution log file will be saved  
	
	
quartz.properties:  
- org.quartz.dataSource.quartzDS.URL: Quartz engine database host  
- org.quartz.dataSource.quartzDS.user: Quartz engine database user  
- org.quartz.dataSource.quartzDS.password: Quartz engine database password  
	
These last three variables can be overridden using the following environment variables:  
- APP_JDBC_URL  
- APP_JDBC_USER  	
- APP_JDBC_PASSWORD  
	  
## Basic Usage

Examples repo is here [dagserver-examples](https://github.com/maximolira/dagserver-examples)
The DAG implementation must be compiled into a JAR file (Maven Project) and sent to the path configured in folderpath param, then it will be visible to the scheduler engine.  
The Front-End application to search the logs and see the detail of each DAG can be found here [dagserver-examples](https://github.com/maximolira/dagserver-front)

## Credentials:

GraphQL Endpoint:  
  
- URL: http://<serverhost>:<serverport>/server/query  
- Username: dagserver  
- Password: dagserver  
  
The current graphql schema can be checked in the schema.graphql file located at the root of the classpath.  

## Run as Container

In the examples you can find the docker-compose.yml file for running the server within a container environment.

## License

<a href="https://www.buymeacoffee.com/maximolira" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/default-orange.png" alt="Buy Me A Coffee" height="41" width="174"></a>

  [Apache 2.0](LICENSE)
