#  Take Note - Smart Notes Management System

**Live Demo:** 👉 **[https://take-note-system.onrender.com](https://take-note-system.onrender.com)**

**Take Note** is a complete, full-stack, secure Web application built with **Spring Boot 3**, **Java 17**, and **MySQL**. It features JWT-based authentication, user-scoped note CRUD operations, dynamic tags, and note statistics.

---

##  Tech Stack & Architecture

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

##  Deployment to Render.com & Clever Cloud (100% Free)

This project is optimized for cloud deployment using **Render** (for the Spring Boot app container) and **Clever Cloud** (for the MySQL database).

### 1. Database Setup (Clever Cloud)
*   Create a free account on [Clever Cloud](https://www.clever-cloud.com/).
*   Click **Create...** -> **An Add-on** -> **MySQL** -> Select **Free "Dev" Plan**.
*   Once created, copy the Host, Database Name, User, and Password from the dashboard connection panel.

### 2. Service Setup (Render)
*   Create a free account on [Render](https://render.com/) and connect your GitHub repo.
*   Click **New +** -> **Web Service** -> Select this `take-note` repository.
*   Configure the service parameters:
    *   **Runtime:** `Docker` (Render reads the `Dockerfile` automatically)
    *   **Build Command:** *(Leave completely empty)*
    *   **Start Command:** *(Leave completely empty)*
    *   **Instance Type:** `Free`

### 3. Configure Environment Variables
Go to the **Environment** tab on Render and add these 4 variables:

| Variable Name | Value | Description |
| :--- | :--- | :--- |
| `DB_URL` | `jdbc:mysql://[YOUR_CLEVER_CLOUD_HOST]:3306/[YOUR_CLEVER_CLOUD_DB]` | Connection link to Clever Cloud |
| `DB_USERNAME` | `[YOUR_CLEVER_CLOUD_USER]` | References MySQL username |
| `DB_PASSWORD` | `[YOUR_CLEVER_CLOUD_PASSWORD]` | References MySQL password |
| `JWT_SECRET` | `TakeNoteSecretKey2024SuperSecureForJWTHMACSHA256!` | Secure JWT token key |

---

## 🔒 Security Policy
All passwords are encrypted using BCrypt. API access is fully secured via JSON Web Tokens. Note CRUD routes enforce note ownership checks at the controller level to prevent unauthorized access.
