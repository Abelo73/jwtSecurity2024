# JWT Security 2024

## Description

This repository contains a Spring Boot application for user authentication and password management using JSON Web Tokens (JWT). It includes features for user registration, login, password reset, and email notification.

## Features

- **User Registration:** Allows users to sign up with their details.
- **Authentication:** Users can log in using their credentials and receive a JWT for authenticated sessions.
- **Password Reset:** Users can request a password reset and update their password using a token.
- **Email Notifications:** Sends email notifications for password reset requests.

## Technologies Used

- **Spring Boot:** Framework for building the application.
- **Spring Security:** Provides authentication and authorization.
- **JWT (JSON Web Tokens):** For secure user authentication.
- **JavaMailSender:** For sending email notifications.

## Prerequisites

- Java 17 or higher
- Maven or Gradle
- A running database (e.g., H2, MySQL)
- Git

## Setup

### Clone the Repository

```bash
git clone https://github.com/Abelo73/jwtSecurity2024.git
cd jwtSecurity2024
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/yourdatabase
spring.datasource.username=yourusername
spring.datasource.password=yourpassword

# Email Configuration
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=youremail@example.com
spring.mail.password=yourpassword

#Installation

mvn clean install
mvn spring-boot:run

Endpoints
Register a User
POST /register

Request Body:

json

{
  "firstName": "John",
  "lastName": "Doe",
  "username": "johndoe",
  "password": "password",
  "role": "USER"
}

Authenticate a User
POST /authenticate

Request Body:

json
Copy code
{
  "username": "johndoe",
  "password": "password"
}

POST /reset-password

Request Body:

json
Copy code
{
  "token": "your_password_reset_token",
  "newPassword": "new_password"
}


