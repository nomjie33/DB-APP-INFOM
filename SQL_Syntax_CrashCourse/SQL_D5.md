<details>
  <summary>Tutorial 20: SQL JOIN</summary>

  A **JOIN** clause is used to combine rows from two or more tables, based on a related column between them.

  Let's look at a selection from the "Orders" table: 

  | OrderID | CustomerID | OrderDate | 
  |---------|------------|-----------|
  |10308 | 2 | 1996-09-18 | 
  | 10309 | 37 | 1996-09-19 | 
  | 10310 | 77 | 1996-09-20 |

  Then, look at a selection from the "Customers" table: 

  | CustomerID | CustomerName | ContactName | Country | 
  |------------|--------------|-------------|---------|
  | 1 | Aldreds Futterkiste | Maria Aders | Germany | 
  | 2 | Ana Trujillo Emparedados y helados | Ana Trujillo | Mexico | 
  | 3 | Antonio Moreno Raqueria | Antonio Moreno | Mexico | 

  Notice that the "CustomerID" column in the "Orders" table refers to the 
  
  CustomerID" in the "Customers' table. The relationship between the two tables above is the "CustomerID" column. 

  Then, we can create the following SQL statement (that contains an INNER JOIN), which selects records that have matching values in both tables: 

  ```sql
  SELECT Orders.OrderID, Customers.CustomerName, Orders.OrderDate
  FROM Orders
  INNER JOIN Customers ON Orders.CustomerId=Customers.CustomerID; 
  ```

| OrderID | CustomerName | OrderDate | 
|---------|--------------|-----------|
| 10308 | Ana Trujillo Emparedados y helados | 9/18/1996 | 
| 10365 | Antonio Moreno Taqueia | 11/27/1996 |  
| 10383 | Around the Horn | 12/16/1996 | 
| 10355 | Around the Horn | 11/15/1996 | 
| 10278 | Berglunds snabbkop | 9/12/1996 | 

#### Different Types of SQL JOINs 
Here are the different types of the JOINs in SQL: 

