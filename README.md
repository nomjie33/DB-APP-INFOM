<img src="https://github.com/nomjie33/DB-APP-INFOM/blob/9ebf125472ca122eed8a7c043cc06762a2fac6d4/src/main/gui/assets/logo1_orig.png" alt="logo" width="400 height=auto"/>
# 🚲 U.V.R! - An E-Vehicle Rental Management System

A comprehensive database-driven application for managing electric vehicle rentals (E-Scooters, E-Bikes, and E-Trikes) built with Java, JavaFX, and MySQL.

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Database Schema](#database-schema)
- [Setup Instructions](#setup-instructions)
- [Project Structure](#project-structure)
- [Usage](#usage)
- [Reports](#reports)
- [Contributors](#contributors)

## 🎯 Overview

This Vehicle Rental Management System is designed for electric vehicle rental businesses operating across multiple locations in Metro Manila. The system handles customer registrations, vehicle inventory management, rental transactions, payments, penalties, maintenance tracking, and generates comprehensive business reports.

### Key Business Functions
- **Customer Management**: Register and manage customer profiles with complete address information
- **Vehicle Inventory**: Track e-scooters, e-bikes, and e-trikes across multiple locations
- **Rental Operations**: Two-phase rental process (booking → physical pickup)
- **Payment Processing**: Handle rental payments with multiple payment methods (Cash, GCash, Credit Card, PayMaya)
- **Maintenance Tracking**: Record vehicle maintenance and parts inventory
- **Deployment Management**: Track vehicle movements between rental locations
- **Penalty Management**: Automated late return penalty calculation
- **Reporting**: Generate PDF reports for business analytics

## ✨ Features

### Admin Dashboard
- Real-time overview of active rentals, available vehicles, and revenue
- Quick access to all management modules
- System-wide statistics and metrics

### Customer Management
- Complete customer profiles with hierarchical address system (City → Barangay → Street)
- Customer search by name, email, or ID
- Soft delete functionality (mark as inactive)
- Rental history per customer

### Vehicle Management
- Multi-type vehicle support (E-Scooter, E-Bike, E-Trike)
- Vehicle status tracking (Available, In Use, Maintenance)
- Pricing per vehicle type
- Location-based vehicle deployment

### Rental Operations
- **Phase 1**: Customer booking with pickup date/time selection
- **Phase 2**: Admin confirms physical pickup and starts rental
- Automatic vehicle status updates
- Rental duration tracking
- Soft delete for cancellations

### Payment System
- Multiple payment methods
- Automatic cost calculation based on rental duration
- Payment history and receipts
- Integration with penalty charges

### Maintenance System
- Maintenance request tracking
- Parts inventory management
- Technician assignment
- Maintenance cheque (work order) system
- Cost tracking per maintenance job

### Reporting Module
- **Customer Rental Report**: Detailed rental history by customer
- **Defective Vehicles Report**: Maintenance-required vehicles list
- **Location Rental Frequency Report**: Popular pickup locations analytics
- **Rental Revenue Report**: Financial performance analysis
- Branded PDF export for all reports

## 🛠 Tech Stack

### Backend
- **Java 11+**: Core application logic
- **MySQL 8.0**: Relational database
- **JDBC**: Database connectivity

### Frontend
- **JavaFX**: Desktop GUI framework
- **FXML**: UI layout definitions
- **CSS**: Custom styling

### Libraries
- **iText 5**: PDF report generation
- **MySQL Connector/J**: JDBC driver

## 🗄 Database Schema

### Core Tables
- `locations` - Rental branch locations
- `cities`, `barangays`, `addresses` - Hierarchical address system
- `customers` - Customer profiles
- `vehicles` - Vehicle inventory
- `staff` - Admin staff accounts
- `technicians` - Maintenance staff
- `parts` - Spare parts inventory

### Transaction Tables
- `rentals` - Rental transactions with two-phase workflow
- `payments` - Payment records
- `penalties` - Late return penalties
- `deployments` - Vehicle location transfers
- `maintenance` - Maintenance records
- `maintenance_cheque` - Maintenance work orders

### Key Relationships
```
rentals
├── customerID → customers
├── plateID → vehicles
└── locationID → locations

deployments
├── plateID → vehicles
└── locationID → locations

maintenance
├── plateID → vehicles
└── technicianID → technicians

customers
└── addressID → addresses → barangays → cities
```

## 🚀 Setup Instructions

### Prerequisites
- Java Development Kit (JDK) 11 or higher
- MySQL Server 8.0 or higher
- JavaFX SDK (if not bundled with JDK)
- MySQL Workbench (recommended)

### Database Setup

1. **Create Database Properties File**
   ```bash
   # Copy the example file
   cp db.properties.example db.properties
   ```

2. **Edit `db.properties` with your MySQL credentials**
   ```properties
   db.url=jdbc:mysql://localhost:3306/vehicle_rental_db
   db.username=your_mysql_username
   db.password=your_mysql_password
   ```

3. **Run Database Schema**
   ```bash
   # In MySQL Workbench or command line:
   mysql -u your_username -p < database_schema.sql
   ```

4. **Load Test Data (Optional)**
   ```bash
   mysql -u your_username -p vehicle_rental_db < test_data.sql
   ```

### Application Setup

1. **Clone the Repository**
   ```bash
   git clone https://github.com/nomjie33/DB-APP-INFOM.git
   cd DB-APP-INFOM
   ```

2. **Add Required Libraries**
   - Download MySQL Connector/J from [MySQL Downloads](https://dev.mysql.com/downloads/connector/j/)
   - Download iText 5.x from [Maven Central](https://mvnrepository.com/artifact/com.itextpdf/itextpdf)
   - Place JAR files in the `lib/` directory

3. **Configure IDE**
   
   **For VS Code:**
   - Ensure Java Extension Pack is installed
   - Libraries should be auto-detected from `lib/` folder
   - Use the provided `.vscode/launch.json` configuration

   **For Eclipse:**
   - Import project as existing Java project
   - Add libraries to build path: Right-click project → Build Path → Configure Build Path → Add JARs

   **For IntelliJ IDEA:**
   - Open project
   - File → Project Structure → Libraries → Add JAR files from `lib/`

4. **Test Database Connection**
   ```bash
   java util.DBConnection
   ```
   You should see: "✓ Database connection test SUCCESSFUL!"

5. **Run the Application**
   ```bash
   java main.VehicleRentalApp
   ```

## 📁 Project Structure

```
DB-APP-INFOM/
├── src/
│   ├── dao/                 # Data Access Objects
│   │   ├── CustomerDAO.java
│   │   ├── VehicleDAO.java
│   │   ├── RentalDAO.java
│   │   └── ...
│   ├── model/              # Entity classes
│   │   ├── Customer.java
│   │   ├── Vehicle.java
│   │   ├── RentalTransaction.java
│   │   └── ...
│   ├── service/            # Business logic layer
│   │   ├── RentalService.java
│   │   ├── PaymentService.java
│   │   └── ...
│   ├── main/
│   │   ├── VehicleRentalApp.java
│   │   └── gui/            # JavaFX controllers
│   ├── reports/            # PDF report generators
│   │   ├── CustomerRentalReport.java
│   │   ├── DefectiveVehiclesReport.java
│   │   └── ...
│   ├── util/               # Utility classes
│   │   ├── DBConnection.java
│   │   └── Helpers.java
│   └── test/               # Test classes
├── lib/                    # External libraries
├── bin/                    # Compiled classes
├── reports_output/         # Generated PDF reports
├── database_schema.sql     # Database creation script
├── test_data.sql          # Sample data for testing
├── db.properties.example  # Database config template
└── README.md
```

## 💡 Usage

### Login
- Default admin credentials are created via the database schema
- Login with staff credentials to access the admin dashboard

### Managing Rentals

**Creating a Rental (Two-Phase Process):**
1. **Phase 1 - Customer Booking:**
   - Navigate to Rental Records → Add Rental
   - Select customer, vehicle, and location
   - Choose pickup date/time
   - Vehicle remains "Available" until physical pickup

2. **Phase 2 - Physical Pickup:**
   - When customer arrives, admin starts the rental
   - System records actual start time
   - Vehicle status changes to "In Use"

3. **Completing a Rental:**
   - When customer returns vehicle, click "Complete Rental"
   - System calculates duration and cost
   - Vehicle status returns to "Available"

### Generating Reports

1. Navigate to the Reports section in the dashboard
2. Select report type:
   - **Customer Rental Report**: Choose customer and date range
   - **Defective Vehicles**: Select time period
   - **Location Frequency**: Choose monthly or yearly view
   - **Revenue Report**: Select date range
3. View on-screen or export to PDF
4. PDFs are saved to `reports_output/` folder

## 📊 Reports

### Customer Rental Report
- Complete rental history per customer
- Rental dates, vehicles used, locations
- Payment status and amounts
- Total spending analysis

### Defective Vehicles Report
- Vehicles currently in maintenance
- Maintenance history and costs
- Technician assignments
- Parts used

### Location Rental Frequency Report
- Rental volume per location
- Average rental duration
- Revenue per location
- Most popular vehicle types by location
- Deployment statistics

### Rental Revenue Report
- Daily/Monthly/Yearly revenue breakdown
- Payment method distribution
- Peak rental periods
- Revenue by vehicle type

## 👥 Contributors

- **Bantillo, Gonzales, Reyes, Tan**
  - Database Design & Implementation
  - Backend Development (DAO, Services)
  - Frontend Development (JavaFX GUI)
  - Report Generation System
