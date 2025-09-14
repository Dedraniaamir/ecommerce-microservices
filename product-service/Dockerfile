#Product Service Dockerfile
# Save as: product-service/Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/product-service-1.0.0.jar app.jar

EXPOSE 8082

ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Add health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8082/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]