# Java Industry Programming – PUP MSIT (2025)

**Course**: Industry-oriented Programming  
**Program**: Master of Science in Information Technology (MSIT) – Open University  
**Institution**: Polytechnic University of the Philippines (PUP)  
**Academic Year**: 2025  

This repository contains **all my solutions**, seatwork, assignments, midterm exam, final exam, and the **capstone final project** for the Java Industry Programming subject.

---

## 🎯 Final Project: Micro Finance Management System  
**with MariaDB Backend for Data Persistence**

### Project Overview
A complete **console-based Micro Finance Management System** built in Java for a hypothetical microfinance institution. It handles end-to-end loan lifecycle management with full **CRUD operations** backed by a MariaDB database using JDBC for secure and persistent data storage.

This project demonstrates real-world industry skills: object-oriented design, database integration, input validation, business logic (interest calculation, loan status tracking), and clean code practices required in Philippine fintech and government financial systems.

### Key Features
- **Client Management** – Register new clients, view/update profiles, search by name/ID  
- **Loan Management** – Apply for loans, approve/reject, calculate interest (simple & compound), track outstanding balance  
- **Payment Processing** – Record repayments, auto-update loan status, generate amortization schedule  
- **Reports & Analytics** – Outstanding loans summary, repayment history, client portfolio overview  
- **Data Persistence** – Full MariaDB integration (JDBC) with proper connection pooling concepts and parameterized queries to prevent SQL injection  
- **Input Validation & Error Handling** – Prevents invalid loans, duplicate clients, negative amounts, etc.  
- **Menu-Driven Console Interface** – User-friendly navigation (no GUI yet – ready for future Swing/JavaFX upgrade)

### Technologies Used
- **Language**: Java (JDK 17/21)  
- **Database**: MariaDB 10.6+ (via JDBC driver)  
- **IDE**: IntelliJ IDEA / VS Code  
- **Design Pattern**: MVC-inspired structure (Model = Entity classes, View = Console, Controller = Service layer)  
- **Other**: Scanner for input, java.sql package, prepared statements

### Database Schema (MariaDB)
- `clients` table (client_id, name, address, contact, date_registered)  
- `loans` table (loan_id, client_id, amount, interest_rate, term_months, status, date_approved)  
- `payments` table (payment_id, loan_id, amount_paid, payment_date)  

(SQL dump file included in the `MicroFinanceManagement/db/` folder)

### How to Run
1. Install MariaDB and create database `microfinance_db`  
2. Import the provided `schema.sql`  
3. Update `DatabaseConfig.java` with your credentials  
4. Run `Main.java` (or the main class in IntelliJ)  
5. Enjoy the interactive menu!

---

## 📚 Coursework Contents

| Folder              | Contents                                      | Skills Demonstrated                     |
|---------------------|-----------------------------------------------|-----------------------------------------|
| `seatwork/`         | HelloWorld, loops, conditionals, basic calculators | Core Java syntax & logic                |
| `assignments/`      | Grade programs, investment calculators, number manipulation | OOP concepts, methods, arrays           |
| `midterm/`          | Midterm exam solutions (2025)                 | Problem-solving under time pressure     |
| `finals/`           | Final exam solutions (2025)                   | Advanced logic & integration            |
| `MicroFinanceManagement/` | Capstone project (above)                   | Full-stack Java + Database application  |

---

## Why This Project Stands Out
- **Real-world relevance** – Directly applicable to Philippine microfinance institutions, cooperatives, and government livelihood programs (e.g., DTI, DA, or LBP projects).  
- **Production-ready practices** – Parameterized queries, exception handling, modular code, and clear separation of concerns.  
- **Scalability** – Designed to be easily extended with Swing GUI, Spring Boot, or cloud deployment (GCP/AWS) – skills I’m already applying in my Google Cloud Cybersecurity and Linux admin roles.

---

**Repository created as part of my MSIT journey at PUP Open University (2025).**  
Feel free to explore the code — all solutions are original and fully commented.

⭐ Star the repo if you find it useful!  
Questions or collaboration? Open an issue.

**Alex Mortel**  
Google Cloud Cybersecurity Certificate Holder | MSIT Student  
alex.mortel@outlook.com | linkedin.com/in/alex-mortel
