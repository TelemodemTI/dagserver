# dagserver

<p align="center">
  <img src="https://github.com/maximolira/dagserver/blob/00d8ea73307ee900288aba8d851c81a728528e33/front/src/assets/favicon.png?raw=true"
         alt="Sponsored by Evil Martians" width="150" height="150">
<p>

Dag server based on quartz, allows to execute batch processes modeled as DAG (Direct Acyclic graph). Inspired by Apache Airflow and IBM Datastage.

![CI/CD](https://github.com/maximolira/dagserver/actions/workflows/CICD.yaml/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=dagserver&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=dagserver)



## Documentation

The documentation is available here: [Documentation](https://docs.telemodem.cl/books/dagserver-documentation)

## Installation

This project is based on Springboot, so it can be executed using the standard java command.
A Docker image is provided [Docker-Hub](https://hub.docker.com/r/maximolira/dagserver).

## Basic configuration

The following are the basic configurations that are needed to run the application:

application.properties:  
- param.hibernate.dialect: Hibernate Dialect
- param.flyway.migrations: Migration path for initial database load
- param.jwt_secret: Secret of token JWT for authentication  
- param.jwt_signer: Signer of token  
- param.jwt_subject: Subject of the token  
- param.jwt_ttl: Time to live  
- param.folderpath: Path from where the JAR files will be loaded, which contain the implementation of the DAGs to be executed  
- param.xcompath: Path where the temporary files generated by the XCOM will be stored 
- param.storage.exception: Path where the trace file is located. This file allows you to monitor the exceptions that are executed in the scheduler during background processes

quartz.properties:  
- org.quartz.dataSource.quartzDS.driver: Quartz engine driver database
- org.quartz.dataSource.quartzDS.URL: Quartz engine database host  
- org.quartz.dataSource.quartzDS.user: Quartz engine database user  
- org.quartz.dataSource.quartzDS.password: Quartz engine database password  
	
The following are the defined environment variables
- APP_JDBC_DRIVER: overwrites the variable corresponding to the Quartz database driver
- APP_JDBC_URL: overwrites the variable corresponding to the Quartz engine database host  
- APP_JDBC_USER: overwrites the variable corresponding to the Quartz engine database user  
- APP_JDBC_PASSWORD: overwrites the variable corresponding to the Quartz engine database pwd    
- APP_MIGRATION_JDBC_TYPE: overwrites the variable with the location of the initial migrations. currently there is support for h2 and mysql
- APP_HIBERNATE_DIALECT: overwrites the hibernate dialect variable to be used by the scheduler
	  
## Basic Usage

Dagserver provides a user-friendly web-based interface accessible at http://hostname:port/, allowing you to streamline your DAG (Directed Acyclic Graph) workflow management. With this interface, you can:

1. **Create and Compile DAGs:** Easily design, implement, and compile DAGs into standard Java JAR files. These JAR files contain the DAG implementations ready for execution.

2. **Schedule DAGs:** Utilize Quartz expressions or Cron schedules to schedule the execution of your DAGs. This powerful feature allows you to automate and manage when your DAGs run.

3. **Chain DAG Executions:** Similar to the functionality provided by the Quartz framework, you can chain the execution of DAGs by configuring Listeners. These listeners can be triggered at the start or end of the execution of other DAGs, enabling complex workflow orchestration.

4. **Flexible Execution Channels:** DAGserver supports multiple execution channels, allowing you to trigger the execution of a DAG when a specific event occurs in an input channel. This flexibility enhances the versatility of your DAG workflows.

The web-based editor provided by the Front-End makes designing, implementing, and managing DAGs intuitive and efficient. It also provides easy access to logs and detailed DAG execution information.

These features empower you to create, schedule, and manage your DAGs seamlessly, adapting to various use cases and execution scenarios.

## Credentials:

GraphQL Endpoint:  
  
- Front-End: http://<serverhost>:<serverport>/
- URL: http://<serverhost>:<serverport>/query  
- Username: dagserver  
- Password: dagserver  
  
The current graphql schema can be checked in the schema.graphql file located at the root of the classpath.  

## Selenium Test project

This related project is dedicated to performing frontend tests on the "dagserver" application using Selenium and testNG:
[dagserver-selenium](https://github.com/maximolira/dagserver-selenium).

## License

<a href="https://www.buymeacoffee.com/maximolira" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/default-orange.png" alt="Buy Me A Coffee" height="41" width="174"></a>

  [Apache 2.0](LICENSE)
