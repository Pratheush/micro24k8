

DockerCompose KEYCLOAK -- MYSQL

version: '3.8'

services:
mysql:
image: mysql:8.0
container_name: keycloak-mysql
environment:
MYSQL_ROOT_PASSWORD: rootpassword
MYSQL_DATABASE: keycloak
MYSQL_USER: keycloak_user
MYSQL_PASSWORD: keycloak_password
ports:
- "3306:3306"
volumes:
- mysql-data:/var/lib/mysql

keycloak:
image: quay.io/keycloak/keycloak:20.0.3
container_name: keycloak
environment:
DB_VENDOR: mysql
DB_ADDR: mysql
DB_DATABASE: keycloak
DB_USER: keycloak_user
DB_PASSWORD: keycloak_password
KEYCLOAK_USER: admin
KEYCLOAK_PASSWORD: admin
ports:
- "8080:8080"
depends_on:
- mysql
command: ["start-dev"]

volumes:
mysql-data:



Explanation:
MySQL Service:

image: Specifies the MySQL Docker image version to use.
container_name: Sets a custom name for the MySQL container.
environment:
MYSQL_ROOT_PASSWORD: The root password for MySQL.
MYSQL_DATABASE: The name of the database to be created.
MYSQL_USER: The user for the Keycloak database.
MYSQL_PASSWORD: The password for the Keycloak database user.
ports: Exposes port 3306 on the host to access MySQL.
volumes: Mounts a volume for persistent MySQL data.
Keycloak Service:

image: Specifies the Keycloak Docker image version to use.
container_name: Sets a custom name for the Keycloak container.
environment:
DB_VENDOR: Specifies the database vendor (MySQL in this case).
DB_ADDR: The address of the MySQL service (using the service name mysql).
DB_DATABASE: The name of the Keycloak database.
DB_USER: The user for the Keycloak database.
DB_PASSWORD: The password for the Keycloak database user.
KEYCLOAK_USER: The admin username for Keycloak.
KEYCLOAK_PASSWORD: The admin password for Keycloak.
ports: Exposes port 8080 on the host to access Keycloak.
depends_on: Ensures that the MySQL service starts before the Keycloak service.
command: Uses start-dev for running Keycloak in development mode.
Volumes:

mysql-data: A volume to persist MySQL data across container restarts.

we are starting Keycloak in the dev environment using the ‘start-dev’ argument provided through the command field of the docker-compose configuration.