FROM maven:3.6.1-jdk-11 as maven_builder
WORKDIR /app/
COPY / /app/dagserver/
WORKDIR /app/dagserver/
RUN ["mvn","clean","install"]


FROM tomcat:9.0.54-jdk11
COPY --from=maven_builder /app/dagserver/target/dagserver-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/
COPY --from=maven_builder /app/dagserver/start.sh /root/
RUN apt-get update
RUN apt-get install vim -y
RUN mv /usr/local/tomcat/webapps/dagserver-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war
RUN mkdir /root/dags/

EXPOSE 2587
EXPOSE 8080

ENV env_name DAGSERVERURI
ENV env_name APP_JDBC_URL
ENV env_name APP_JDBC_USER
ENV env_name APP_JDBC_PASSWORD
ENV env_name APP_JDBC_DRIVER
ENV env_name APP_MIGRATION_JDBC_TYPE
ENV env_name APP_HIBERNATE_DIALECT

WORKDIR  /root

#CMD ["catalina.sh", "run"]
#ENTRYPOINT ["tail", "-f", "/dev/null"]
CMD ["sh","start.sh"]
