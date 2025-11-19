# 🚲 U.V.R! - An Vehicle Rental App & Management System
<img src="https://github.com/nomjie33/DB-APP-INFOM/blob/9ebf125472ca122eed8a7c043cc06762a2fac6d4/src/main/gui/assets/logo1_orig.png" alt="logo" width="500"/>

A comprehensive database-driven application for managing electric vehicle rentals (E-Scooters, E-Bikes, and E-Trikes) built with Java, JavaFX, and MySQL.

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
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

## 🛠 Technology Stack

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
   mysql -u your_username -p < "CCINFOM 22-07.sql"
   ```
   
   The SQL script will automatically create the `vehicle_rental_db` database and populate it with initial data.

### Application Setup

1. **Clone the Repository**
   ```bash
   git clone https://github.com/nomjie33/DB-APP-INFOM.git
   cd DB-APP-INFOM
   ```

2. **Add Required Libraries**
   - Download MySQL Connector/J 8.0+ from [MySQL Downloads](https://dev.mysql.com/downloads/connector/j/)
   - Download iText 5.5.13.3 from [Maven Central](https://mvnrepository.com/artifact/com.itextpdf/itextpdf/5.5.13.3)
   - Place JAR files in the `lib/` directory

3. **Configure IDE**
   
   **For IntelliJ IDEA:**
   - Open the project directory
   - File → Project Structure → Libraries → Add (`+`) → Java
   - Select all JAR files from the `lib/` directory
   - Apply and OK
   
   **For Eclipse:**
   - Import project as existing Java project
   - Right-click project → Build Path → Configure Build Path
   - Libraries tab → Add JARs → Select JARs from `lib/` folder
   
   **For VS Code:**
   - Ensure Java Extension Pack is installed
   - Libraries in `lib/` folder should be auto-detected
   - If not, add to `.classpath` file

4. **Verify Database Connection**
   - Run `util.DBConnection` to test connectivity
   - You should see: "✓ Database connection test SUCCESSFUL!"

5. **Run the Application**
   - Execute `main.VehicleRentalApp` as the main class
   - The login screen should appear

## 📁 Project Structure

```
DB-APP-INFOM/
├── src/
│   ├── module-info.java     # Java module configuration
│   ├── dao/                 # Data Access Objects
│   │   ├── CustomerDAO.java
│   │   ├── VehicleDAO.java
│   │   ├── RentalDAO.java
│   │   ├── PaymentDAO.java
│   │   ├── DeploymentDAO.java
│   │   ├── MaintenanceDAO.java
│   │   ├── LocationDAO.java
│   │   ├── AddressDAO.java
│   │   └── ...
│   ├── model/              # Entity/POJO classes
│   │   ├── Customer.java
│   │   ├── Vehicle.java
│   │   ├── RentalTransaction.java
│   │   ├── PaymentTransaction.java
│   │   ├── DeploymentTransaction.java
│   │   ├── MaintenanceTransaction.java
│   │   └── ...
│   ├── service/            # Business logic layer
│   │   ├── RentalService.java
│   │   ├── PaymentService.java
│   │   ├── PenaltyService.java
│   │   ├── MaintenanceService.java
│   │   ├── DeploymentService.java
│   │   └── ...
│   ├── main/
│   │   ├── VehicleRentalApp.java  # Main application entry point
│   │   └── gui/            # JavaFX FXML controllers
│   │       ├── Admin-login.fxml
│   │       ├── Admin-dashboard.fxml
│   │       ├── Admin-customerRecords.fxml
│   │       ├── Admin-vehicleRecords.fxml
│   │       └── ...
│   ├── reports/            # PDF report generators
│   │   ├── CustomerRentalReport.java
│   │   ├── DefectiveVehiclesReport.java
│   │   ├── LocationRentalFrequencyReport.java
│   │   ├── RentalRevenueReport.java
│   │   └── PDFBrandingHelper.java
│   ├── util/               # Utility classes
│   │   ├── DBConnection.java
│   │   └── Helpers.java
│   └── test/               # Test classes
│       ├── DAOCRUDTest.java
│       ├── daotest.java
│       └── ServiceTest.java
├── bin/                    # Compiled .class files (generated)
├── lib/                    # External JAR dependencies
│   ├── mysql-connector-j-x.x.x.jar
│   └── itextpdf-5.5.13.3.jar
├── reports_output/         # Generated PDF reports directory
├── SQL_Syntax_CrashCourse/ # SQL learning resources
│   ├── SQL_D1.md
│   ├── SQL_D2.md
│   └── ...
├── CCINFOM 22-07.sql      # Main database schema & data
├── diagnose.sql           # Database diagnostic queries
├── db.properties.example  # Database config template
├── db.properties          # Actual DB config (gitignored)
├── DB-APP-INFOM.iml      # IntelliJ project file
├── PROJECT_STRUCTURE.md   # Detailed project documentation
└── README.md             # This file
```

## 💡 Usage

### Login
- Launch the application to access the admin login screen
- Use staff credentials from the database (created via the SQL schema)
- Successfully logged-in users are directed to the admin dashboard

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

**CCINFOM S22 Group 07**
- Team members responsible for:
  - Database Schema Design & Implementation
  - Backend Development (DAO Layer, Service Layer)
  - Frontend Development (JavaFX GUI & Controllers)
  - Business Logic & Transaction Management
  - PDF Report Generation System

## 📝 Academic Context

This project was developed as a major course requirement for **CCINFOM (Database Systems)** - Section S22, Group 07.

**Institution**: De La Salle University  
**Course**: Database Management Systems  
**Academic Year**: 2024-2025  

The system demonstrates:
- Relational database design principles
- CRUD operations with JDBC
- Transaction management
- Three-tier architecture (Presentation, Business, Data layers)
- Real-world business process implementation

## 🤝 Contributing

This is an academic project developed for coursework. While not actively maintained post-submission, feedback and suggestions are welcome:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/improvement`)
3. Commit your changes (`git commit -m 'Add improvement'`)
4. Push to the branch (`git push origin feature/improvement`)
5. Open a Pull Request

## 🐛 Troubleshooting

### Database Connection Issues
- Verify MySQL server is running
- Check credentials in `db.properties`
- Ensure `vehicle_rental_db` database exists
- Confirm MySQL Connector JAR is in `lib/` folder

### JavaFX Runtime Issues
- Ensure JavaFX SDK is properly configured in your IDE
- For Java 11+, JavaFX is not bundled and must be added separately

### PDF Generation Issues
- Verify iText library is in `lib/` folder
- Check write permissions for `reports_output/` directory

## 📧 Contact

For questions or issues related to this project, please use GitHub Issues or contact the repository maintainer.

---

**Note**: This is an academic project developed for educational purposes, demonstrating database design, Java application development, and software engineering principles in the context of a vehicle rental management system.
