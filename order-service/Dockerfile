# Order Service Dockerfile
# Save as: order-service/Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/order-service-1.0.0.jar app.jar

EXPOSE 8083

ENV JAVA_OPTS="-Xms512m -Xmx1536m"

# Add health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
  CMD curl -f http://localhost:8083/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]