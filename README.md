# Spring Boot Authentication System

A secure Authentication and Authorization system built using Spring Boot. This project implements complete user account management features including Signup, Login, Email Verification, Forgot Password, and Reset Password functionality.

## Features

- User Registration (Signup)
- User Login
- Email Verification
- Forgot Password
- Reset Password
- Password Encryption
- JWT Authentication
- Input Validation
- Exception Handling
- REST APIs
- Database Integration

---

## Tech Stack

### Backend

- Java 21 (or Java 17)
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- JWT (JSON Web Token)

### Database

- MySQL

### Email Service

- Gmail SMTP

### Build Tool

- Maven

### Additional Libraries

- Lombok
- Jakarta Mail

---

## Project Structure

src/main/java

```
com.project

├── config

├── controller

├── dto

├── entity

├── repository

├── security

├── service

├── utils

└── exception
```

---

## Authentication Flow

### 1. User Registration

User sends:

POST `/api/auth/signup`

```json
{
  "name":"John Doe",
  "email":"john@example.com",
  "password":"password123"
}
```

Process:

1. Validate request data
2. Check existing email
3. Encrypt password
4. Generate verification token
5. Save user
6. Send verification email

---

## 2. Email Verification

Endpoint:

```text
GET /api/auth/verify-email?token=xxxxxxxx
```

Process:

1. Validate token
2. Verify expiration time
3. Mark email as verified

---

## 3. Login

Endpoint:

```text
POST /api/auth/login
```

Request:

```json
{
  "email":"john@example.com",
  "password":"password123"
}
```

Process:

1. Validate credentials
2. Authenticate user
3. Generate JWT token
4. Return token

Response:

```json
{
  "token":"jwt_token"
}
```

---

## 4. Forgot Password

Endpoint:

```text
POST /api/auth/forgot-password
```

Request:

```json
{
  "email":"john@example.com"
}
```

Process:

1. Verify email exists
2. Generate reset token
3. Save token
4. Send reset email

---

## 5. Reset Password

Endpoint:

```text
POST /api/auth/reset-password
```

Request:

```json
{
  "token":"xxxxxxxx",
  "newPassword":"newPassword123"
}
```

Process:

1. Validate token
2. Check expiration
3. Encrypt new password
4. Update password

---

## JWT Flow

```text
Client
  |
Login
  |
Spring Security
  |
Generate JWT
  |
Return Token
  |
Client stores Token
  |
Protected APIs
  |
Authorization Header
  |
Validate Token
  |
Access Granted
```

---

## Database Tables

### users

| Column | Type |
|--------|------|
| id | Long |
| name | String |
| email | String |
| password | String |
| emailVerified | Boolean |
| createdAt | LocalDateTime |

### verification_tokens

| Column | Type |
|--------|------|
| id | Long |
| token | String |
| expiryDate | LocalDateTime |

### password_reset_tokens

| Column | Type |
|--------|------|
| id | Long |
| token | String |
| expiryDate | LocalDateTime |

---

## Gmail SMTP Configuration

application.properties

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587

spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password

spring.mail.properties.mail.smtp.auth=true

spring.mail.properties.mail.smtp.starttls.enable=true

spring.mail.properties.mail.smtp.starttls.required=true
```

---

## Run The Project

Clone repository

```bash
git clone https://github.com/your-username/project-name.git
```

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/signup | Register user |
| GET | /api/auth/verify-email | Verify email |
| POST | /api/auth/login | User login |
| POST | /api/auth/forgot-password | Send reset email |
| POST | /api/auth/reset-password | Reset password |

---

## Security Features

- BCrypt password encryption
- JWT token authentication
- Email verification
- Password reset token expiration
- Input validation
- Exception handling

---
