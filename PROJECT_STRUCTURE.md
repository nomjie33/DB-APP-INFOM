# Vehicle Rental Database Application
## CCINFOM Database Application Project

---

## 📋 Project Overview

This is a **Vehicle Rental Management System** built using Java and MySQL. The application implements a **layered architecture** to manage:
- Customer records
- Vehicle inventory
- Rental transactions
- Payment processing
- Maintenance tracking
- Penalty management
- Vehicle deployment across locations
- Business analytics and reporting

---

## 🏗️ Project Architecture

### Layered Structure

```
┌─────────────────────────────────────┐
│     PRESENTATION LAYER (UI)         │  ← JavaFX/Swing interfaces
├─────────────────────────────────────┤
│     SERVICE LAYER (Business Logic)  │  ← Workflows & rules
├─────────────────────────────────────┤
│     DAO LAYER (Data Access)         │  ← SQL queries & CRUD
├─────────────────────────────────────┤
│     DATABASE LAYER (MySQL)          │  ← Tables & data
└─────────────────────────────────────┘
```

### Package Structure

```
src/
├── model/              # Entity classes (POJOs)
│   ├── Customer.java
│   ├── Vehicle.java
│   ├── Technician.java
│   ├── Part.java
│   ├── Location.java
│   ├── RentalTransaction.java
│   ├── PaymentTransaction.java
│   ├── MaintenanceTransaction.java
│   ├── PenaltyTransaction.java
│   └── DeploymentTransaction.java
│
├── dao/                # Data Access Objects
│   ├── CustomerDAO.java
│   ├── VehicleDAO.java
│   ├── TechnicianDAO.java
│   ├── PartDAO.java
│   ├── LocationDAO.java
│   ├── RentalDAO.java
│   ├── PaymentDAO.java
│   ├── MaintenanceDAO.java
│   ├── PenaltyDAO.java
│   └── DeploymentDAO.java
│
├── service/            # Business logic
│   ├── RentalService.java
│   ├── PaymentService.java
│   ├── MaintenanceService.java
│   ├── PenaltyService.java
│   └── DeploymentService.java
│
├── ui/                 # User interfaces
│   ├── CustomerUI.java
│   ├── VehicleUI.java
│   ├── TechnicianUI.java
│   ├── PartUI.java
│   ├── LocationUI.java
│   └── TransactionUI.java
│
├── reports/            # Report generation
│   └── ReportService.java
│
├── util/               # Utilities
│   ├── DBConnection.java
│   └── Helpers.java
│
└── main/               # Application entry
    └── VehicleRentalApp.java
```

---

## 🗄️ Database Schema

### Tables

1. **customers** - Customer information
2. **vehicles** - Vehicle inventory
3. **technicians** - Maintenance staff
4. **parts** - Parts inventory
5. **locations** - Rental branch locations
6. **rentals** - Rental transactions
7. **payments** - Payment records
8. **maintenance** - Maintenance/repair records
9. **penalties** - Customer penalties
10. **deployments** - Vehicle movement tracking
---

### Database Setup

1. **Create database:**
   ```sql
   CREATE DATABASE vehicle_rental_db;
   USE vehicle_rental_db;
   ```

2. **Create tables:**
   - See `SQL_Syntax_CrashCourse/` folder for SQL syntax reference
   - Create all 10 tables based on the schema above
   - Define primary keys, foreign keys, and constraints

3. **Update database credentials:**
   - Open `src/util/DBConnection.java`
   - Update `DB_URL`, `DB_USER`, and `DB_PASSWORD`

### Project Setup

1. **Add MySQL Connector JAR:**
   - Download from: https://dev.mysql.com/downloads/connector/j/
   - Add to project classpath/build path

2. **Add JavaFX SDK (if using JavaFX):**
   - Download from: https://gluonhq.com/products/javafx/
   - Add to project classpath
   - Configure VM arguments: `--module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml`

3. **Test database connection:**
   ```bash
   java util.DBConnection
   ```

---

## 👥 Collaboration Workflow

### Task Assignment

Each team member should work on one layer at a time:

**Person 1 - Model Layer:**
- Implement all entity classes in `model/` package
- Add fields, constructors, getters/setters
- Implement `toString()`, `equals()`, `hashCode()`

