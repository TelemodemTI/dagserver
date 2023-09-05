![Dagserver](https://github.com/maximolira/dagserver/blob/00d8ea73307ee900288aba8d851c81a728528e33/front/src/assets/favicon.png?raw=true)


# dagserver
Dag server based on quartz, allows to execute batch processes modeled as DAG (Direct Acyclic graph). Inspired by Apache Airflow and IBM Datastage.

![CI/CD](https://github.com/maximolira/dagserver/actions/workflows/CICD.yaml/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=dagserver&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=dagserver)


## Requirements

You must have a Quartz-compatible database installation. Currently only tested using Mysql type (MariaDB).

## Installation

Once this war file is compiled, it can be run on the latest version of TOMCAT 9.
A Docker image is provided [Docker-Hub](https://hub.docker.com/r/maximolira/dagserver).

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
- DAGSERVERURI
	  
## Basic Usage

Dagserver provides a user-friendly web-based interface accessible at http://localhost:8080/dagserver/cli, allowing you to streamline your DAG (Directed Acyclic Graph) workflow management. With this interface, you can:

1. **Create and Compile DAGs:** Easily design, implement, and compile DAGs into standard Java JAR files. These JAR files contain the DAG implementations ready for execution.

2. **Schedule DAGs:** Utilize Quartz expressions or Cron schedules to schedule the execution of your DAGs. This powerful feature allows you to automate and manage when your DAGs run.

3. **Chain DAG Executions:** Similar to the functionality provided by the Quartz framework, you can chain the execution of DAGs by configuring Listeners. These listeners can be triggered at the start or end of the execution of other DAGs, enabling complex workflow orchestration.

4. **Flexible Execution Channels:** Dagserver supports multiple execution channels. While currently, the primary channel involves scheduling processes using Quartz, it also offers an event-based execution channel. You can trigger a DAG execution when a specific event occurs in a particular GitHub repository. This flexibility enhances the versatility of your DAG workflows.

The web-based editor provided by the Front-End makes designing, implementing, and managing DAGs intuitive and efficient. It also provides easy access to logs and detailed DAG execution information.

These features empower you to create, schedule, and manage your DAGs seamlessly, adapting to various use cases and execution scenarios.

## Credentials:

GraphQL Endpoint:  
  
- Front-End: http://<serverhost>:<serverport>/dagserver/cli
- URL: http://<serverhost>:<serverport>/server/query  
- Username: dagserver  
- Password: dagserver  
  
The current graphql schema can be checked in the schema.graphql file located at the root of the classpath.  

## Run as Container

docker-compose.yml example file is provided for running the server within a container environment.

## License

<a href="https://www.buymeacoffee.com/maximolira" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/default-orange.png" alt="Buy Me A Coffee" height="41" width="174"></a>

  [Apache 2.0](LICENSE)
