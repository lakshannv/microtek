FROM tomcat:9.0.68-jre8-temurin-focal
COPY microtek.war /usr/local/tomcat/webapps/
EXPOSE 8080
