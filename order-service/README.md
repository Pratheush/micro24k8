
### - Database Migrations with Flyway

<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>

## * By using Flyway, we can provide the necessary SQL scripts that will be executed whenever we need to change our database schema. We need to provide these scripts under the src/main/resources/db/migration folder.

## * Flyway will look for the scripts under this particular folder, and Flyway will also follow a particular naming convention to identify the SQL scripts, we need to name the files like below:

## * V<Number>__<file-name>.sql

## * Example: V1__init.sql, V2__add_products.sql, etc.

## * Note that the number, inside the name of the SQL file, needs to be incremented for each database migration you want to run.


## * Let’s create the below file to create the Order table

## * V1__init.sql

CREATE TABLE `t_orders`
(
`id`          bigint(20) NOT NULL AUTO_INCREMENT,
`order_number` varchar(255) DEFAULT NULL,
`sku_code`  varchar(255),
`price`    decimal(19, 2),
`quantity` int(11),
PRIMARY KEY (`id`)
);

================================================================

## - We will implement Synchronous Communication between Order Service and Inventory Service using the Spring Cloud OpenFeign library.

## use Docker to install Apache Kafka together with Zookeeper
## use a Kafka UI to see the topics and messages in our Kafka Cluster using the Kafka UI project


## We will define our schema in avro format, for that reason we need to also add the avro and kafka-avro-serializer dependencies.

<dependency>
    <groupId>io.confluent</groupId>
    <artifactId>kafka-avro-serializer</artifactId>
    <version>7.6.0</version>
</dependency>
<dependency>
    <groupId>io.confluent</groupId>
    <artifactId>kafka-schema-registry-client</artifactId>
    <version>7.6.0</version>
</dependency>
<dependency>
    <groupId>org.apache.avro</groupId>
    <artifactId>avro</artifactId>
    <version>1.11.3</version>
</dependency>

## * After adding the above dependencies, now it’s time to implement the logic to send an event to the kafka topic whenever there is an order placed in the order-service. We will first start by defining the avro-schema of the event we want to send. And we will define the schema in a .avsc file, avsc is the format to define the Avro schema, let’s add the below file under src/main/resources/avro folder.

order-placed.avsc
{
"type": "record",
"name": "OrderPlacedEvent",
"namespace": "com.techie.microservices.order.event",
"fields": [
{ "name": "orderNumber", "type": "string" },
{ "name": "email", "type": "string" },
{ "name": "firstName", "type": "string" },
{ "name": "lastName", "type": "string" }
]
}
## Here we have a few fields orderNumber, email, firstName, and lastName that are used to send notifications to the user whenever an order is placed successfully.
## The idea is to generate the Java classes automatically using this schema, so if there is a change in the schema file, then those changes will be automatically applied during the build time.

## To be able to generate the Java classes automatically, we are going to use the avro-maven-plugin:
