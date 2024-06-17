
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

