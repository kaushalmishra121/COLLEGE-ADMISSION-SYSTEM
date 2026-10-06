-- ==========================================================
-- Apex Institute of Engineering & Technology (AIET)
-- Central Admission Portal - Database Schema Script
-- Database: admissions_db
-- ==========================================================

CREATE DATABASE IF NOT EXISTS admissions_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE admissions_db;

-- 1. Departments / Program Tracks Table
CREATE TABLE IF NOT EXISTS departments (
    dept_id INT AUTO_INCREMENT PRIMARY KEY,
    dept_name VARCHAR(120) NOT NULL UNIQUE,
    degree VARCHAR(40) NOT NULL DEFAULT 'B.Tech',
    duration_years INT NOT NULL DEFAULT 4,
    intake_capacity INT NOT NULL,
    avg_package_lpa DECIMAL(5, 2) NOT NULL
);

-- 2. Seed Academic Departments
INSERT INTO departments (dept_name, degree, duration_years, intake_capacity, avg_package_lpa) VALUES
('Computer Science & Engineering', 'B.Tech', 4, 180, 11.20),
('Artificial Intelligence & Machine Learning', 'B.Tech', 4, 120, 12.80),
('Information Technology', 'B.Tech', 4, 120, 10.50),
('Electronics & Telecommunication Engineering', 'B.Tech', 4, 90, 9.40)
ON DUPLICATE KEY UPDATE dept_name=VALUES(dept_name);

-- 3. Student Applications Table
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

-- 4. Seed Verified Applications
INSERT INTO applications (student_name, email, phone, hsc_percentage, program, status) VALUES
('Aarav Sharma', 'aarav.sharma@example.com', '+91 9876543210', 92.40, 'Computer Science & Engineering', 'Accepted'),
('Neha Patel', 'neha.patel@example.com', '+91 9811223344', 88.50, 'Artificial Intelligence & Machine Learning', 'Pending'),
('Rohan Verma', 'rohan.verma@example.com', '+91 9822334455', 81.20, 'Information Technology', 'Pending'),
('Priya Nair', 'priya.nair@example.com', '+91 9833445566', 86.70, 'Electronics & Telecommunication Engineering', 'Accepted');
