# 🎓 Apex Institute of Engineering & Technology (AIET)
### Enterprise Admission Management System & Student Enrollment Portal

[![Java](https://img.shields.io/badge/Java-17%20%2F%2021%20%2F%2025-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)](https://developer.mozilla.org/en-US/docs/Web/HTML)
[![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)](https://developer.mozilla.org/en-US/docs/Web/CSS)
[![JavaScript](https://img.shields.io/badge/JavaScript-ES6%2B-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)](https://developer.mozilla.org/en-US/docs/Web/JavaScript)
[![License](https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge)](LICENSE)
[![NAAC](https://img.shields.io/badge/NAAC-A%2B%20Grade%20Accredited-red?style=for-the-badge)](#)

---

## 🏛️ Executive Overview

The **Apex Institute of Engineering & Technology (AIET) Admission Management System** is a full-stack, enterprise-grade academic admission platform designed to streamline student candidate registrations, merit evaluation, automated receipt generation, status auditing, and asynchronous email notification dispatches.

Built with high visual fidelity, academic branding tokens (`#15243E` Navy Blue, `#C8102E` Crimson Red), high-resolution discipline showcases, and a zero-dependency embedded Java HTTP architecture powered by standard `java.sql.*` JDBC connectivity.

---

## 🌟 Key Features

### 1. 👨‍🎓 Interactive Candidate Admission Portal
* **Verified Application Form**: Collects candidate full name, active email, 10-digit mobile number, 12th/HSC aggregate scores, and chosen engineering degree tracks.
* **Instant Digital Receipt**: On form submission, an official printable **Admission Acknowledgement Slip** pops up automatically with unique Application Reference IDs, timestamps, registrar seal, and barcodes.
* **Clean Print Engine (`@media print`)**: Dedicated print stylesheet removes web bars, hero banners, and shadows to export clean single-page official PDF receipts.

### 2. 🛡️ Administrative Review Desk & Governance Panel
* **Live Candidate Data Table**: Displays all incoming applicants with real-time score auditing, contact numbers, and branch preferences.
* **Decision Management**: Admission officers can review and transition candidate statuses (`Pending` ➔ `Accepted` / `Rejected` / `Under Review`).
* **Database & Memory Sync**: Automatically syncs with MySQL database records via RESTful endpoints, with seamless local caching fallbacks.

### 3. 📧 Asynchronous Email Dispatch Service (`EmailService.java`)
* Background thread pool (`ExecutorService`) delivers formatted HTML admission confirmation letters directly to the student's email inbox without blocking the user interface.

### 4. 🏫 Immersive Campus Showcase
* **Branch Cards with Tech Imagery**: Dedicated visual cards for *Computer Science & Engineering*, *AI & Machine Learning*, *Information Technology*, and *Electronics & Telecommunication*.
* **Placement Metrics**: Live display of **94.8%** placement record, **₹42.5 LPA** highest international package, and **220+** recruiting partners (*Google, Microsoft, TCS, L&T*).
* **Campus Infrastructure & Events**: Research highlights for high-performance GPU clusters, 24/7 digital library, robotics labs, **TechNova 36-Hr Hackathon**, and **Aura Fest**.

---

## 📐 System Architecture & Tech Stack

```
   ┌─────────────────────────────────────────────────────────┐
   │                  CLIENT / BROWSER                      │
   │   HTML5  •  CSS3 (@media print)  •  Vanilla JS (ES6+)    │
   └──────────────────────────┬──────────────────────────────┘
                              │
               HTTP GET/POST JSON Payloads
                              │
   ┌──────────────────────────▼──────────────────────────────┐
   │                  JAVA BACKEND SERVER                    │
   │  ┌──────────────────────────────────────────────────┐  │
   │  │ Server.java (com.sun.net.httpserver / Port 8080) │  │
   │  └───────────────────────┬──────────────────────────┘  │
   │                          │                             │
   │         ┌────────────────┴────────────────┐            │
   │         ▼                                 ▼            │
   │  ┌──────────────┐                 ┌───────────────┐    │
   │  │ AdmissionDAO │                 │ EmailService  │    │
   │  └──────┬───────┘                 └───────┬───────┘    │
   └─────────┼─────────────────────────────────┼────────────┘
             │                                 │
      JDBC Connection Pool             SMTP TLS Dispatch
             │                                 │
   ┌─────────▼─────────┐               ┌───────▼────────┐
   │  MySQL 8.0 Engine │               │ Candidate Mail │
   │  (admissions_db)  │               │   (Inbox)      │
   └───────────────────┘               └────────────────┘
```

---

## 🗄️ Database Setup (MySQL)

Ensure MySQL Server 8.0+ is running locally or on your cloud instance. Execute the provided `schema.sql` script:

```sql
CREATE DATABASE IF NOT EXISTS admissions_db;
USE admissions_db;

CREATE TABLE IF NOT EXISTS applications (
    application_id INT AUTO_INCREMENT PRIMARY KEY,
    student_name VARCHAR(120) NOT NULL,
    email VARCHAR(120) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    hsc_percentage DECIMAL(5, 2) NOT NULL,
    program VARCHAR(120) NOT NULL,
    status ENUM('Pending', 'Under Review', 'Accepted', 'Rejected') DEFAULT 'Pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

Or run via MySQL Command Line / Terminal:
```bash
mysql -u root -p < schema.sql
```

> **Database Credentials Configuration:**
> If your MySQL password differs from the default `root123`, update the `PASS` constant inside [AdmissionDAO.java](file:///c:/Users/KUSHAL%20%20MISHRA/OneDrive/Desktop/college%20edu%20system/AdmissionDAO.java).

---

## 🚀 Local Execution Guide

### Option 1: Direct Java Execution (Recommended)
1. Make sure you have the MySQL Connector/J JAR (`mysql-connector-j-8.4.0.jar` or similar) in your classpath or project directory.
2. Compile the Java source files:
   ```bash
   javac -cp ".;mysql-connector-j-8.4.0.jar" *.java
   ```
3. Launch the Admission Portal Server:
   ```bash
   java -cp ".;mysql-connector-j-8.4.0.jar" Server
   ```
4. Open your web browser and navigate to:
   ```
   http://localhost:8080
   ```

---

### Option 2: Static Cloud Deployment (Vercel / Netlify / GitHub Pages)
The client frontend is fully decoupled and ready for static CDN deployment:
* **Vercel**: Deploy the repository directly; [vercel.json](file:///c:/Users/KUSHAL%20%20MISHRA/OneDrive/Desktop/college%20edu%20system/vercel.json) handles clean URL routing.
* **GitHub Pages**: Host the root folder (`index.html`, `style.css`, `script.js`).

---

## 📂 Project Structure

```
college-edu-system/
├── index.html            # Primary admission portal UI with receipt modal
├── style.css             # Design tokens, responsive grid & @media print styles
├── script.js             # Form controller, API fetch & dynamic admin table
├── Server.java           # Embedded HTTP server & RESTful routing handlers
├── AdmissionDAO.java     # JDBC Data Access Object (CRUD operations)
├── EmailService.java     # Asynchronous candidate email dispatch helper
├── schema.sql            # MySQL database DDL schema and seed records
├── vercel.json           # Vercel static hosting and header configuration
├── .gitignore            # Git exclusion rules for compiled binaries & logs
└── README.md             # Comprehensive project documentation
```

---

## 👥 Authors & Academic Credits

* **Apex Institute of Engineering & Technology (AIET)** &bull; *Office of the Registrar & Admissions Cell*
* **Architecture & UI/UX**: Antigravity Principal Full-Stack Engineering Team
* **Licensing**: Open for academic, institutional, and educational usage under the **MIT License**.
