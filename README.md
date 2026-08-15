# 🚀 Take Note - Smart Notes Management System

**Take Note** is a complete, full-stack, secure Web application built with **Spring Boot 3**, **Java 17**, and **MySQL**. It features JWT-based authentication, user-scoped note CRUD operations, dynamic tags, and note statistics.

---

## 🛠️ Tech Stack & Architecture

*   **Backend:** Java 17, Spring Boot 3, Spring Security, Spring Data JPA, Hibernate
*   **Authentication:** JSON Web Tokens (JWT) stored in secure browser session cookies
*   **Database:** MySQL (Supports user-scoped duplicate tag names)
*   **Frontend:** HTML5, CSS3, Thymeleaf, Bootstrap 5, FontAwesome Icons

---

## 📂 Key Features

1.  **JWT Authentication:** Secure user registration, password hashing (BCrypt), and login session handling using cookies.
2.  **Private Notes (CRUD):** Users can only View, Add, Edit, or Delete notes that belong to their specific account (ownership-hardened).
3.  **Dynamic Filters:** Filter notes instantly by Favorites or custom Tag categories.
4.  **Live Stats:** Sidebar dynamically shows total notes and total favorite notes count.
5.  **Clean SPA Experience:** Add, edit, and delete notes dynamically using Bootstrap Modals without page reloads.

---

## 🏃 Local Installation & Running

### Prerequisites
*   Java Development Kit (JDK) 17
*   MySQL Server installed and running

### Step-by-Step Setup:

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/Gittyasn/take-note.git
    cd take-note
    ```

2.  **Configure Database:**
    *   Create a local MySQL schema named `takenote_db`:
        ```sql
        CREATE DATABASE takenote_db;
        ```

3.  **Run the Application:**
    Set the environment variables and boot the application using Maven Wrapper:

    *   **Windows (PowerShell):**
        ```powershell
        $env:DB_PASSWORD="your_mysql_password"
        $env:JWT_SECRET="TakeNoteSecretKey2024SuperSecureForJWTHMACSHA256!"
        .\mvnw.cmd spring-boot:run
        ```
    *   **Linux / Mac (Terminal):**
        ```bash
        export DB_PASSWORD="your_mysql_password"
        export JWT_SECRET="TakeNoteSecretKey2024SuperSecureForJWTHMACSHA256!"
        ./mvnw spring-boot:run
        ```

4.  **Open the Web App:**
    Go to **[http://localhost:8080](http://localhost:8080)** in your browser.

---

## 🚀 Deployment to Railway.app

This project is fully optimized for **Railway.app** deployment with secure, zero-hardcoded secrets.

### 1. Database Provisioning
*   Log into [Railway.app](https://railway.app) and click **New Project** -> **Provision MySQL**.

### 2. Service Deployment
*   Click **New** -> **GitHub Repo** and select this `take-note` repository.

### 3. Setup Variables
Select the deployed Spring Boot service, navigate to the **Variables** tab, and add the following 4 environment variables:

| Variable Name | Value | Description |
| :--- | :--- | :--- |
| `DB_URL` | `jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}` | Dynamically references host, port, and database name |
| `DB_USERNAME` | `${{MySQL.MYSQLUSER}}` | References MySQL username |
| `DB_PASSWORD` | `${{MySQL.MYSQLPASSWORD}}` | References MySQL password |
| `JWT_SECRET` | `TakeNoteSecretKey2024SuperSecureForJWTHMACSHA256!` | Secure JWT token key |

*Railway will automatically link your database variables to the Spring Boot app!*

---

## 🔒 Security Policy
All passwords are encrypted using BCrypt. API access is fully secured via JSON Web Tokens. Note CRUD routes enforce note ownership checks at the controller level to prevent unauthorized access.
