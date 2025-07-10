B2B Supermarket Sales Management System
This project is a backend enterprise-grade application for managing sales operations for B2B customers of a supermarket chain. It is built using a microservices architecture with Java Spring Boot, PostgreSQL, and secured RESTful APIs using JWT authentication. Each microservice is containerized with Docker, and the project includes OpenAPI documentation and a Postman collection for testing.
Prerequisites
Before setting up the project, ensure you have the following installed:

Java 17+: For building and running the Spring Boot applications.
Maven 3.8+: For dependency management and building the project.
Docker: For containerizing and running the microservices.
Docker Compose: For orchestrating the microservices and PostgreSQL databases.
PostgreSQL Client (optional): For manual database inspection (e.g., psql or pgAdmin).
Postman: For testing the APIs using the provided Postman collection.

Project Structure
The project is organized into the following microservices, each with its own codebase:

customer-management-service: Manages B2B customer information.
product-management-service: Handles products, categories, and suppliers.
price-list-management-service: Manages product pricing for customer categories.
shop-management-service: Manages supermarket outlet details.
sales-management-service: Handles sales orders and invoices.
user-management-service: Manages users, roles, and privileges for API access.

Each microservice has its own:

Source code (Java Spring Boot).
PostgreSQL database schema (.sql files in src/main/resources/db).
Dockerfile for containerization.
OpenAPI (Swagger) documentation (available at /swagger-ui/index.html when running).

Setup Instructions
1. Clone the Repository
Clone the project repository to your local machine:
git clone https://github.com/your-repo/b2b-supermarket-sales-system.git
cd b2b-supermarket-sales-system

2. Configure Environment Variables
Each microservice requires environment variables for database credentials and JWT settings. Create a .env file in the root directory with the following structure:
# Database credentials for each microservice
CUSTOMER_DB_URL=jdbc:postgresql://localhost:5432/customer_db
CUSTOMER_DB_USERNAME=admin
CUSTOMER_DB_PASSWORD=securepassword

PRODUCT_DB_URL=jdbc:postgresql://localhost:5433/product_db
PRODUCT_DB_USERNAME=admin
PRODUCT_DB_PASSWORD=securepassword

PRICELIST_DB_URL=jdbc:postgresql://localhost:5434/pricelist_db
PRICELIST_DB_USERNAME=admin
PRICELIST_DB_PASSWORD=securepassword

SHOP_DB_URL=jdbc:postgresql://localhost:5435/shop_db
SHOP_DB_USERNAME=admin
SHOP_DB_PASSWORD=securepassword

SALES_DB_URL=jdbc:postgresql://localhost:5436/sales_db
SALES_DB_USERNAME=admin
SALES_DB_PASSWORD=securepassword

USER_DB_URL=jdbc:postgresql://localhost:5437/user_db
USER_DB_USERNAME=admin
USER_DB_PASSWORD=securepassword

# JWT Configuration
JWT_SECRET=your-secure-jwt-secret-key
JWT_EXPIRATION=86400000

Replace your-secure-jwt-secret-key with a strong secret key for JWT signing. Ensure the database credentials are secure and not exposed in version control.
3. Build the Microservices
Navigate to each microservice directory and build the project using Maven:
cd customer-management-service
mvn clean install
cd ../product-management-service
mvn clean install
cd ../price-list-management-service
mvn clean install
cd ../shop-management-service
mvn clean install
cd ../sales-management-service
mvn clean install
cd ../user-management-service
mvn clean install

Alternatively, build all services from the root directory:
mvn clean install -f customer-management-service/pom.xml
mvn clean install -f product-management-service/pom.xml
mvn clean install -f price-list-management-service/pom.xml
mvn clean install -f shop-management-service/pom.xml
mvn clean install -f sales-management-service/pom.xml
mvn clean install -f user-management-service/pom.xml

4. Set Up the Databases
Each microservice has a corresponding PostgreSQL database. Run the SQL scripts to create the database schemas:
psql -U admin -d customer_db -f customer-management-service/src/main/resources/db/schema.sql
psql -U admin -d product_db -f product-management-service/src/main/resources/db/schema.sql
psql -U admin -d pricelist_db -f price-list-management-service/src/main/resources/db/schema.sql
psql -U admin -d shop_db -f shop-management-service/src/main/resources/db/schema.sql
psql -U admin -d sales_db -f sales-management-service/src/main/resources/db/schema.sql
psql -U admin -d user_db -f user-management-service/src/main/resources/db/schema.sql

Ensure the PostgreSQL server is running, and the credentials match those in the .env file. Alternatively, the databases will be automatically created and initialized when using Docker Compose (see below).
5. Run the Application with Docker Compose
The project includes a docker-compose.yml file to orchestrate all microservices and their databases. From the root directory, run:
docker-compose up --build

This command:

Builds Docker images for each microservice.
Starts PostgreSQL containers for each database.
Starts the microservices, exposing their REST APIs on the following ports:
Customer Management: 8081
Product Management: 8082
Price List Management: 8083
Shop Management: 8084
Sales Management: 8085
User Management: 8086



To stop the application, press Ctrl+C or run:
docker-compose down

6. Access the APIs
Once the application is running, the APIs are available at:

Customer Management: http://localhost:8081/api/customers
Product Management: http://localhost:8082/api/products, /api/categories, /api/suppliers
Price List Management: http://localhost:8083/api/pricelists
Shop Management: http://localhost:8084/api/shops
Sales Management: http://localhost:8085/api/sales-orders, /api/invoices
User Management: http://localhost:8086/api/users

Each microservice requires JWT authentication. Obtain a JWT token by sending a POST request to the User Management Service:
curl -X POST http://localhost:8086/api/auth/login -H "Content-Type: application/json" -d '{"username":"admin","password":"admin123"}'

Use the returned token in the Authorization header for subsequent requests (e.g., Bearer <token>).
7. View API Documentation
Each microservice provides OpenAPI (Swagger) documentation. Access it at:

http://localhost:8081/swagger-ui/index.html (Customer Management)
http://localhost:8082/swagger-ui/index.html (Product Management)
http://localhost:8083/swagger-ui/index.html (Price List Management)
http://localhost:8084/swagger-ui/index.html (Shop Management)
http://localhost:8085/swagger-ui/index.html (Sales Management)
http://localhost:8086/swagger-ui/index.html (User Management)

8. Test the APIs with Postman
Import the provided B2B-Supermarket-Sales-API-Collection.json Postman collection (located in the postman directory) into Postman to test the APIs. The collection includes sample requests for all CRUD operations and authentication.
9. Additional Notes

Database Indexes: Indexes are applied on commonly searched fields (e.g., Customer Email, Product Name, Shop Name) as defined in the schema SQL files.
Data Encryption: Personal data like email addresses are encrypted in the database using Spring Security's encryption utilities.
UUIDs: All IDs use UUIDs for scalability, as recommended.
UML Diagrams: Refer to the docs directory for UML diagrams documenting the system architecture and data models.

Troubleshooting

Database Connection Issues: Verify the .env file credentials and ensure PostgreSQL containers are running (docker ps).
Port Conflicts: If ports 8081-8086 or 5432-5437 are in use, update the docker-compose.yml file to use different ports.
JWT Errors: Ensure the JWT_SECRET in the .env file is consistent across all microservices.
Build Failures: Run mvn clean install again and check for missing dependencies or Java version mismatches.

