FROM docker.devplaza.net:443/ubuntu-java:1.0
MAINTAINER Daniel Sünnen, https://github.com/danys
COPY application.yml blogggr.jar /app/
WORKDIR /app
EXPOSE 80
CMD ["nohup", "java", "-jar", "blogggr.jar"]