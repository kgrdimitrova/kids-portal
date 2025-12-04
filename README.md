# Kids Portal

## Project Overview
**Kids Portal** is a platform where children can explore and participate in various events, manage their daily activities, and stay organized. Users can view clubs, events, schedules, and payment details. Admins and trainers have additional roles and permissions to manage users, events, and payments.  

## Tech Stack
- **Language:** Java  
- **Frameworks:** Spring Boot, Spring Security, Spring Data JPA  
- **Database:** MySQL  
- **IDE:** IntelliJ IDEA  
- **Libraries/Tools:** Hibernate, Thymeleaf, Feign Client, H2 (for tests), Jakarta Validation  

## Modules / Microservices
- **kids-portal:** Main application for managing users, clubs, and events.  
- **payment-srvc:** Microservice responsible for managing payments related to events. Communicates with `kids-portal` through REST endpoints.  

## Setup Instructions
1. Clone the repository.
2. Configure the database in `application.properties`:

```properties
# Base configuration
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/kids_portal?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=root
logging.level.org.hibernate.persister.entity=ERROR

# API Weather
weather.api.key=80f293a00d4f693493d3e6dc5826af1c
weather.api.url=https://api.openweathermap.org/data/2.5
Start the payment-srvc microservice first (port 8084).

Start the kids-portal application (port 8083).

Both services run locally, communicating via REST API.

Key Features / Usage
Users: Register, login, view profile, and manage subscriptions to events.

Events: Create events, view details, update events. Events can automatically generate recurring trainings for the current month.

Clubs: View and manage clubs, schedules, and participants.

Payments: View and manage payments for events. Trainers can validate payments; admins can view all payment details.

Roles & Permissions:

Admin: Full access to users, events, and clubs. Can manage roles and statuses.

Trainer: Can create events, manage clubs, validate payments.

User: Can subscribe/unsubscribe to events, join/left clubs and view schedules.

Email Notifications: When an event is updated, all subscribed users receive an email notification.

API Integration (Kids Portal ↔ Payment Service)
The main application uses the following endpoints in payment-srvc:

Method	Endpoint	Description
POST	/payments	Create or update a payment (upsertPayment).
PUT	/payments/{eventId}/{userId}/status	Toggle payment status (PENDING ↔ PAID).
GET	/payments/event/{eventId}	Fetch all payments for a given event.
GET	/payments/user/{userId}	Fetch all payments for a given user.

Testing
Full coverage with unit tests, API tests, and integration tests.

Tests are located in the src/test folder and run via your IDE or mvn test.

H2 in-memory database is used for integration testing.

Future Improvements
Enhance notification system with SMS or push notifications.

Advanced scheduling features for recurring events.

Detailed reporting dashboard for admins and trainers.

Credits
Developed as a Java/Spring Boot microservice application with REST integration between kids-portal and payment-srvc.
