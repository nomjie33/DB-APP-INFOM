<details>
  <summary>Tutorial 25: UNION AND UNION ALL</summary>

  #### UNION Syntax 
  ```sql
  SELECT column_name(s) FROM table1
  UNION
  SELECT column_name(s) FROM table2; 
  ```

  ##### Example
  ```sql
  SELECT City FROM Customers
  UNION
  SELECT City FROM Suppliers
  ORDER BY City; 
  ```
  
  #### UNION ALL Syntax 
  ```sql
  SELECT column_name(s) FROM table1
  UNION ALL
  SELECT column_name(s) FROM table2; 
  ```
  ##### Example
  ```sql
  SELECT City FROM Customers
  UNION ALL
  SELECT City FROM Suppliers
  ORDER BY City; 
  ```

</details>

<details> 
<summary> Tutorial 26: The SQL GROUP BY Statement</summary>
  The **GROUP BY** statement groups rows that have the same values into summary rows, like "find the number of customers in each country". 

  The **GROUP BY** statement is often used with aggregate functions (**COUNT()**, **MAX()**, **MIN()**, **SUM()**, **AVG()**) to group the result set by one or more columns. 

  #### GROUP BY Syntax 
  ```sql
  SELECT column_name(s)
  FROM table_name
  WHERE condition
  GROUP BY column_name(s)
  ORDER BY column_name(s); 
  ```

</details>

<details>
  <summary> Tutorial 27: The SQL HAVING Clause</summary>
  The **HAVING** clause was added to SQL because the **WHERE** keyword cannot be used with aggregate functions. 

  #### HAVING Syntax
  ```sql
  SELECT column_name(s)
  FROM table_name
  WHERE condition
  GROUP BY column_name(s)
  HAVING condition
  ORDER BY column_name(s)
  ```

  ##### Example
  ```sql
  SELECT Employees.LastName, COUNT(Orders.OrderID) AS NumberOfOrders
  FROM Orders
  INNER JOIN Employees ON Orders.EmployeeID = Employees.EmployeeID
  WHERE LastName = 'Davolio' OR LastName = 'Fuller'
  GROUP BY LastName
  HAVING COUNT (Orders.OrderID) > 25;
  ```
</details>

<details>
  <summary>Tutorial 28: The SQL EXISTS Operator</summary>

  The **EXISTS** operator is used to test for the existence of any record in a subquery 

  The **EXISTS** operator returns TRUE if the subquery returns one or more records. 

  #### EXISTS Syntax
  ```sql
  SELECT column_name(s)
  FROM table_name
  WHERE EXISTS (SELECT column_name FROM table_name WHERE condition); 
  ```
  ##### Example 
  ```sql
  SELECT SupplierName
  FROM Suppliers
  WHERE EXISTS (SELECT ProductName FROM Products WHERE products.SupplierID = Suppliers.supplierID AND Price = 22); 
  ```

</details>

<details>
  <summary>Tutorial 29: The SQL ANY and ALL Operators</summary>

  The **ANY** and **ALL** operators allow you to perform a comparison between a single coolumn value and a range of other vlaues.

  ### The SQL ANY Operator 
  The **ANY** operator:

- returns a boolean value as a result
- returns TRUE if ANY of the subquery values meet the condition

  **ANY** means that the condition will be true if the operation is true for  any of the values in the range.

  #### ANY Syntax
  ```sql
  SELECT column_name(s)
  FROM table_name
  WHERE column_name operator ANY (SELECT column_name FROM table_name WHERE condition); 
  ```

  **NOTE**: The operator must be a standard comparison operator (=, <>, !=, >, >=, <, or <=).

  #### The SQL ALL Operator
  The **ALL** operator:

- returns a boolean value as a result
- returns TRUE if ALL of the subquery values meet the condition
- is used with **SELECT**, **WHERE**, and **HAVING** statements

  **ALL** means that the condition will be true only if the operation is true for all values in the range.

  #### ALL Syntax with SELECT
  ```sql
  SELECT ALL column_name(s)
  FROM table_name
  WHERE condition;
  ```
  
  #### ALL Syntax with WHERE or HAVING
  ```sql
  SELECT column_name(s)
  FROM table_name
  WHERE column_name operator ALL (SELECT column_name FROM table_name WHERE condition);
  ```
  
  **NOTE**: The operator must be a standard comparison operator (=, <>, !=, >, >=, <, or <=).
  