**Person 2 - DAO Layer:**
- Implement all DAO classes with SQL queries
- Use `PreparedStatement` for all queries
- Handle exceptions properly

**Person 3 - Service Layer:**
- Implement business logic in service classes
- Coordinate between DAOs
- Implement workflows and rules

**Person 4 - UI Layer:**
- Build JavaFX interfaces
- Create forms and tables
- Connect UI to services

**Person 5 - Reports:**
- Implement report generation
- Create complex SQL queries
- Format output

## 📝 Implementation Guidelines

### 1. Entity Classes (Model)
- Keep classes simple - no business logic
- Only data fields and accessor methods
- Use proper data types (int, String, Timestamp, double)
- Document all fields

### 2. DAO Classes
- **Always use PreparedStatement** to prevent SQL injection
- Use try-with-resources for automatic resource cleanup
- Return `null` when record not found
- Throw or handle `SQLException` appropriately
- Example:
  ```java
  public Customer getCustomerById(int customerId) {
      String sql = "SELECT * FROM customers WHERE customer_id = ?";
      try (Connection conn = DBConnection.getConnection();
           PreparedStatement stmt = conn.prepareStatement(sql)) {
          stmt.setInt(1, customerId);
          ResultSet rs = stmt.executeQuery();
          if (rs.next()) {
              // Build and return Customer object
          }
      } catch (SQLException e) {
          e.printStackTrace();
      }
      return null;
  }
  ```

### 3. Service Classes
- Implement business rules and validations
- Call multiple DAOs if needed
- Use database transactions for atomic operations
- Return meaningful error messages

### 4. UI Classes
- **Never call DAOs directly** - always use services
- Validate user input before processing
- Show clear error messages
- Use tables for displaying lists
- Use forms for data entry

### 5. Reports
- Use SQL JOINs for efficiency
- Add date range filters
- Format output clearly
- Consider exporting to PDF/Excel

---

## 🎯 Key Features to Implement

### Core Transactions
- ✅ Create rental
- ✅ Complete rental
- ✅ Process payment
- ✅ Schedule maintenance
- ✅ Assess penalties
- ✅ Deploy vehicles

### Reports
- ✅ Rental revenue report
- ✅ Defective vehicles report
- ✅ Location rental frequency
- ✅ Customer rental history
- ✅ Top customers
- ✅ Overdue rentals
- ✅ Maintenance costs
- ✅ Fleet distribution

---

## 🐛 Testing

1. **Unit test each DAO method:**
   - Test insert, update, delete, select
   - Test with valid and invalid data

2. **Test service workflows:**
   - Test complete rental workflow
   - Test payment processing
   - Test maintenance completion

3. **Test UI forms:**
   - Test form validation
   - Test data display
   - Test error handling

---

## 📚 Resources

- [MySQL Documentation](https://dev.mysql.com/doc/)
- [JavaFX Documentation](https://openjfx.io/)
- [JDBC Tutorial](https://docs.oracle.com/javase/tutorial/jdbc/)
- SQL Syntax in `SQL_Syntax_CrashCourse/` folder

## ✅ Project Checklist

### Week 1: Foundation
- [ ] Set up MySQL database
- [ ] Import `database_schema.sql`
- [ ] Add MySQL Connector JAR
- [ ] Test database connection (`DBConnection.testConnection()`)
- [ ] Implement all Model classes
- [ ] Test model classes

### Week 2: Data Access
- [ ] Implement basic DAOs (Customer, Vehicle, Location, Technician, Part)
- [ ] Test each DAO method individually
- [ ] Implement transaction DAOs (Rental, Payment, Maintenance, Penalty, Deployment)
- [ ] Test DAO integration

### Week 3: Business Logic
- [ ] Implement `RentalService`
- [ ] Implement `PaymentService`
- [ ] Implement `MaintenanceService`
- [ ] Implement `PenaltyService`
- [ ] Implement `DeploymentService`
- [ ] Test complete workflows

### Week 4: User Interface
- [ ] Design main UI layout
- [ ] Implement `TransactionUI` (priority)
- [ ] Implement other UI classes
- [ ] Connect UI to services
- [ ] Test user workflows

### Week 5: Reports & Testing
- [ ] Implement report queries
- [ ] Generate sample reports
- [ ] End-to-end testing
- [ ] Bug fixes
- [ ] Documentation
