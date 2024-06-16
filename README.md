
# Getting Started

### Reference Documentation

### The tutorial series includes some changes below:

### - Spring Boot 3
### - Integration Tests using Latest Testing Techniques
### - Spring Cloud Feign for inter service communication
### - API Gateway implementation using Spring Cloud Gateway MVC instead of Spring Cloud Gateway with Webflux
### - Add Circuit Breaker Logic in API Gateway
### - Security using latest Keycloak
### - Distributed Tracing using Grafana Stack instead of Slueth
### - Deployment using Kubernetes

## * We are going to use Grafana Stack to implement Observability

### * Grafana

### * Grafana Loki
### * Grafana Tempo
### * Prometheus
### * Open Telemetry

* #### ***** we will use Grafana-loki to aggregate and view to aggregate the logs from our microservices and we are going to view this logs in Grafana using Grafana-Loki

* #### ***** we are going to use Grafana-Tempo for distributed tracing
* #### ***** we are going to use Prometheus to collect metrics
* #### ***** we are going to use Grafana to visualize the metrics of our applications of services in a beautiful dashboard
* #### ***** finally we will containerize all the services using Docker Then will run all the container using Docker-COmpose and then we will migrate this workload of Docker-Compose to Kubernetes