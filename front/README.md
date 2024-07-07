# dagserver
Dag server based on quartz, allows to execute batch processes modeled as DAG (Direct Acyclic graph). Inspired by Apache Airflow

## Quick Start

Dagserver is a Spring Boot application, so it can be executed in any of the ways supported by the framework. However, the recommended way is to run using a Docker image. You can find examples of a DockerFile and docker-compose files in the repository.

To build the image using Docker:

```
docker build --tag 'dagserver' .
docker run -d -p 8081:8081 'dagserver'
```

or using docker-compose:

```
docker-compose up
```

Depending on your configuration and the integrations you want to implement on your server, you may need to expose additional ports. In a production environment, it is recommended to define a volume to store the files necessary for the operation of the server. These files are located inside the container in the /root/dags path.

It is possible to run dagserver in HA mode using an external database. This can be configured in the quartz.properties file available in the resources folder.

	  
## Credentials:

Default frontend:  
  
- URL: http://<serverhost>:<serverport> ej: http://localhost:8081
- Username: dagserver  
- Password: dagserver  

## License

<a href="https://www.buymeacoffee.com/maximolira" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/default-orange.png" alt="Buy Me A Coffee" height="41" width="174"></a>

  [Apache 2.0](LICENSE)
