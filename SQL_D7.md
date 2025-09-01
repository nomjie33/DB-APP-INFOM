# DATABASE syntax

<details>
  <summary>SQL CREATE, DROP, BACKUP DATABASE</summary>
  
  ### CREATE Syntax
  ```sql
  CREATE DATABASE testDB;
  ```

  ### DROP Syntax
  ```sql
  DROP DATABASE databasename;
  ```

  ### BACKUP Database
  ```sql
  BACKUP DATABASE databasename;
  TO DISK = 'filepath';
  WITH DIFFERENTIAL; //A differential back up reduces the back up time (since only the changes are backed up).
  ```

</details>

<details>
  <summary>SQL CREATE, ALTER, and DROP Table</summary>

    ### CREATE Database 
  ```sql
  CREATE TABLE table_name (
      column1 datatype,
      column2 datatype,
      column3 datatype.
      ...
  ); 
  ```

  ### Copy table 
  ```sql
  CREATE TABLE new_table_name AS
  SELECT column1, column2,...
  WHERE ....;
  ```

  ### DROP table syntax
  ```sql
  DROP TABLE table_name; 
  ```

  ### SQL TRUNCATE table
  The **TRUNCATE TABLE** statement is used to delete the data inside a table, but not the table itself.
  ```sql
  TRUNCATE TABLE table_name;
  ```

</details>

<details>
  <summary>SQL ALTER TABLE</summary>
  
  ## SQL ALTER TABLE syntax
  ### ALTER TABLE - ADD Column 
  To add a column in a table, use the following syntax: 
  ```sql
  ALTER TABLE table_name
  ADD column_name datatype; 
  ```

  The following SQL adds an "Email" column to the "Customers" table: 
  ```sql
  ALTER TABLE Customers
  ADD Email varchar(255);
  ```

  ### ALTER TABLE - DROP COLUMN 
  To delete a column in a table, use the following syntax (notice that some database systems don't allow deleting a column);
  ```sql
  ALTER TABLE table_name
  DROP COLUMN column_name;
  ```

  ### ALTER TABLE - RENAME COLUMN
  To rename a column in a table, use the following syntax: 
  ```sql
  ALTER TABLE table_name
  RENAME COLUMN old_name to new_name;
  ```

  or
  ```sql
  EXEC sp_rename 'table_name.old_name', 'new_name', 'COLUMN';
  ```

  ### ALTER TABLE - ALTER/MODIFY DATATYPE 
  ```sql
  ALTER TABLE table_name
  ALTER COLUMN column_name datatype;
  ```

</details>

<details>
  <summary>SQL CREATE/ALTER Table constraints</summary>

  Most commonly used constraints
  - NOT NULL - Ensures that a column cannot have a NULL value
  - UNIQUE - Ensures that all values in a column are different
  - PRIMARY KEY - A combination of a **NOT NULL** and **UNIQUE**. Uniquely identifies each row in a table. 
  - FOREIGN KEY - Prevents action that would destroy links between tables
  - CHECK - Ensures that the values in a column satisfies a specific condition
  - DEFAULT - Sets a default value for a column if no value is specified 
  - CREATE INDEX - Used to create and retrieve data from the database very quickly

  ### SQL NOT NULL Constraint 
  Enforces to not accept null values 

  #### SYNTAX
  CREATE
  ```sql
  CREATE TABLE Persons (
    ID int NOT NULL,
    LASTName varchar(255) NOT NULL,
    FirstName varchar(255) NOT NULL,
    Age int
  );
  ```
  ALTER
  ```sql
  ALTER TABLE Persons
  ALTER COLUMN Age int NOT NULL; 
  ```

  #### SQL UNIQUE constraint
  Ensures that all values in a column are different. 
  
  Both the **UNIQUE** and **PRIMARY KEY** constraints provide a guarantee for uniqueness for a column or set of columns 

  A **PRIMARY KEY** constraint automatically has a **UNIQUE** constraint. 

  You can have many **UNIQUE** constraints per table but onnly one **PRIMARY KEY** constraint per table.

  MY SQL: 
  ```sql
  CREATE TABLE Persons (
    ID int NOT NULL,
    LastName varchar(255) NOT NULL,
    FirstName varchar(255),
    Age int,
    UNIQUE (ID) 
  );
  ```

  ```sql
  ALTER TABLE Persons
  ADD UNIQUE (ID);
  ```

  #### Multiple Unique (CREATE): 
  ```sql
  CREATE TABLE Persons (
    ID int NOT NULL,
    LastName varchar(255) NOT NULL,
    FirstName varchar(255),
    Age int,
    CONSTRAINT UC_Person UNIQUE (ID, LastName)
  );
  ```

  #### Multiple Unique (ALTER):
  ```sql
  ALTER TABLE Persons
  ADD CONSTRAINT UC_Person UNIQUE (ID, Lastname); 
  ```
  ### SQL PRIMARY KEY 
  ```sql
  CREATE TABLE Persons (
    ID int NOT NULL,
    LastName varchar(255) NOT NULL,
    FirstName varchar(255).
    Age int,
    PRIMARY KEY (ID)
  );
  ```

</details>
