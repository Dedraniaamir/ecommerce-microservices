# microservice-security-keycloak


download keycloak

1. Download keycloak through docker and use through docker image

docker run -p 127.0.0.1:9090:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:26.3.3 start-dev