![Alt Image](https://www.w3schools.com/sql/img_inner_join.png)
- (INNER) JOIN: Returns records that have matching values in both tables

![Alt Image](https://www.w3schools.com/sql/img_left_join.png)
- LEFT (OUTER) JOIN: Returns all records from the left table, and the matched records from the right table

![Alt Image](https://www.w3schools.com/sql/img_right_join.png)
- RIGHT (OUTER) JOIN: Returns all records from the right table, and the matched records from the left table

![Alt Image](https://www.w3schools.com/sql/img_full_outer_join.png)
- FULL (OUTER) JOIN: Returns all records when there is a match in either left or right table

</details>

<details>
  <summary>Tutorial 21: SQL INNER JOIN</summary>

  The **INNER JOIN** keyword selects records that have matching values in both tables. 

  Let's look at a selection of the Products table: 

  | ProductID | ProductName | CategoryID | Price | 
  |-----------|-------------|------------|-------|
  | 1 | Chais | 1 | 18 | 
  | 2 | Chang | 1 | 19 | 
  | 3 | Aniseed Syrup | 2 | 10 | 

  And a selection of the Categories table 

  | CategoryID | CategoryName | Description | 
  | 1 | Beverages | Soft drinks, coffees, teas, beers, and ales | 
  | 2 | Condiments | Sweet and savory sauces, relishes, spreads, and seasonings |
  | 3 | Confections | Desserts, candies, and sweet breads | 

  We will join the Products table with the Categories table by using the CategoryID field
  from both tables: 

  ##### Example 
  Join Products and Categories with the INNER JOIN keyword: 
  ```sql
  SELECT ProductID, ProductName, CategoryName
  FROM Products
  INNER JOIN Categories ON Products.CategoryID = Categories.CategoryId; 
  ```

  ![Alt Image](https://www.w3schools.com/sql/img_inner_join.png)

  ```markdown
  The INNER JOIN keyword returns only rows with a match in both tables. Which means that if   you have a product with no CategoryID, or CategoryId that is not present in the         Categories table. That record would not be returned in the result. 
  ```

  #### Naming the Columns 
  It is a good practice to include the table name when specifying columns in the SQL statement. 

  ##### Example 
  ```sql
  SELECT Products.ProductID, Products.ProductName, Categories.CategoryName
  FROM Products
  INNER JOIN Categories ON Products.CategoryID = Categories.CategoryID;
  ```
  The example above works without specifying table names, because none of the specified column names are present in both tables. If you try to include CategoryID in the SELECT statement, you will get an error if you do not specify the table name (because CategoryID is present in both tables).

  #### JOIN or INNER JOIN 
    **JOIN** and **INNER JOIN** will return the same result. 

    **INNER** is the default join type for **JOIN**, so when you write **JOIN** the parser actually writes **INNER JOIN**. 

    #### JOIN Three Tables 
    ##### Example 
    ```sql
    SELECT Orders.OrderID, Customers.CustomerName, Shippers.ShipperNae 
    FROM (Orders INNER JOIN Customers ON Orders.CustomerID = Customers.CustomerID) 
    ```
</details>

<details> 
<summary>Tutorial 22: SQL LEFT JOIN Keyword</summary>
  The **LEFT JOIN** keyword returns all records from the left table (table1), and the 
  matching records from the right table (table2). The result is 0 records from the right table (table2). The result is 0 records from the right side if there is no match. 

#### LEFT JOIN Syntax 
```sql
SELECT column_name(s)
FROM table1
LEFT JOIN table2
ON tbale1.column_name = table2.column_name; 
```

**Note**: The **LEFT JOIN** keyword returns all records from the left table (Customers), even if there are no matches in the right table (Orders). 

</details>

<details> 
<summary>Tutorial 23: SQL RIGHT JOIN Keyword</summary>
  The **RIGHT JOIN** keyword returns all records from the right table (table2), and the matching records from the left table 9table1). The result is 0 records from the left side, if there is no match. 

  #### RIGHT JOIN Syntax
  ```sql
  SELECT column_name(s)
  FROM table1
  RIGHT JOIN table2
  ON table1.column_name = table2.column_name; 
  ```
*8Note**: The **RIGHT JOIN** keyword returns all records from the right table (Employees), even if there are no matches in the left table (Orders).
</details>

<details>
  <summary>Tutorial 24: SQL FULL OUTER JOIN Keyword</summary>
  The **FULL OUTER JOIN** keyword returns all records when there is a match in left (table1) or right (table2) table records. 

  **Tip**: **FULL OUTER JOIN** and **FULL JOIN** are the same.

  #### FULL OUTER JOIN Syntax 
  ```sql
  SELECT column_name(s)
  FROM table1
  FULL OUTER JOIN table2
  ON table1.column_name = table2.column_name
  WHERE condition; 
  ```
  #### SQL FULL OUTER JOIN Example
  The following SQL statement selects all customers, and all orders: 
  ```sql
  SELECT Customers.CustomerName, Orders.OrderID
  FROM Customers
  FULL OUTER JOIN Orders ON Customers.CustomerID=Orders.CustomerID
  ORDER BY Customers.CustomerName; 
  ```

  **Note:** The **FULL OUTER JOIN** keyword returns all matching records from both tables whether the other table matches or not. So, if there are rows in "Customers" that do not have matches in "Orders", or if there are rows in "Orders" that do not have matches in "Customers", those rows will be listed as well. 

</details>

<details> 
<summary>Tutorial 24: SQL Self Join</summary>
  A self join is a regular join, but the table is joined with itself. 

  #### Self Join Syntax 
  ```sql
  SELECT column_name(s)
  FROM table1 T1, table2 T2
  WHERE condition;
  ```

  #### SQL Self Join Example
  ```sql
  SELECT A.CustomerName AS CustomerName1, B.CustomerName AS CustomerName2, A.City
  FROM Customers A, Customers B
  WHERE A.CustomerID <> B.CustomerID
  AND A.City = B.City
  ORDER BY A.City 
  ```

</details>
