#Build War with maven
#FROM node:argon
FROM maven AS MAVEN_BUILD
WORKDIR /build
COPY pom.xml /build/
COPY src /build/src/
COPY target /build/target/
RUN mvn dependency:go-offline
RUN mvn package -Dmaven.test.skip=true
WORKDIR /build/target
EXPOSE 3001
CMD ["java", "-jar", "schoollab-0.0.1-SNAPSHOT.jar"]