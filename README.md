
# Getting Started

to see product-service actuator metrics
then http://localhost:8086/actuator/metrics

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



### * Observability with Grafana Stack which comprises Grafana, Loki, and Tempo.

### * In a nutshell, Observability is the process of understanding the internal state of the application with the help of different indicators such as Logs, Metrics, and Tracing information.

### * Grafana Stack
### * Grafana Stack comprises about 3 softwares:
1. Grafana: This is the most widely used tool that helps to monitor and visualize the metrics of our application. Users can visualize the metrics by building different dashboards and can use different kinds of charts to visualize the metrics. We can also configure alerts to be notified whenever a metric reaches a certain required threshold.
   To collect metrics, we will be using Prometheus, a metrics aggregation tool.

2. Loki: is a Log Aggregation tool that receives the logs from our application and indexes the logs to be visualized using Grafana.
3. Tempo: is used as a distributed tracing tool, which can track requests that span across different systems.


### Promtail: Promtail is essentially an agent that takes its inspiration from Prometheus. Its primary role is to collect logs based on the configuration specified in the scraping settings. These logs are then transported to Loki for storage and further analysis.

### Loki: Loki serves as a specialized data store tailored for log storage. It efficiently stores logs, making them easily retrievable for analysis and monitoring purposes. Unlike traditional log storage systems, Loki’s design is optimized for scalability and performance in handling vast amounts of log data.

### Grafana: Grafana plays a crucial role in this monitoring stack. Once the logs are stored in Loki, Grafana steps in to visualize and present this data on a dashboard. Grafana’s user-friendly interface allows for the creation of insightful and customizable dashboards, making it easier for users to interpret and analyze the log data in real time.

### STORY :::
###### Imagine Promtail as a diligent messenger bee buzzing around, collecting tiny pieces of information (logs) from different flowers (scrape configurations). This bee then delivers its findings to Loki, a cozy hive specifically designed to store these bits of information. Finally, Grafana acts like a friendly beekeeper, skillfully organizing and presenting the honey (logs) from the hive on a beautiful and easy-to-read honeycomb-shaped dashboard. Together, they create a harmonious ecosystem where information flows seamlessly, just like a well-coordinated bee

# Loki Configuration: Set up Loki to collect logs from your services.
# Tempo Configuration: Configure Tempo to collect traces from your services.
# Grafana Configuration: Set up Grafana to visualize metrics, logs, and traces.
# Add Prometheus, Loki, and Tempo as data sources in Grafana.

#### Promtail and Loki each possess dedicated configuration files, These files serve to define essential parameters such as listening ports, client URLs, and scrape configurations, allowing for a flexible and externalized management approach.

***

# Distributed tracing ::
API-Gateway is getting a lot of Incoming Requests and suppose a lot of requests are hitting Notification-Service and if some requests are failing due to some reasons then
how do we trace each requests that are hitting Notification-Service from other number of incoming requests that are hitting Notification-Service
so we need some way to differentiate each requests we can do that using distributed tracing mechanism
so request is going from user to API-Gateway and forwarded to Order-Service and then forwarded to Notification-Service and if request is failing in Notification-Service we need some way to track the whole request from the start to beginning we can do that by something called Trace or trace-id. A trace is a information added to each request
that helps us to trace the request from its start to the end (api-gateway to notification-service) we can do that by trace-id.

so each request is assigned trace-id which we can use to trace request at any point.

we also have span-id which is another information i.e. unique identification of request in the one particular service

for example for a request coming from user suppose trace-id is 123 so trace-id will be the same when request is going forward from api-gateway to notification-service
but span-id will be different and unique in each services.

### Distributed Tracing Benefits
Distributed Tracing enables you to trace your request from start to end.
Distributed Tracing allows Flow of Request Visualization as request propagates through different services in a distributed system.it helps us to understand the dependencies and what kind of interactions there is happening with different services within the microservices network. it also gives us picture how our request is traversing through microservice architecture.

Distributed Tracing also allows us to Identify Performance Bottleneck and trace latency issues. thus we can pin-point the components that are contributing the delay of request in services or high response time.

Error Analysis and Debugging> Distributed helps us to identify what is the root cause of the error or which service or which component is failing

Tracking Dependency.

Performance Optimization: we can do capacity planning, usages of resources and scalability efforts by understanding the load and performance of different services
we can allocate more resources to a particular microservice that might be having more load and is acting slow and we can scale the service so that overall system through put improves

### Zipkin
zipkin is an open source distributed tracing system

#### In Distributed Tracing environment zipkin comprises collector, storage, search and visualization with collector here our application sends trace-data and collector will accept the data over transport modes like http, kafka or RabbitMQ, once the data is collected then data is stored in storage like MySQL, Cassandra, Elasticsearch. zipkin provides api or web-interface to search for traces in the storage based on criterias like name annotations or time and then zipkin helps in visualization of traced-data in the end using diagram or charts that how our request is flowed through the system , where time was spent and what happened when an issue occurred so it gives us hawk-eye with what is happening with the request in a sense by giving us lots of information along

### Micrometer
Micrometer provides insights that help us keep tabs on our applications performance. Micrometer helps in collecting different metrics from our application. these metrics provides insights that how our application is performing it gives us like response time, resource-utilization etc.
Micrometer acts as a middleman or a bridge between our application and the metrics collection systems.
Micrometer simplifies the process of collecting metrics from our application.

Micrometer provides the metrics in a way that can be scraped and stored by variety of monitoring system.

Micrometer, a vendor-neutral application metrics facade.


## Micrometer vs Zipkin
on the other hand zipkin is a distributed tracing system that tracks the path that the requests take as it travels through different microservices. it provides us the detailed view of single request showing us how long each step took and what service was responsible for delay on the way.

micrometer provides an application level view with aggregated metrics over time
aggregated metrics level view with micrometer

Distributed tracing tools like zipkin provide a request level view tracing the path latency of the individual request and not application as a whole
request level view with distributed tools like zipkin


* #### ***** we will use Grafana-loki to aggregate and view to aggregate the logs from our microservices and we are going to view this logs in Grafana using Grafana-Loki

* #### ***** we are going to use Grafana-Tempo for distributed tracing
* #### ***** we are going to use Prometheus to collect metrics
* #### ***** we are going to use Grafana to visualize the metrics of our applications of services in a beautiful dashboard
* #### ***** finally we will containerize all the services using Docker Then will run all the container using Docker-COmpose and then we will migrate this workload of Docker-Compose to Kubernetes



* #### NOTE: You can also use other tracing implementation like OpenTelemetry - micrometer-tracing-bridge-otel dependency instead of Brave - micrometer-tracing-bridge-brave
* #### ***** If you want to trace the calls to the database, as we are using Spring Data JDBC, we can add the dependency datasource-micrometer-spring-boot dependency.


USER >>> /api/order >>> API-Gateway >>>> Order-Service >>>>> Inventory-Service

     Span1------------------->   Span2-------------->     Span3----------------->
Trace-> traceID ____________________________________________________________\



![Distributed Tracing.png](..%2FDistributed%20Tracing.png)



![Microservices Architecture.png](..%2FMicroservices%20Architecture.png)

