# 🏨 Hotel Management System

A Java Swing desktop application for managing hotel reservations. The system provides a complete reservation workflow, including booking creation, reservation management, check-in/check-out, and persistent local storage using CSV files.

---

## 📖 Project Overview

This project was developed as part of a Software Engineering course to demonstrate object-oriented programming principles, GUI development with Java Swing, algorithms, recursion, validation, and file handling.

The application works entirely offline and does not require a database.

---

## ✨ Features

- 🔐 Role-Based Login (Administrator & Receptionist)
- 📊 Dashboard with booking statistics
- ➕ Create new reservations
- 🔍 Search bookings by guest name or surname
- 📋 View all reservations
- ✅ Check-In confirmation
- 🚪 Check-Out management
- 💾 Persistent local file storage
- ✔ Input validation and error handling

---

## 🖥 Application Modules

### Login

Authenticates users using predefined Administrator and Receptionist accounts.

### Dashboard

Displays booking statistics, recent activity, and administrator revenue information.

### Add Booking

Creates new hotel reservations with validation and automatic price calculation.

### View / Search

Searches bookings by guest name or surname and displays all reservations.

### Check-In / Check-Out

Confirms guest arrivals and completes reservations after check-out.

---

# 🛠 Technologies

- Java
- Java Swing
- Object-Oriented Programming (OOP)
- File Handling
- CSV Storage
- Exception Handling

---

# 📚 OOP Concepts Implemented

## Inheritance

- Person → Guest, Employee
- Room → StandardRoom, DeluxeRoom
- Booking → PendingBooking, ConfirmedBooking

## Abstract Classes

- Person
- Room
- Booking

## Interfaces

- Reservable
- Searchable

## Polymorphism

Implemented using Room and Booking references where runtime behavior changes according to the object type.

## Method Overloading

```java
addBooking(Booking booking)

addBooking(String guestName,
           String surname,
           String roomNumber,
           String nights)
```

## Method Overriding

- Room.getFinalPrice()
- Room.displayInfo()
- Booking.displayInfo()

---

# ✔ Validation & Exception Handling

### Regex Validation

- Email
- Phone Number
- Guest Name

### Input Validation

- Empty fields
- Room number validation
- Date validation
- Check-out after Check-in

### Exception Handling

Implemented using try-catch blocks and user-friendly JOptionPane messages.

---

# 🔍 Algorithms

## Searching

Linear Search

- Search by guest name
- Search by surname

## Sorting

Bubble Sort

- Sort bookings by room number

---

# 🔁 Recursion

- Revenue calculation
- Discount calculation for Standard Rooms

---

# 💾 CRUD Operations

The application supports complete CRUD functionality using local text files.

- Create
- Read
- Update
- Delete

---

# 📁 Data Storage

Bookings are stored in:

```
data/hotel_records.txt
```

CSV format:

```
name,surname,roomNumber,nights,checkIn,checkOut,status
```

---

# 📂 Project Structure

```
Hotel-Management-System/
│
├── src/
├── data/
│   └── hotel_records.txt
│
├── README.md
├── LICENSE
└── manifest.mf
```

---

# 🔑 Test Accounts

| Username | Password | Role |
|-----------|----------|------|
| admin | admin | Administrator |
| john | john123 | Receptionist |
| mary | mary123 | Receptionist |

---

# 🚀 Getting Started

## Clone the repository

```bash
git clone https://github.com/Regis-T/Hotel-Management-System.git
```

## Open the project

Open the project in NetBeans or any Java IDE.

## Run

Run the main application class.

---

# 📈 Future Improvements

- MySQL database integration
- Online booking support
- PDF invoice generation
- Room availability calendar
- User registration
- Enhanced reporting
- Improved UI/UX

---

## 👨‍💻 Author

**Regis Tosku**

Software Engineering Student

[![GitHub](https://img.shields.io/badge/GitHub-Regis--T-181717?style=for-the-badge&logo=github)](https://github.com/Regis-T)

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Regis%20Tosku-0A66C2?style=for-the-badge&logo=linkedin)](https://www.linkedin.com/in/regis-tosku-b7884433a)

---

## 📄 License

This project is licensed under the MIT License.
