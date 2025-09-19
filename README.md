# SmartLinker

Smart Contact Management system with integrated email functionality and cloud storage.

## Overview

SmartLinker is a comprehensive contact management application built with Spring Boot that provides secure user authentication, contact organization, and integrated email services. 
The application features cloud-based image storage, advanced search capabilities, and a responsive UI with theme customization. 
Users can manage their contacts, send emails directly through the platform, and export data for external use.


Video demo Link (Click & Watch) : https://shorturl.at/zRjSg

Note: 
Live demo link (Deployment is there in progress) will be uploaded soon.

## Key Features

- **Authentication & Security**: User registration with email verification, OAuth2 integration for Google/GitHub login
- **Contact Management**: Add, view, update, and delete contacts with profile pictures stored on AWS/Cloudinary
- **Advanced Operations**: Search functionality across multiple fields, pagination for large datasets, favorite contact marking
- **Email Integration**: Compose and send emails with attachments directly from the application
- **Data Export**: Export contact data to Excel format for external processing
- **User Experience**: Profile management, dark/light theme toggle, feedback system, responsive design

## Architecture & Tech Stack

**Backend Framework:**
- Spring Boot with Spring MVC architecture
- Spring Data JPA for database operations
- Spring Security with OAuth2 for authentication
- JavaMail for email services

**Frontend:**
- Thymeleaf template engine
- TailwindCSS for styling
- Flowbite component library
- Vanilla JavaScript for client-side interactions

**Database & Storage:**
- MySQL for data persistence
- Cloudinary for image storage

**Additional Libraries:**
- PDF/Excel processing libraries
- ModelMapper for object mapping
- Bean Validation for input validation

## Setup & Installation

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+ or PostgreSQL 12+
- IDE (IntelliJ IDEA, Eclipse, or VS Code - I used VS Code for this project)

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/arvindparaliya/SmartLinker.git
   cd smartlinker
   ```

2. **Database Configuration**
   
   Create a database and update `src/main/resources/application.properties`:
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:mysql://localhost:3306/smartlinker_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   
   # OAuth2 Configuration
   spring.security.oauth2.client.registration.google.client-id=your_google_client_id
   spring.security.oauth2.client.registration.google.client-secret=your_google_client_secret
   
   spring.security.oauth2.client.registration.github.client-id=your_github_client_id
   spring.security.oauth2.client.registration.github.client-secret=your_github_client_secret
   
   # Email Configuration
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=your_email@gmail.com
   spring.mail.password=your_app_password
   
   # Cloud Storage Configuration
   cloudinary.cloud.name=your_cloudinary_name
   cloudinary.api.key=your_api_key
   cloudinary.api.secret=your_api_secret
   ```

3. **Run the application**
   ```bash
   # Using Maven
   mvn spring-boot:run
   
   # Or using IDE
   # Run the main class: SmartLinkerApplication.java
   ```

4. **Access the application**
   
   Open your browser and navigate to `http://localhost:8081`

## Usage Guide

### Getting Started

1. **Registration**: Create an account using email/password or OAuth2 providers (Google/GitHub)
2. **Email Verification**: Check your email and click the verification link for email-based registration
3. **Login**: Access your dashboard using your credentials

### Contact Management

1. **Add Contacts**: Navigate to "Add Contact" and fill in the contact details with an optional profile picture
2. **View Contacts**: Browse your contacts with pagination support
3. **Search**: Use the search functionality to find contacts by name, email, or phone number
4. **Contact Details**: Click on any contact to view detailed information
5. **Update/Delete**: Use the action buttons to modify or remove contacts
6. **Favorites**: Mark important contacts as favorites for quick access
7. **Categories** : User can filter the contacts based on with or without email address, favorites, linkedin link, general link, noraml address, contact photo and more.
8. **Recent Contacts** : User can find Recent saved contact quickly.
9. **Last 7 days Contacts ** : User easily can find last 7 days contacts saved.

### Email Services

1. **Compose Email**: Select contacts and use the email composer
2. **Attachments**: Add files to your emails before sending
3. **Send**: Emails are sent directly through the configured SMTP server

### Data Management

1. **Export**: Use the export feature to download your contacts as Excel files
2. **Profile**: Update your personal information and preferences
3. **Theme**: Toggle between dark and light themes
4. **Feedback**: Submit feedback through the integrated form

## Dependencies

```xml
<!-- Core Spring Boot Starters -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>

<!-- Utilities -->
<dependency>
    <groupId>org.modelmapper</groupId>
    <artifactId>modelmapper</artifactId>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
</dependency>

<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>
```

## Contributing

This is currently a personal project. Contributions, issues, and feature requests are welcome. Feel free to check the issues page if you want to contribute.

Video, Screenshots and live demo link will be uploaded soon.
