
The Inventory Service exposes only 1 endpoint, similar to the Order Service, here is a brief overview of the endpoint:

Service Operation	Endpoint Method	Service Endpoint
GET Inventory	GET	/api/inventory
REST Operations for Inventory Service
As we are using MySQL Database also for the inventory service, we need to first update the mysql/init.sql file with the SQL commands to create the inventory database.

Let’s also create the Flyway migration scripts under the src/main/resources/db/migration folder, here we will be creating 2 scripts:
– V1__init.sql
– V2__add_inventory.sql

The V1__init.sql file as the name suggests, creates the t_inventory table