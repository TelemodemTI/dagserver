FROM maven:3.8.5-openjdk-17 as maven_builder
WORKDIR /app/
COPY / /app/dagserver/
WORKDIR /app/dagserver/
RUN ["mvn","--quiet","clean","install"]


FROM eclipse-temurin:17-jdk-alpine
COPY --from=maven_builder /app/dagserver/target/dagserver-0.8.0-SNAPSHOT.jar /
COPY --from=maven_builder /app/dagserver/selenium /
RUN mkdir /root/dags/
RUN mkdir /root/dags/drivers
RUN cp /mysql-connector-j-9.0.0.jar /root/dags/drivers/mysql-connector-j-9.0.0.jar
EXPOSE 8081

ENV env_name APP_JDBC_URL
ENV env_name APP_JDBC_USER
ENV env_name APP_JDBC_PASSWORD
ENV env_name APP_JDBC_DRIVER
ENV env_name APP_MIGRATION_JDBC_TYPE
ENV env_name APP_HIBERNATE_DIALECT
ENV env_name APP_EXTENSIONS
ENV env_name APP_PROFILES_DEFAULT
ENV env_name APP_FOLDERPATH
ENV env_name APP_USERNAME
ENV env_name APP_PWD
ENV env_name APP_INIT_API_KEY
ENV env_name APP_BACKGROUND_JOBS

#ENTRYPOINT ["tail", "-f", "/dev/null"]
ENTRYPOINT ["java","-jar","dagserver-0.8.0-SNAPSHOT.jar"]