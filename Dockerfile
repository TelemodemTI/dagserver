FROM maven:3.8.5-openjdk-17 as maven_builder
WORKDIR /app/
COPY / /app/dagserver/
WORKDIR /app/dagserver/
RUN ["mvn","--quiet","clean","install"]


FROM eclipse-temurin:17-jdk-alpine
COPY --from=maven_builder /app/dagserver/target/dagserver-0.2.0-SNAPSHOT.jar /

EXPOSE 8081

ENV env_name APP_JDBC_URL
ENV env_name APP_JDBC_USER
ENV env_name APP_JDBC_PASSWORD
ENV env_name APP_JDBC_DRIVER
ENV env_name APP_MIGRATION_JDBC_TYPE
ENV env_name APP_HIBERNATE_DIALECT

#ENTRYPOINT ["tail", "-f", "/dev/null"]
ENTRYPOINT ["java","-jar","dagserver-0.2.0-SNAPSHOT.jar"]