# 🔗 Advanced URL Shortener Backend

A high-performance, asynchronous URL shortening service built with **Spring Boot 3** and **Java 21**. This backend leverages **Redis** for ultra-fast redirects and rate limiting, while **RabbitMQ** handles heavy lifting like email verification in the background.

---

## 📊 Database Design & Schema
The project uses **PostgreSQL** for persistent storage of users, shortened URLs, and detailed click analytics.

![Database Schema](./Images/url-shortener-database-schema.png)

---

## 🚀 Live Demo & Testing
* **Live API Documentation:** [Swagger UI on Render](https://urlshortener-app-1-0.onrender.com/swagger-ui/index.html)
* **Note on Hosted Version:** Due to Render's SMTP port restrictions, email features (verification/password reset) are only available in the **Local Environment**.

---

## 🛠️ Tech Stack
* **Language:** Java 21
* **Framework:** Spring Boot 3
* **Database:** PostgreSQL
* **Caching & Rate Limiting:** Redis (Dockerized)
* **Message Broker:** RabbitMQ (Email Microservice)
* **Testing & Documentation:** Postman & Swagger UI
* **Deployment:** Render & Docker

---

## 🌟 Key Features

### ⚡ Performance & Caching
* **Redis Integration:** Short-to-original URL resolution is cached in Redis to minimize database hits and ensure sub-millisecond redirects.
* **Rate Limiting:** Redis-based throttling protects the API from abuse and brute-force attacks.

### ✉️ Asynchronous Processing
* **RabbitMQ:** User registration and password reset flows are non-blocking. The system pushes email tasks to RabbitMQ, which are then processed by a dedicated worker.

### 📈 Analytics & Security
* **Click Tracking:** Captures visitor IP addresses and timestamps for every redirect.
* **Role-Based Access:** Admin-only endpoints for system-wide analytics and management.
* **JWT Authentication:** Secure user flows for registration, login, and URL management.

---

## 📸 Project Walkthrough

### 1. Hosted API (Swagger)
The production environment is documented with Swagger, allowing for real-time testing of URL creation and redirection.

![Swagger UI Demo](./Images/hosted-swagger-demo.png)

### 2. Local Testing (Postman)
Full system testing including the RabbitMQ email worker and PostgreSQL data integrity.

| API Testing Flow | Feature Highlight |
| :--- | :--- |
| **Postman Testing** | ![Postman API testing](./Images/Postman-API-testing.png) |
| **User Onboarding** | Registration & Email Verification Flow |
| **Redirect Logic** | Redis Caching & IP Tracking |

---

## ⚙️ Local Setup

1.  **Clone the Repository:**
    ```bash
    git clone [https://github.com/vishalgit101/UrlShortener-RedisCaching-EmailMicroService-RabbitMQ-Backend.git](https://github.com/vishalgit101/UrlShortener-RedisCaching-EmailMicroService-RabbitMQ-Backend.git)
    ```

2.  **Run Infrastructure (Docker):**
    Ensure you have Redis and RabbitMQ running:
    ```bash
    docker run -d --name redis -p 6379:6379 redis
    docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
    ```

3.  **Configure Environment:**
    Update `src/main/resources/application.properties` with your PostgreSQL and SMTP (Gmail/Mailtrap) credentials.

4.  **Build and Run:**
    ```bash
    mvn spring-boot:run
    ```

---

## 📧 Contact & Support
Developed by **Vishal**. Feel free to reach out for any questions regarding the Redis caching logic or RabbitMQ implementation!
