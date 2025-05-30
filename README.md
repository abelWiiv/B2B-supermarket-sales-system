Customer Management Microservice

This microservice handles the management of B2B customer information for the Supermarket Sales Management System.

Technology Stack





Java 17



Spring Boot 3.2.5



PostgreSQL 15



JWT for authentication



Maven



Docker

Setup Instructions

Prerequisites





Docker



Maven



Java 17

Build and Run





Clone the repository:

git clone <repository-url>
cd customer-management-service



Build the Docker image:

docker-compose build



Start the services:

docker-compose up -d



Verify the services are running:

docker ps

Access the Application





API: http://localhost:8082/customer-management



Swagger UI: http://localhost:8082/customer-management/swagger-ui.html

API Endpoints





POST /api/v1/customers - Create a new customer



GET /api/v1/customers/{id} - Get customer by ID



GET /api/v1/customers - Get all customers (paginated)



PUT /api/v1/customers/{id} - Update customer



DELETE /api/v1/customers/{id} - Delete customer

Security





All endpoints are secured with JWT.



Required permissions: READ_CUSTOMER, CREATE_CUSTOMER, UPDATE_CUSTOMER, DELETE_CUSTOMER

Notes





Replace the JWT secret key in application.yml with a secure key in production.



The email field is not encrypted in this implementation. Use database-level encryption or a library like jasypt if needed.