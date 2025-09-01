# SQL W3schools (DAY 2) 

<details>
  <summary>Tutorial 5: The SQL ORDER BY</summary>

  The **ORDER BY** keyword is used to sort the result-set in ascending or descending order.

  ##### Example 
  Sort the product by price: 
  
  ```sql
  SELECT * FROM Products
  ORDER BY Price;
  ```

  #### Syntax
  
  ```sql
  SELECT column1, column2, ...
  FROM table_name
  ORDER BY column1, column2, ... ASC | DESC; 
  ```

  #### DESC 
  The **ORDER BY** keyword sorts the records in ascending order by defaul. To sort the records in 
  
  descending order, use the **DESC keyword. 

  ##### Example 
  
  ```sql
  SELECT * FROM Products
  ORDER BY Price DESC; 
  ```

  #### Order Alphabetically 
  For string values, the **ORDER BY** keyword will order alphabetically: 
  
  ```sql
  SELECT * FROM Products
  ORDER BY ProductName;
  ```

  #### Alphabetically DESC 
  To sort the table reverse alphabetically, use the **DESC** keyword: 
  
  ```sql
  SELECT * FROM Products
  ORDER BY ProductName DESC; 
  ```

  #### ORDER BY Several Columns 

  ```sql
  SELECT * FROM Customers
  ORDER BY Country, CustomerName; 
  ```

  #### Using Both ASC and DESC 
  The following SQL statement selects all customers from the "Customers" table, sorted ascending by      the "Country" table, sorted ascending by the "Country" and descending the "CustomerName" column: 

  ```sql
  SELECT * FROM Customers
  ORDER BY Country ASC, CustomerName DESC; 
  ```
</details>

<details> 
<summary>Tutorial 6: The SQL AND Operator</summary>
  
  The **WHERE** clause can contain one or many AND operators


  The **AND** operator is used to filter records based on more than one condition, like if you want to   return all customers from Spain that start with the letter 'G': 

  ##### Example 
  Select all customers from Spain that start with the letter 'G': 
  SELECT * FROM Customers
  WHERE Country = 'Spain' AND CustomerName LIKE 'G%'; 

  #### Syntax 
  ```sql
  SELECT column1, column2, ...
  FROM table_name
  WHERE condition1 AND condition2 AND condition3 ...; 
  ```

  #### All Conditions Must Be True  
  The following SQL statement selects al lfields from Customers where Country is "Brazil" 

  **AND** City is "Rio de Janeiro" **AND** CustomerID is higher than 50: 

  #### Combining AND and OR
  You can combine the **AND** and **OR** Operators 
  
  The following SQL statement selects all customers from Spain that starts with a "G" or an "R" 
  
  Make sure you use parentheses to get the correct result. 

  ```sql
  SELECT * FROM Customers
  WHERE Country = 'Spain' AND (CustomerName LIKE 'G%' OR CustomerName LIKE 'R%');
  ```

  Without parentheses, the select statement will return all customers from Spain that start with a     "G", plus all customers that start with an "R", regardless of the country value: 

  ##### Example 
  Select all customers that either: 

  are from Spain and starts with either 'G", or starts with the letter "R": 

  ```sql
  SELECT * FROM Customers
  WHERE Country = 'Spain' AND CustomerName LIKE 'G%' OR CustomerName LIKE 'r%';
  ```
  
</details>

<details>
  <summary>Tutorial 7: The NOT Operator</summary>
  The **NOT** operator is used in combination with other operators to give the opposite result, also called the negative result.

  In the select statement below we want to return all customers that are **NOT** from Spain: 
  ##### Example
  Select only the customers that are **NOT** from Spain: 
  
  ```sql
  SELECT * FROM Customers
  WHERE NOT Country = 'Spain'; 
  ```
  #### Syntax

  ```sql
  SELECT column1, column2, ...
  FROM table_name
  WHERE NOT condition; 
  ```

  #### NOT LIKE
  
  ##### Example 
  Select customers that does not start with the letter 'A' ;
  
  ```sql 
  SELECT * FROM Customers
  WHERE CustomerName NOT LIKE 'A%';
  ```

  #### NOT BETWEEN

  ##### Example
  Select customers with a customerID not between 10 and 60: 

  ```sql
  SELECT * FROM Customers
  WHERE CustomerID NOT BETWEEN 10 AND 60;
  ```

  #### NOT IN 

  ##### Example
  Select customers that are not from Paris or London:

  ```sql
  SELECT * FROM Customers
  WHERE City NOT IN ('Paris', 'London')
  ```

</details>

