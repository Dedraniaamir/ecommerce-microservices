# API Gateway Dockerfile
# Save as: api-gateway/Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/api-gateway-1.0.0.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xms512m -Xmx1024m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
