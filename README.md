# Secure User Management & Authentication System

A robust and security-focused User Management System developed using Java Servlets, JSP, JDBC, and MySQL. The application provides a complete authentication and authorization workflow with advanced account protection mechanisms, administrative controls, and secure password management practices.

## Key Features

### User Registration & Authentication

* Secure user registration and login functionality.
* Email verification notification sent upon successful account registration.
* Authentication system built using industry-standard security practices.
* Passwords are securely stored using BCrypt hashing, ensuring that plaintext passwords are never saved in the database.

### User Account Management

* Users can:

  * View their profile information.
  * Update account details.
  * Delete their own accounts.
* Account operations are protected through authenticated session validation.

### Administrative Controls

* Dedicated administrator dashboard.
* Admin can:

  * View registered users.
  * Edit user information.
  * Delete user accounts.
  * Monitor account security status.

### Session Management & Auto Logout

* Session timeout mechanism implemented for enhanced security.
* Users are automatically logged out after 10 minutes of inactivity.
* Re-authentication is required to regain access after session expiration.

### Advanced Account Protection

#### Failed Login Attempt Tracking

* Every failed login attempt is recorded and tracked within the database.

#### Temporary Account Lockout

* Accounts are automatically locked after 5 consecutive failed login attempts.
* Locked accounts remain inaccessible for 15 minutes.
* Lock duration is calculated and enforced dynamically.

#### Security Monitoring

* Dedicated database fields maintain:

  * Failed attempt counts.
  * Account lock status.
  * Lock expiration timestamps.
* Enables real-time monitoring and auditing of authentication activities.

### Database Design

The system maintains comprehensive user security metadata including:

* User credentials
* BCrypt password hashes
* Failed login attempt counters
* Account lock timestamps
* Session-related information
* User role management

## Technology Stack

* Java Servlets
* JSP (Java Server Pages)
* JDBC
* MySQL
* BCrypt Password Hashing
* JavaMail API
* Apache Tomcat
* postgres

## Security Highlights

* BCrypt password encryption
* Session-based authentication
* Automatic session expiration
* Brute-force attack mitigation
* Account lockout protection
* Role-based administration
* Secure database credential handling
* Authentication activity tracking

## Project Objective

This project demonstrates the implementation of a modern authentication and user management system that follows secure development practices commonly used in enterprise web applications. The system emphasizes account security, user management, session control, and protection against unauthorized access attempts.