<details> 
<summary>Tutorial 8: The SQL INSERT INTO Statement</summary>

  The **INSERT INTO** statement is used to insert new records in a table.

  #### INSERT INTO Syntax

  It is possible to write the **INSERT INTO** statement in two ways:

  1. Specify both the column names and the values to be inserted:

  ```sql
INSERT INTO table_name (column1, column2, column3, ...)
VALUES (value1, value2, value, ...);
  ```

  2. If your are adding values for all the columns of the table, you do not need to specify

     the column names in the SQL. Query. However, make sure the order of the values is in the same

     order of the values is the same order as the columns in the table. Here, the **INSERT INTO**

     syntax would be as follows:

```sql
INSERT INTO table_name
VALUES (value1, value2, value, ...);
```

#### INSERT INTO Example
The following SQL statement inserts a new record in the "Customers" table:

##### Example 
```sql
INSERT INTO Customers (CustomerName, ContactName, Address, City, PostalCode, Country(
VALUES ('Cardinal', 'Tom B. Erichsen', 'Skagen 21', 'Stavanger', '4006', 'Norway');
```

NOTE: If you only put values on specified columns, the other unfilled columns will be null

</details>

<details> 
<summary>Tutorial 9: SQL NULL Values</summary>

  A field with a NULL value is a field with no value. 

  If a field in a table is optional, it is possible to insert a new record or update a record

  without adding a value to this field. Then, the field will be saved with a NULLL value. 

  ```markdown
  NOTE: A NULL value is different from a zero value or a field that contains spaces. A
  field with a NULL value is one that has been left blank during record creation!
  ```
  
  #### How to Test for NULL Values? 

  It is not possible to test for NULL values with comparison operators, such as =, <, or <>

  We will have to use the **IS NULL** and **IS NOT NULL** operators instead.

  #### IS NULL Syntax 
  ```markdown
  SELECT column_names
  FROM table_name
  WHERE column_name IS NULL;
  ```

  #### IS NOT NULL Syntax 
  ```markdown
  SELECT column_names
  FROM table_names
  WHERE column_name IS NOT NULL;
  ```

</details>

<details> 
<summary>Tutorial 10: The SQL UPDATE and DELETE Statement</summary>

  The **UPDATE** statement is used to modify the existing recors in a table. 

  #### UPDATE Syntax 
  ```sql
  UPDATE table_name
  SET column1 = value1, column2 = value2, ...
  WHERE condition;
  ```

  ```markdown
  Note: Be careful when updating records in a table! Notice the WHERE clause in the UPDATE.
  The WHERE clause specifies which record(s) that should be updated. If you omit the WHERE
  clause, all records in the table will be updated. 
  ```

  ##### Example 
  The following SQL statement updates the first customer (CustomerID = 1) with a new 
  contact person and a new city. 

  ```sql
  UPDATE Customers
  SET ContactName = 'Alfred Schmidt', City = 'Frankfurt'
  WHERE CustomerID = 1; 
  ```

  #### UPDATE Multiple Records 
  It is the **WHERE** clause that determines how many records will be updated.

  The following SQL statement will update the ContactName to "Juan" for all records where country is "Mexico":

  ##### Example 
  ```sql
  UPDATE Customers
  SET ContactName = 'Juan'
  WHERE Country = 'Mexico";
  ```
  
  #### Delete All Records 
  It is possible to delete all rows in a table without deleting the table. This means that the table 
  structure, attributes, and indexes will be intact: 

  ```sql
  DELETE FROM table_name; 
  ```

  #### Delete a table (DROP)
  ```sql
  DROP TABLE Customers; 
  ```

</details>

<details> 
<summary>Tutorial 11: SQL TOP, LIMIT, FETCH FIRST or ROWNUM Clause</summary>

  #### The SQL SELECT TOP Clause
  The **SELECT TOP** clause is used to specify the number of records to return. 

  The **SELECT TOP** clause is useful on large table with thousands of records.

  Returning a large numebr of records can impact performance. 

  ##### Example 
  
  Select only the first 3 records of the Customers table: 
  ```sql
  SELECT TOP 3 * FROM Customers;
  ```

  ```markdown
  Note: Not all database systems support the SELECT TOP clause. MySQL supports the LIMIT clause to select a       limited number of records, while Oracle uses FETCH FIRST n ROWS ONLY and ROWNUM.
  ```

  #### SQL Server / MS Access Syntax: 
  ```sql
  SELECT TOP number|percent column_name(s)
  FROM table_name
  WHERE condition;
  ```

  #### MySQL Syntax
  ```sql
  SELECT column_name(s)
  FROM table_name
  WHERE condition
  LIMIT number; //selects first 3
  ```

  #### Oracle 12 Syntax:
  ```sql
  SELECT column_names(s)
  FROM table_name
  ORDER BY column_name(s)
  FETCH FIRST number ROWS ONLY; //Ex: FETCH FIRST 3 ROWS ONLY 
  ```
  
</details>
