<details>
  <summary>Tutorial 15: The SQL LIKE Operator</summary>
  The **LIKE** operator is used in a **WHERE** clause to search for a specified pattern in a column. 

  - The percent sign % represents zero, one, or multiple characters
  - The underscore sign _ represents one, single character

  #### The _ Wildcard
  The _ wildcard represents a single character. 

  It can be any character or number, but each _ represents one, and only one, character. 

  Example 
  Return all customers from a city that starts with 'L' followed by one wildcard character, then 'nd' 
  and then two wildcard characters: 
  ```sql
  SELECT * FROM Customers
  WHERE city LIKE 'L_nd__'; 
  ```

  #### The % Wildcard
  The % wildcard represents any number of characters, even zero characters.

  Example
  Return all customers from a city that contains the letter 'L': 
  ```sql
  SELECT * FROM Customers
  WHERE city LIKE '%L%';

  #### Starts with
  ```sql
  SELECT * FROM Customers
  WHERE CustomerName LIKE 'a%' OR CustomerName LIKE 'b%';
  ```

  #### Ends With 
  ```sql
  SELECT * FROM Customers
  WHERE CustomerName LIKE 'a%';
  ```

  #### Starts and ends with 
  ```sql
  SELECT * FROM Customers
  WHERE CustomerName LIKE 'b%s';
  ```

  #### Contains 
  ```sql
  SELECT * FROM Customers
  WHERE CustomerName LIKE '%or%'; 
  ```
  
</details>

<details> 
<summary> Tutorial 16: SQL Wildcard Chracters</summary>
  A wildcard character is used to substitute one or more characters in a string 

  #### Wildcard Characters 
  | Symbol | Description | 
  |--------|-------------|
  | % | Represents zero or more characters |  
  | _ | Represents a single character | 
  | [] | Represents any single character within the brackets * | 
  | ^ | Represents any characters not in the brackets * | 
  | - | Represents any single character within the specified range * | 

  * Not supported in PostgreSQL and MySQL databases.
  ** Supported only in Oracle databases.

  #### Using the % Wildcard 
  ##### Example 
  Return all customers that end with the pattern 'es': 
  ```sql
  SELECT * FROM Customers
  WHERE CustomerName LIKE '%es'
  ```

  Return all customers that contains the pattern 'mer' 
  ```sql
  SELECT * FROM Customers
  WHERE CustomerName LIKE '%mer%'; 
  ```

  #### Using the _ Wildcard 
  ##### Example 
  Return all customers with a City starting with any character, followed by "ondon": 
  ```sql
  SELECT * FROM Customers
  WHERE City LIKE '_odon"; 
  ```

  Return all customers with a City starting with "L", followede by any 3 characters, ending with "on":
  ```sql
  SELECT * FROM Customers
  WHERE City LIKE 'L___on';
  ```

  #### Using the [] Wildcard
  The [] wildcard returns a result if any of the characters inside gets a match.
  ##### Example 
  Return all customers starting with either "b", "s", or "p": 
  ```sql
  SELECT * FROM Customers
  WHERE CustomerName LIKE '[bsp]%';
  ```

</details>

<details>
  <summary>Tutorial 17: The SQL IN Operator</summary>
  The IN operator allows you to specify multiple values in a WHERE clause. 

  The IN operator is a shorthand for multiple OR conditions/ 

  ##### Example 
  Return all customers from 'Germany', 'France', or 'UK' 
  ```sql
  SELECT * FROM Customers
  WHERE Country IN ('Germany', 'France', 'UK'); 
  ```

  ##### NOT IN 
  ##### Example 
  Return all customers that are NOT from 'Germany', 'France' or 'UK: 
  ```sql
  SELECT * FROM Customers
  WHERE Country NOT IN ('Germany', 'France', 'UK'); 
  ```
  
</details>

<details> 
<summary>Tutorial 18: The SQL BETWEEN Operator</summary>
  An inclusive operator that selects values within a given range. The values can be numbers, text, or
  dates. 

  ##### Example 
  Selects all products with a price between 10 and 20: 
  ```sql
  SELECT * FROM Products
  WHERE Price BETWEEN 10 AND 20;
  ```

  #### NOT BETWEEN 
  To display the products outside the range of the previous example, use NOT BETWEEN: 

  ##### Example
  ```sql
  SELECT * FROM Products
  WHERE Price NOT BETWEEN 10 AND 20; 
  ```
  #### BETWEEN Text Values 
  The following SQL statement selects all products with a ProductName alphabetically between            Carnarvon Tigers and Mozzarella di Giovanni: 

  ```sql
  SELECT * FROM Products
  WHERE ProductName BETWEEN 'Carnavon Tigers' AND 'Mozzarella di Giovanni'
  ORDER BY ProductName; 
  ```
  #### BETWEEN Dates 
  ```sql
  SELECT * FROM Orders
  WHERE OrderDate BETWEEN #07/01/1996# AND #07/31/1996#;
  ```
</details>

<details>
  <summary>Tutorial 19: SQL Aliases</summary>
  SQL aliases are used to give a table, or a column in a table, a temporary name. 

  Aliases are often used to make column names more readable. 

  An alias only exists for the duration of that query. 

  An alias is created with the AS keyword. 

  ```sql
  SELECT CustomerID AS ID
  FROM Customers; 
  ```

  #### Alias for Columns 
  ##### Example
  The following SQL statement creates two aliases, one for the CustomerID column and one for the        CustomerName column: 
  ```sql
  SELECT CustomerID AS ID, CustomerName AS Customer
  FROM Customers; 
  ```

  #### Alias with a space character
  ##### Example
  ```sql
  SELECT ProductName AS [My Great Products]
  FROM Products; 
  ```

  ```sql
  SELECT ProductName AS "My Great Products"
  FROM Products; 
  ```

  #### Concatenate Columns 
  The following SQL statement creates an alias named "Address" that combine four columns 
  (Address, PostalCode, City and Country): 

  (MySQL)
  ```sql
  SELECT CustomerName, CONCAT(Address + ', ' + PostalCode + ' ' + City + ', '  + Country) AS Address
  FROM Customers;  
  ```
</details>
