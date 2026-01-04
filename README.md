# 🚀 UrlShortener - High Performance Microservices Backend

A scalable, enterprise-grade **Spring Boot** application designed to handle URL shortening with high throughput. This project leverages a Microservices architecture featuring **Redis** for caching, **RabbitMQ** for asynchronous messaging, and a dedicated Email Service.

---

## 📊 Database Schema
The application uses a relational database to manage users, URL mappings, and analytics data efficiently.

![ERD](./screenshots/postman/erd-urlshortener.png)

---

## 💻 Visual Walkthrough

### 1. Infrastructure & Caching
The system uses **Redis** for low-latency caching and rate limiting, and **RabbitMQ** to handle asynchronous tasks like email notifications.

| Redis Caching & Rate Limiting | RabbitMQ Message Queues |
| :--- | :--- |
| ![Redis Cache](./screenshots/postman/cmd-redis-cache-url-and-rate-limit-keys.png) | ![RabbitMQ](./screenshots/postman/rabbit-mq-queues-cloud.png) |

### 2. User Authentication & Email Verification
Secure onboarding flow using asynchronous email verification tokens.

* **Registration:** Users receive a unique token via email.
* **Verification:** Secure endpoint to validate the account.
* **Validation:** Robust error handling for existing users.

| Email Token Received | Account Verified | Duplicate User Handling |
| :--- | :--- | :--- |
| ![Email Token](./screenshots/postman/gmail-verification-token.png) | ![Verified](./screenshots/postman/account-verified.png) | ![User Exists](./screenshots/postman/user-already-exists.png) |

### 3. Core Features & Analytics
Users can generate short links, create QR codes, and track usage data including IP addresses and click counts.

| QR Code Generation | Analytics (Click Events) | Rate Limiting Protection |
| :--- | :--- | :--- |
| ![QR Code](./screenshots/postman/create-qr.png) | ![Analytics](./screenshots/postman/click-events-with-user-ip.png) | ![Rate Limit](./screenshots/postman/too-manyreqs-limit-exceeded.png) |

---

## 🛠️ Project Architecture & Structure

The project follows a **Microservices-ready** layered architecture to ensure separation of concerns and scalability:

* **`com.vishal.urlshortener.config`**: Configurations for Redis, RabbitMQ, Swagger, and CORS.
* **`com.vishal.urlshortener.controller`**: REST APIs for Auth, URL operations, and Admin tasks.
* **`com.vishal.urlshortener.entity`**: JPA Data models (User, Url, Analytics).
* **`com.vishal.urlshortener.service`**: Business logic including Caching strategies and Message production.
* **`com.vishal.urlshortener.consumer`**: RabbitMQ consumers for processing email tasks.
* **`com.vishal.urlshortener.repository`**: Data Access Layer (SQL).
* **`docker`**: Containerization setup for the App, Redis, and Database.

---

## ⚙️ Technologies Used

* **Backend:** Java, Spring Boot, Spring Security.
* **Caching:** Redis (Key-value store & Rate Limiting).
* **Messaging:** RabbitMQ (Asynchronous communication).
* **Database:** MySQL / PostgreSQL.
* **DevOps:** Docker, Docker Compose.
* **Testing:** Postman (Collection included).

---

## 🚀 Getting Started

1.  **Clone the project:**
    ```bash
    git clone [https://github.com/vishalgit101/UrlShortener-RedisCaching-EmailMicroService-RabbitMQ-Backend.git](https://github.com/vishalgit101/UrlShortener-RedisCaching-EmailMicroService-RabbitMQ-Backend.git)
    ```
2.  **Infrastructure Setup (Docker):**
    Ensure Docker is running, then spin up the containers:
    ```bash
    docker-compose up --build
    ```
3.  **Manual Configuration:**
    If running locally without Docker, update `application.properties` with your Redis, RabbitMQ, and SQL credentials.
4.  **Explore API:**
    * **Swagger UI:** `http://localhost:8080/swagger-ui.html`
    * **Postman:** Import the collection found in the `postman/` folder.
