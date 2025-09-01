
## SQL Aggregate Functions
### Most commonly used: 
- MIN() - returns the smallest value within the selected column
- MAX() - returns the highest value within the selected column
- COUNT() - returns the number of rows in a set
- SUM() - returns the total sum of a numerical column
- AVG() - returns the average value of a numerical column

<details>
  <summary>Tutorial 12: The SQL MIN() and MAX() Functions</summary>
  The **MIN()** function returns the smalles value of the selected column

  ##### MIN Example
  Find the lowest price in the Price column
  ```sql
  SELECT MIN(Price) FROM Products;
  ```

  ##### MAX Example
  Find the highest price in the Price column
  ```sql
  SELECT MAX(Price) FROM Products;
  ```
  #### Set Column Name(Alias)
  When you use **MIN()** or **MAX()**, the returned column will not have a descriptive name. To give
  the column a descriptive name, use the **AS** keyword: 
  ```sql
  SELECT MIN(Price) AS SmallestPrice FROM Products; 
  ```

  #### Use MIN() with GROUP BY
  Here we use the **MIN()** function and the **GROUP BY** clause, to return the smalles price for each

  category in the Porducts table:
  ```sql
  SELECT MIN(Price) AS SmallestPrice, CategoryID
  FROM Products
  GROUP BY CategoryID;
  ```
  
</details>

<details>
  <summary>Tutorial 13: The SQL COUNT() Function</summary>
  The **COUNT()** function returns the number of rows that matches a specified criterion.

  ```sql
  SELECT COUNT(*)
  FROM Products; 
  ```

  #### Specify Column
  You can specify a column name instead of the asterix symbol (*). 

  If you specify a column name instead of (*), NULL values will not be counted.

  ##### Example
  Find the number of products where the ProductName is not null: 
  ```sql
  SELECT COUNT(ProductName)
  FROM Products; 
  ```

  #### Add a WHERE Clause 
  You can add a **WHERE** clause to specify conditions: 

  ```sql
  SELECT COUNT(ProductID)
  FROM Products
  WHERE Price > 20;
  ```

  #### Ignore Duplicates
  ```sql
  SELECT COUNT(DISTINCT Price)
  FROM Products; 
  ```

  #### Use an Alias
  ```sql
  SELECT COUNT(*) AS [Number of records]
  FROM Products;
  ```

  #### Use COUNT() with GROUP BY
  ```sql
  SELECT COUNT(*) AS [Number of records], CategoryID
  FROM Products
  GROUP BY CategoryID;
  ```
</details>

<details> 
<summary>Tutorial 14: The SQL SUM() and AVG() </summary>
  The SUM() function returns the total sum of a numeric column. 
  ```sql
  SELECT SUM(Quantity)
  FROM OrderDetails;
  ```

  #### SUM() With an Expression

  If we assume that each product in the OrderDetails column costs 10 dollars, we can find 
  the total earnings in dollars by multipying each quantity with 10: 

  Example: 
  ```sql
  SELECT SUM(Quantity * 10)
  FROM OrderDetails
  ```

  We can also join the OrderDetails table to the Products table to find the actual amount, instead

  of assuming it is 10 dollars: 

  ##### Exxample
  ```sql
  SELECT SUM(Price * QUantity)
  FROM OrderDetails
  LEFT JOIN Products ON OrderDetails.ProductID = Products.ProductID; 
  ```

  More Abount Joins later

  ### SQL AVG() Function
  The **AVG()** function returns the average value of a numeric column. (Null values are ignored)

  ```sql
  SELECT AVG(Price)
  FROM Products; 
  ```

  #### Higher Than Average
  ```sql
  SELECT * FROM Products
  WHERE price > (SELECT AVG(Price) FROM Products); 
  ```
</details>