</details>

<details>
  <summary>Tutorial 30: The SQL SELECT INTO Statement</summary>

  The **SELECT INTO** statement copies data from one table into a new table. 
  
  #### SELECT INTO Syntax
  ```sql
  SELECT * INTO newtable [IN externaldb] FROM oldtable
  WHERE condition; 
  ```

  Copy only some columns into a new table:
  ```sql
  SELECT column1, column2, column3, ...
  INTO newtable [IN externaldb] FROM oldtable
  WHERE condition; 
  ```

</details>

<details>
  <summary> Tutorial 31: The SQL INSERT INTO SELECT Statement</summary>
  The **INSERT INTO SELECT** statement copies data from one table and inserts it into another table. 

  The **INSERT INTO SELECT** statement requires that the data types in source and target tables match. 

  **Note:** The existing records in the target table are unaffected. 

  #### INSERT INTO SELECT Syntax 
  Copy all columns from one table to another table: 
  ```sql
  INSERT INTO table2
  SELECT * FROM table1
  WHERE condition; 
  ```

  Copy only some columns from one table into another table: 
  ```sql
  INSERT INTO table2 (column1, column2, column3, ...)
  FROM table1
  WHERE condition; 
  ```
</details>

<details> 
<summary> Tutorial 32: The SQL CASE Expression</summary>

  The **CASE exression goes through conditions and returns a value when the first condition is met (like an if-then-else statement). So, once a condition is true, it will stop reading and return the result. If no conditions are true, it returns the value in the **ELSE** clause. 

  If there is no **ELSE** part and no conditions are true, it returns NULL.

  #### CASE Syntax
  ```sql
  CASE
    WHEN condition1 THEN result1
    WHEN condition2 THEN result2
    WHEN condition3 THEN resultN
    ELSE result
  END; 
  ```
</details>

<details>
<summary>Tutorial 33: SQL IFNULL(), ISNULL(), COALESCE(), and NVL() Functions</summary>

Look at the following "Products" table: 
| P_Id | ProductName | UnitPrice | UnitsInStock | UnitsOnOrder | 
|------|-------------|-----------|--------------|--------------|
| 1 | Jarlsberg | 10.45 | 16 | 15 | 
| 2 | Mascarpone | 32.56 | 23 | |
| 3 | Gorgonzola | 15.67 | 9 | 20 | 

Suppose that the "UnitsOnOrder" column is optional, and may contain NULL values. 

Look at the following SELECT statement: 

```sql
SELECT ProductName, UnitPrice * (UnitsInStock + UnitsOnOrder)
FROM Products; 
```

In the example above, if any of the "UnitsOnOrder" values are NULL, the result will be NULL.

**MySQL** 
The MySQL **IFNULL()** function lets you return an alternative value if an expression is NULL:

```sql
SELECT ProductName, UnitPrice * (UnitsInStock + IFNULL (UnitsOnOrder, 0))
FROM Products; 
```

or we can use the COALESCE() function, like this: 

```sql
SELECT ProductName, UnitPrice * (UnitsInStock + COALESCE(UnitsOnOrder, 0))
FROM Products; 
```
  
</details>

<details>

  <summary>Tutorial 34: SQL Stored Procedures for SQL Server</summary>

  A stored prodecure is a prepared SQL code that you can save, so the code can be reused over and over again. 

  So if you have an SQL query that you write over and over again, save it as a stored procedure, and then just call it to execute it. 

  You can also pass parameters to a stored procedure, so that the stored procedure can act based on the parameter value(s) that is passed. 

  #### Stored Procedure Syntax 
  ```sql
  CREATRE PROCEDURE procedure_name
  AS sql_statement
  GO; 
  ```

  #### Execute a stored procedure 
  ```sql
  EXEC procedure_name; 
  ```
</details>



<details>
  <summary> Tutorial 35: SQL Operators</summary>

  https://www.w3schools.com/sql/sql_operators.asp
</details>
