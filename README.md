# Core Banking Management System 🏦

## Overview
A robust, console-based banking application simulating real-world financial operations. Developed to showcase advanced software engineering practices, this project is available in both **Java**. It demonstrates strict Object-Oriented Programming (OOP) architecture, secure transaction handling, and data persistence.

## 🚀 Key Features
* **Interactive CLI:** A user-friendly command-line interface for real-time account management and navigation.
* **Secure Financial Operations:** Supports deposits, withdrawals, and inter-account transfers with strict pre-computation validation.
* **Custom Exception Handling:** Utilizes a custom error-handling architecture (e.g., `NotEnoughFunds`, `InactiveAccountException`) to enforce business rules and prevent invalid state changes.
* **Data Persistence:** Integrates File I/O streams (`BufferedReader`/`BufferedWriter` in Java) to locally save and restore account states and transaction histories across sessions.

## 🛠️ Technologies & Core Concepts
* **Languages:** Java
* **Design Principles:** DRY (Don't Repeat Yourself), Fail-Fast Validation
* **OOP Concepts:** Polymorphism, Inheritance, Abstraction, Encapsulation

## 🏗️ Architecture Highlights
The domain is modeled using a decoupled, highly cohesive class hierarchy:
* `Customer`: The central entity aggregating multiple account types.
* `Account` (Abstract): The base class enforcing structural contracts for financial operations.
* `CheckingAccount` & `SavingsAccount`: Concrete implementations containing specific business rules (e.g., overdraft limits, interest rate calculations).
* `Transaction`: Immutable objects acting as the source of truth for all financial movements.

## 💻 How to Run

### Java Version
Open your terminal, navigate to the source directory, and run:
```bash
javac *.java
java BankSystem

