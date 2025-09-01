# SQL tutorial from W3Schools (DAY 1)
## SQL Syntax

### DEMO TABLE
  |  CustomerID  |  CustomerName  |  ContactName  |  Address  |  City  |  PostalCode  |  Country  |
  |--------------|----------------|---------------|-----------|--------|--------------|-----------|
  | 1 | Alfreds Futterkiste | Maria Anders | Obere Str. 57 | Berlin | 122209 | Germani |
  | 2 | Ana Trujilo Emparedados y helados | Ana Trujilo | Avda. de la Constitucion 2222 | Mexico D.F. | 05021 | Mexico |
  | 3 | Antonio Moreno Taqueria | Antonio Moreno | Mataderos 2312 | Mexico D.F | 05-23 | Mexico |
  | 4 | Around the Horn | Thomas Hardy | 120 Hanover sq. | London | WA1 1DP | UK |
  | 5 | Berglunds snabbkop | Christina Berglund | Berguvsvagen 8 | Lulea | S-958 22 | Sweden | 
  
<details> 
  <summary>Tutorial 1: SQL Statements</summary>

  This statement returns all records from a table named "Customers": 
  
  ```sql
  SELECT * FROM Customers;
  ```
  SQL Keywords are NOT case sensitive: **select** is the same as **SELECT**
  
  Semicolon is for seperating SQL Statements

  ### Some of the Most Important SQL Commands 
  - SELECT: extracts data from a database
  - UPDATE: updates data in a database
  - DELETE: deletes data from a database
  - INSERT INTO: inserts new data into a database
  - ALTER DATABASE: modifies a database
  - CREATE TABLE: creates a new table
  - ALTER TABLE: modifies a table
  - DROP TABLE: deletes a table
  - CREATE INDEX: creates an index (search key)
  - DROP INDEX: deletes an index
</details>

<details> 
  <summary>Tutorial 2: SQL SELECT Statement</summary>

  This one returns data from the Customers table 
  ```sql
  SELECT CustomerName, City FROM Customers;
  ```

  #### Syntax
  ```sql
  SELECT column1, column2, ...
  FROM table_name
  ```
</details>

<details>
  <summary>Tutorial 3: SQL SELECT DISTINCT Statement</summary>

  The SELECT DISTINCT statement is used to return only distinct(different) values. 
  Example: 
  ```sql
  SELECT DISTINCT Country FROM Customers;
  ```
  <details> 
    <summary>Result:</summary>
    
  | Record | Country     |
  |--------|-------------|
  | 1      | Argentina   |
  | 2      | Austria     |
  | 3      | Belgium     |
  | 4      | Brazil      |
  | 5      | Canada      |
  | 6      | Denmark     |
  | 7      | Finland     |
  | 8      | France      |
  | 9      | Germany     |
  | 10     | Ireland     |
  | 11     | Italy       |
  | 12     | Mexico      |
  | 13     | Norway      |
  | 14     | Poland      |
  | 15     | Portugal    |
  | 16     | Spain       |
  | 17     | Sweden      |
  | 18     | Switzerland |
  | 19     | UK          |
  | 20     | USA         |
  | 21     | Venezuela   |

  </details>
  
  #### Syntax 
  ```sql
  SELECT DISTINCT column1, column2,...
  FROM table_name; 
  ```
  Without the DISTINCT keyword, the SQL statement returns the "Country" value from all the records of the "Customers" table: 
  ```sql
  SELECT Country FROM Customers; 
  ```
  <details> 
  <summary>Result</summary>
    
| Record | Country     |
|--------|-------------|
| 1      | Germany     |
| 2      | Mexico      |
| 3      | Mexico      |
| 4      | UK          |
| 5      | Sweden      |
| 6      | Germany     |
| 7      | France      |
| 8      | Spain       |
| 9      | France      |
| 10     | Canada      |
| 11     | UK          |
| 12     | Argentina   |
| 13     | Mexico      |
| 14     | Switzerland |
| 15     | Brazil      |
| 16     | UK          |
| 17     | Germany     |
| 18     | France      |
| 19     | UK          |
| 20     | Austria     |
| 21     | Brazil      |
| 22     | Spain       |
| 23     | France      |
| 24     | Sweden      |
| 25     | Germany     |
| 26     | France      |
| 27     | Italy       |
| 28     | Portugal    |
| 29     | Spain       |
| 30     | Spain       |
| 31     | Brazil      |
| 32     | USA         |
| 33     | Venezuela   |
| 34     | Brazil      |
| 35     | Venezuela   |
| 36     | USA         |
| 37     | Ireland     |
| 38     | UK          |
| 39     | Germany     |
| 40     | France      |
| 41     | France      |
| 42     | Canada      |
| 43     | USA         |
| 44     | Germany     |
| 45     | USA         |
| 46     | Venezuela   |
| 47     | Venezuela   |
| 48     | USA         |
| 49     | Italy       |
| 50     | Belgium     |
| 51     | Canada      |
| 52     | Germany     |
| 53     | UK          |
| 54     | Argentina   |
| 55     | USA         |
| 56     | Germany     |
| 57     | France      |
| 58     | Mexico      |
| 59     | Austria     |
| 60     | Portugal    |
| 61     | Brazil      |
| 62     | Brazil      |
| 63     | Germany     |
| 64     | Argentina   |
| 65     | USA         |
| 66     | Italy       |
| 67     | Brazil      |
| 68     | Switzerland |
| 69     | Spain       |
| 70     | Norway      |
| 71     | USA         |
| 72     | UK          |
| 73     | Denmark     |
| 74     | France      |
| 75     | USA         |
| 76     | Belgium     |
| 77     | USA         |
| 78     | USA         |
| 79     | Germany     |
| 80     | Mexico      |
| 81     | Brazil      |
| 82     | USA         |
| 83     | Denmark     |
| 84     | France      |
| 85     | France      |
| 86     | Germany     |
| 87     | Finland     |
| 88     | Brazil      |
| 89     | USA         |
| 90     | Finland     |
| 91     | Poland      |

  </details>

```markdown
### Therefore, DISTINCT Keyword will make you produce all distinct countries + count(without repetition)
```

### To return number of distinct countries:

Use the **"COUNT"** keyword:
```sql
SELECT COUNT(DISTINCT Country) FROM Customers;
```

For MS Access: 
```
SELECT COUNT(*) AS DistinctCountries
FROM (SELECT DISTINCT Country FROM Customers); 
```

You'll learn this later on dw hahaha 

</details>

<details>
  <summary>Tutorial 4: SQL WHERE Clause</summary>

  The WHERE clause is used to filter records; 

  It is used to extract only those records that fulfill a specified condition:

  ```sql
  SELECT * FROM Customers
  WHERE Country = 'Mexico'; 
  ```

  <details>
    <summary>Result:</summary>
    
  |  CustomerID  |  CustomerName  |  ContactName  |  Address  |  City  |  PostalCode  |  Country  |
  |--------------|----------------|---------------|-----------|--------|--------------|-----------|
  | 2 | Ana Trujillo Emparedados y helados | Ana Trujillo | Avda. de la Constitución 2222 | México D.F. | 05021 | Mexico |
  | 3 | Antonio Moreno Taquería | Antonio Moreno | Mataderos 2312 | México D.F. | 05023 | Mexico  |
  | 13 | Centro comercial Moctezuma | Francisco Chang | Sierras de Granada 9993 | México D.F. | 05022 | Mexico |
  | 58 | Pericles Comidas clásicas | Guillermo Fernández | Calle Dr. Jorge Cash 321 | México D.F. | 05033 | Mexico |
  | 80 | Tortuga Restaurante | Miguel Angel Paolino | Avda. Azteca 123 | México D.F. | 05033 | Mexico |
    
  </details>

  #### Syntax
  ```sql
  SELECT column1, column2, ...
  FROM table_name
  WHERE condition; 
  ```

  #### Text Fields vs Numeric Fields 
  SQL requires single quotes around text values

  However, numeric fields should not be enclosed in quotes: 

  Example: 
  ```sql
  SELECT * FROM Customers
  WHERE CustomerID=1; 
  ```

  Result: 
  |  CustomerID  |  CustomerName  |  ContactName  |  Address  |  City  |  PostalCode  |  Country  |
  |--------------|----------------|---------------|-----------|--------|--------------|-----------|
  | 1 | Alfreds Futterkiste | Maria Anders | Obere Str. 57 | Berlin | 122209 | Germani |

  #### Operators in the WHERE Clause 

  You can use other operators aside from the = operator to filter the search. 
  
  Example: 
  ```sql
  SELECT * FROM Customers;
  WHERE CustomerID > 80; 
  ```

  <details>
    <summary>Result:</summary>
    
  |  CustomerID  |  CustomerName  |  ContactName  |  Address  |  City  |  PostalCode  |  Country  |
  |--------------|----------------|---------------|-----------|--------|--------------|-----------|
  | 81 | Tradição Hipermercados | Anabela Domingues | Av. Inês de Castro, 414 | São Paulo | 05634-030 | Brazil |
  | 82 | Trail's Head Gourmet Provisioners | Helvetius Nagy | 722 DaVinci Blvd. | Kirkland | 98034 | USA |
  | 83 | Vaffeljernet | Palle Ibsen | Smagsløget 45 | Århus | 8200 | Denmark |
  | 84 | Victuailles en stock | Mary Saveley | 2, rue du Commerce | Lyon | 69004 | France |
  | 85 | Vins et alcools Chevalier | Paul Henriot | 59 rue de l'Abbaye | Reims | 51100 | France |
  | 86 | Die Wandernde Kuh | Rita Müller | Adenauerallee 900 | Stuttgart | 70563 | Germany |
  | 87 | Wartian Herkku | Pirkko Koskitalo | Torikatu 38 | Oulu | 90110 | Finland |
  | 88 | Wellington Importadora | Paula Parente | Rua do Mercado, 12 | Resende | 08737-363 | Brazil |
  | 89 | White Clover Markets | Karl Jablonski | 305 - 14th Ave. S. Suite 3B | Seattle | 98128 | USA |
  | 90 | Wilman Kala | Matti Karttunen | Keskuskatu 45 | Helsinki | 21240 | Finland |
  | 91 | Wolski | Zbyszek | ul. Filtrowa 68 | Walla | 01-012 | Poland |

  </details>

  | Operator | Desctiption | 
  |----------|-------------|
  | =        | Equal       |
  | >        | Greater than| 
  | <        | Less than   |
  | >=       | Greater than or equal | 
  | <=       | Less than or equal | 
  | <> or != | Not equal |
  | BETWEEN  | Between a certain range | 
  | LIKE     | Search for a pattern | 
  | IN       | To specify multiple possible values for a column | 

  #### Syntax 
  ##### Equal
  ```sql
  SELECT * FROM Products;
  WHERE Price = 18;
  ```
  <details>
    <summary>Result: </summary>
  
  | ProductID | ProductName        | SupplierID | CategoryID | Unit               | Price |
  |-----------|------------------|-----------|------------|------------------|-------|
  | 1         | Chais             | 1         | 1          | 10 boxes x 20 bags | 18    |
  | 35        | Steeleye Stout    | 16        | 1          | 24 - 12 oz bottles | 18    |
  | 39        | Chartreuse verte  | 18        | 1          | 750 cc per bottle | 18    |
  | 76        | Lakkalikööri      | 23        | 1          | 500 ml           | 18    |
  
  </details>
  
  ##### Greater than
  
  ```sql
  SELECT * FROM Products;
  WHERE Price > 18; 
  ```
  <details>
    <summary>Result: </summary>

| ProductID | ProductName                     | SupplierID | CategoryID | Unit                  | Price  |
|-----------|---------------------------------|-----------|------------|----------------------|--------|
| 8         | Northwoods Cranberry Sauce      | 3         | 2          | 12 - 12 oz jars       | 40     |
| 9         | Mishi Kobe Niku                 | 4         | 6          | 18 - 500 g pkgs.      | 97     |
| 10        | Ikura                           | 4         | 8          | 12 - 200 ml jars      | 31     |
| 12        | Queso Manchego La Pastora       | 5         | 4          | 10 - 500 g pkgs.      | 38     |
| 17        | Alice Mutton                    | 7         | 6          | 20 - 1 kg tins        | 39     |
| 18        | Carnarvon Tigers                | 7         | 8          | 16 kg pkg.            | 62.5   |
| 20        | Sir Rodney's Marmalade          | 8         | 3          | 30 gift boxes         | 81     |
| 26        | Gumbär Gummibärchen             | 11        | 3          | 100 - 250 g bags      | 31.23  |
| 27        | Schoggi Schokolade              | 11        | 3          | 100 - 100 g pieces    | 43.9   |
| 28        | Rössle Sauerkraut               | 12        | 7          | 25 - 825 g cans       | 45.6   |
| 29        | Thüringer Rostbratwurst         | 12        | 6          | 50 bags x 30 sausgs.  | 123.79 |
| 32        | Mascarpone Fabioli              | 14        | 4          | 24 - 200 g pkgs.      | 32     |
| 38        | Côte de Blaye                   | 18        | 1          | 12 - 75 cl bottles    | 263.5  |
| 43        | Ipoh Coffee                     | 20        | 1          | 16 - 500 g tins       | 46     |
| 51        | Manjimup Dried Apples           | 24        | 7          | 50 - 300 g pkgs.      | 53     |
| 53        | Perth Pasties                   | 24        | 6          | 48 pieces             | 32.8   |
| 56        | Gnocchi di nonna Alice          | 26        | 5          | 24 - 250 g pkgs.      | 38     |
| 59        | Raclette Courdavault            | 28        | 4          | 5 kg pkg.             | 55     |
| 60        | Camembert Pierrot               | 28        | 4          | 15 - 300 g rounds     | 34     |
| 62        | Tarte au sucre                  | 29        | 3          | 48 pies               | 49.3   |
| 63        | Vegie-spread                    | 7         | 2          | 15 - 625 g jars       | 43.9   |
| 64        | Wimmers gute Semmelknödel       | 12        | 5          | 20 bags x 4 pieces    | 33.25  |
| 69        | Gudbrandsdalsost                | 15        | 4          | 10 kg pkg.            | 36     |
| 72        | Mozzarella di Giovanni          | 14        | 4          | 24 - 200 g pkgs.      | 34.8   |
    
  </details>
  
  ##### Less than

  ```sql
  SELECT * FROM Products;
  WHERE Price < 18; 
  ```
  <details>
    <summary>Result: </summary>

  | ProductID | ProductName                        | SupplierID | CategoryID | Unit                         | Price  |
|-----------|----------------------------------|-----------|------------|------------------------------|--------|
| 1         | Chais                             | 1         | 1          | 10 boxes x 20 bags           | 18     |
| 2         | Chang                             | 1         | 1          | 24 - 12 oz bottles           | 19     |
| 3         | Aniseed Syrup                     | 1         | 2          | 12 - 550 ml bottles          | 10     |
| 4         | Chef Anton's Cajun Seasoning      | 2         | 2          | 48 - 6 oz jars               | 22     |
| 5         | Chef Anton's Gumbo Mix            | 2         | 2          | 36 boxes                     | 21.35  |
| 6         | Grandma's Boysenberry Spread      | 3         | 2          | 12 - 8 oz jars               | 25     |
| 11        | Queso Cabrales                    | 5         | 4          | 1 kg pkg.                    | 21     |
| 13        | Konbu                             | 6         | 8          | 2 kg box                     | 6      |
| 14        | Tofu                              | 6         | 7          | 40 - 100 g pkgs.             | 23.25  |
| 15        | Genen Shouyu                       | 6         | 2          | 24 - 250 ml bottles          | 15.5   |
| 16        | Pavlova                            | 7         | 3          | 32 - 500 g boxes             | 17.45  |
| 19        | Teatime Chocolate Biscuits         | 8         | 3          | 10 boxes x 12 pieces         | 9.2    |
| 21        | Sir Rodney's Scones                | 8         | 3          | 24 pkgs. x 4 pieces          | 10     |
| 22        | Gustaf's Knäckebröd                | 9         | 5          | 24 - 500 g pkgs.             | 21     |
| 23        | Tunnbröd                           | 9         | 5          | 12 - 250 g pkgs.             | 9      |
| 24        | Guaraná Fantástica                 | 10        | 1          | 12 - 355 ml cans             | 4.5    |
| 25        | NuNuCa Nuß-Nougat-Creme            | 11        | 3          | 20 - 450 g glasses           | 14     |
| 30        | Nord-Ost Matjeshering              | 13        | 8          | 10 - 200 g glasses           | 25.89  |
| 31        | Gorgonzola Telino                  | 14        | 4          | 12 - 100 g pkgs              | 12.5   |
| 33        | Geitost                            | 15        | 4          | 500 g                        | 2.5    |
| 34        | Sasquatch Ale                      | 16        | 1          | 24 - 12 oz bottles           | 14     |
| 35        | Steeleye Stout                     | 16        | 1          | 24 - 12 oz bottles           | 18     |
| 36        | Inlagd Sill                        | 17        | 8          | 24 - 250 g jars              | 19     |
| 37        | Gravad lax                         | 17        | 8          | 12 - 500 g pkgs              | 26     |
| 39        | Chartreuse verte                   | 18        | 1          | 750 cc per bottle             | 18     |
| 40        | Boston Crab Meat                   | 19        | 8          | 24 - 4 oz tins               | 18.4   |
| 41        | Jack's New England Clam Chowder    | 19        | 8          | 12 - 12 oz cans              | 9.65   |
| 42        | Singaporean Hokkien Fried Mee      | 20        | 5          | 32 - 1 kg pkgs.              | 14     |
| 44        | Gula Malacca                        | 20        | 2          | 20 - 2 kg bags               | 19.45  |
| 45        | Røgede sild                         | 21        | 8          | 1k pkg.                       | 9.5    |
| 46        | Spegesild                           | 21        | 8          | 4 - 450 g glasses            | 12     |
| 47        | Zaanse koeken                       | 22        | 3          | 10 - 4 oz boxes              | 9.5    |
| 48        | Chocolade                           | 22        | 3          | 10 pkgs.                     | 12.75  |
| 49        | Maxilaku                            | 23        | 3          | 24 - 50 g pkgs.              | 20     |
| 50        | Valkoinen suklaa                    | 23        | 3          | 12 - 100 g bars              | 16.25  |
| 52        | Filo Mix                            | 24        | 5          | 16 - 2 kg boxes              | 7      |
| 54        | Tourtière                           | 25        | 6          | 16 pies                       | 7.45   |
| 55        | Pâté chinois                        | 25        | 6          | 24 boxes x 2 pies            | 24     |
| 57        | Ravioli Angelo                      | 26        | 5          | 24 - 250 g pkgs.             | 19.5   |
| 58        | Escargots de Bourgogne              | 27        | 8          | 24 pieces                     | 13.25  |
| 61        | Sirop d'érable                      | 29        | 2          | 24 - 500 ml bottles          | 28.5   |
| 65        | Louisiana Fiery Hot Pepper Sauce    | 2         | 2          | 32 - 8 oz bottles            | 21.05  |
| 66        | Louisiana Hot Spiced Okra           | 2         | 2          | 24 - 8 oz jars               | 17     |
| 67        | Laughing Lumberjack Lager           | 16        | 1          | 24 - 12 oz bottles           | 14     |
| 68        | Scottish Longbreads                 | 8         | 3          | 10 boxes x 8 pieces          | 12.5   |
| 70        | Outback Lager                       | 7         | 1          | 24 - 355 ml bottles          | 15     |
| 71        | Fløtemysost                         | 15        | 4          | 10 - 500 g pkgs.             | 21.5   |
| 73        | Röd Kaviar                          | 17        | 8          | 24 - 150 g jars              | 15     |
| 74        | Longlife Tofu                        | 4         | 7          | 5 kg pkg.                    | 10     |
| 75        | Rhönbräu Klosterbier                 | 12        | 1          | 24 - 0.5 l bottles           | 7.75   |
| 76        | Lakkalikööri                         | 23        | 1          | 500 ml                        | 18     |
| 77        | Original Frankfurter grüne Soße      | 12        | 2          |  12 boxes                            |   13     |

    
  </details>
  
  ##### greater than or equal
  ```sql
  SELECT * FROM Products;
  WHERE Price >= 30; 
  ```

  <details>
    <summary>Result: </summary>

| ProductID | ProductName                    | SupplierID | CategoryID | Unit                   | Price  |
|-----------|--------------------------------|-----------|------------|-----------------------|--------|
| 7         | Uncle Bob's Organic Dried Pears| 3         | 7          | 12 - 1 lb pkgs.       | 30     |
| 8         | Northwoods Cranberry Sauce      | 3         | 2          | 12 - 12 oz jars       | 40     |
| 9         | Mishi Kobe Niku                 | 4         | 6          | 18 - 500 g pkgs.      | 97     |
| 10        | Ikura                           | 4         | 8          | 12 - 200 ml jars      | 31     |
| 12        | Queso Manchego La Pastora       | 5         | 4          | 10 - 500 g pkgs.      | 38     |
| 17        | Alice Mutton                    | 7         | 6          | 20 - 1 kg tins        | 39     |
| 18        | Carnarvon Tigers                | 7         | 8          | 16 kg pkg.            | 62.5   |
| 20        | Sir Rodney's Marmalade           | 8         | 3          | 30 gift boxes         | 81     |
| 26        | Gumbär Gummibärchen             | 11        | 3          | 100 - 250 g bags      | 31.23  |
| 27        | Schoggi Schokolade              | 11        | 3          | 100 - 100 g pieces    | 43.9   |
| 28        | Rössle Sauerkraut               | 12        | 7          | 25 - 825 g cans       | 45.6   |
| 29        | Thüringer Rostbratwurst         | 12        | 6          | 50 bags x 30 sausgs.  | 123.79 |
| 32        | Mascarpone Fabioli              | 14        | 4          | 24 - 200 g pkgs       | 32     |
| 38        | Côte de Blaye                   | 18        | 1          | 12 - 75 cl bottles    | 263.5  |
| 43        | Ipoh Coffee                     | 20        | 1          | 16 - 500 g tins       | 46     |
| 51        | Manjimup Dried Apples           | 24        | 7          | 50 - 300 g pkgs       | 53     |
| 53        | Perth Pasties                   | 24        | 6          | 48 pieces             | 32.8   |
| 56        | Gnocchi di nonna Alice          | 26        | 5          | 24 - 250 g pkgs       | 38     |
| 59        | Raclette Courdavault            | 28        | 4          | 5 kg pkg.             | 55     |
| 60        | Camembert Pierrot               | 28        | 4          | 15 - 300 g rounds     | 34     |
| 62        | Tarte au sucre                  | 29        | 3          | 48 pies               | 49.3   |
| 63        | Vegie-spread                    | 7         | 2          | 15 - 625 g jars       | 43.9   |
| 64        | Wimmers gute Semmelknödel       | 12        | 5          | 20 bags x 4 pieces    | 33.25  |
| 69        | Gudbrandsdalsost                | 15        | 4          | 10 kg pkg.            | 36     |
| 72        | Mozzarella di Giovanni          | 14        | 4          |                       |        |
    
  </details>
  
  ##### Less than or equal
  ```sql
  SELECT * FROM Products;
  WHERE Price <= 30; 
  ```

  <details>
    <summary>Result: </summary>

| ProductID | ProductName                        | SupplierID | CategoryID | Unit                     | Price  |
|-----------|-----------------------------------|-----------|------------|--------------------------|--------|
| 1         | Chais                              | 1         | 1          | 10 boxes x 20 bags       | 18     |
| 2         | Chang                              | 1         | 1          | 24 - 12 oz bottles       | 19     |
| 3         | Aniseed Syrup                      | 1         | 2          | 12 - 550 ml bottles      | 10     |
| 4         | Chef Anton's Cajun Seasoning       | 2         | 2          | 48 - 6 oz jars           | 22     |
| 5         | Chef Anton's Gumbo Mix             | 2         | 2          | 36 boxes                 | 21.35  |
| 6         | Grandma's Boysenberry Spread       | 3         | 2          | 12 - 8 oz jars           | 25     |
| 7         | Uncle Bob's Organic Dried Pears    | 3         | 7          | 12 - 1 lb pkgs.          | 30     |
| 11        | Queso Cabrales                     | 5         | 4          | 1 kg pkg.                | 21     |
| 13        | Konbu                              | 6         | 8          | 2 kg box                 | 6      |
| 14        | Tofu                               | 6         | 7          | 40 - 100 g pkgs.         | 23.25  |
| 15        | Genen Shouyu                        | 6         | 2          | 24 - 250 ml bottles      | 15.5   |
| 16        | Pavlova                             | 7         | 3          | 32 - 500 g boxes         | 17.45  |
| 19        | Teatime Chocolate Biscuits          | 8         | 3          | 10 boxes x 12 pieces     | 9.2    |
| 21        | Sir Rodney's Scones                 | 8         | 3          | 24 pkgs. x 4 pieces      | 10     |
| 22        | Gustaf's Knäckebröd                 | 9         | 5          | 24 - 500 g pkgs          | 21     |
| 23        | Tunnbröd                            | 9         | 5          | 12 - 250 g pkgs          | 9      |
| 24        | Guaraná Fantástica                  | 10        | 1          | 12 - 355 ml cans         | 4.5    |
| 25        | NuNuCa Nuß-Nougat-Creme             | 11        | 3          | 20 - 450 g glasses       | 14     |
| 30        | Nord-Ost Matjeshering               | 13        | 8          | 10 - 200 g glasses       | 25.89  |
| 31        | Gorgonzola Telino                   | 14        | 4          | 12 - 100 g pkgs          | 12.5   |
| 33        | Geitost                             | 15        | 4          | 500 g                    | 2.5    |
| 34        | Sasquatch Ale                       | 16        | 1          | 24 - 12 oz bottles       | 14     |
| 35        | Steeleye Stout                       | 16        | 1          | 24 - 12 oz bottles       | 18     |
| 36        | Inlagd Sill                          | 17        | 8          | 24 - 250 g jars          | 19     |
| 37        | Gravad lax                           | 17        | 8          | 12 - 500 g pkgs          | 26     |
| 39        | Chartreuse verte                     | 18        | 1          | 750 cc per bottle        | 18     |
| 40        | Boston Crab Meat                     | 19        | 8          | 24 - 4 oz tins           | 18.4   |
| 41        | Jack's New England Clam Chowder      | 19        | 8          | 12 - 12 oz cans          | 9.65   |
| 42        | Singaporean Hokkien Fried Mee        | 20        | 5          | 32 - 1 kg pkgs           | 14     |
| 44        | Gula Malacca                         | 20        | 2          | 20 - 2 kg bags           | 19.45  |
| 45        | Røgede sild                          | 21        | 8          | 1k pkg.                  | 9.5    |
| 46        | Spegesild                            | 21        | 8          | 4 - 450 g glasses        | 12     |
| 47        | Zaanse koeken                        | 22        | 3          | 10 - 4 oz boxes          | 9.5    |
| 48        | Chocolade                            | 22        | 3          | 10 pkgs                  | 12.75  |
| 49        | Maxilaku                             | 23        | 3          | 24 - 50 g pkgs           | 20     |
| 50        | Valkoinen suklaa                     | 23        | 3          | 12 - 100 g bars          | 16.25  |
| 52        | Filo Mix                             | 24        | 5          | 16 - 2 kg boxes          | 7      |
| 54        | Tourtière                             | 25        | 6          | 16 pies                  | 7.45   |
| 55        | Pâté chinois                          | 25        | 6          | 24 boxes x 2 pies        | 24     |
| 57        | Ravioli Angelo                        | 26        | 5          | 24 - 250 g pkgs          | 19.5   |
| 58        | Escargots de Bourgogne                 | 27        | 8          | 24 pieces                | 13.25  |
| 61        | Sirop d'érable                         | 29        | 2          | 24 - 500 ml bottles      | 28.5   |
| 65        | Louisiana Fiery Hot Pepper Sauce       | 2         | 2          | 32 - 8 oz bottles        | 21.05  |
| 66        | Louisiana Hot Spiced Okra              | 2         | 2          | 24 - 8 oz jars           | 17     |
| 67        | Laughing Lumberjack Lager              | 16        | 1          | 24 - 12 oz bottles       | 14     |
| 68        | Scottish Longbreads                    | 8         | 3          | 10 boxes x 8 pieces      | 12.5   |
| 70        | Outback Lager                          | 7         | 1          | 24 - 355 ml bottles      | 15     |
| 71        | Fløtemysost                            | 15        | 4          | 10 - 500 g pkgs          | 21.5   |
| 73        | Röd Kaviar                             | 17        | 8          | 24 - 150 g jars          | 15     |
| 74        | Longlife Tofu                          | 4         | 7          | 5 kg pkg.                | 10     |
| 75        | Rhönbräu Klosterbier                   | 12        | 1          | 24 - 0.5 l bottles       | 7.75   |
| 76        | Lakkalikööri                           | 23        | 1          | 500 ml                   | 18     |
| 77        | Original Frankfurter grüne Soße       | 12        | 2          | 12 boxes                 | 13     |
    
  </details>
  
  ##### Not equal
  ```sql
  SELECT * FROM Products;
  WHERE Price <> 18; 
  ```

  <details>
    <summary>Result: </summary>

| ProductID | ProductName                        | SupplierID | CategoryID | Unit                     | Price  |
|-----------|-----------------------------------|-----------|------------|--------------------------|--------|
| 2         | Chang                              | 1         | 1          | 24 - 12 oz bottles       | 19     |
| 3         | Aniseed Syrup                      | 1         | 2          | 12 - 550 ml bottles      | 10     |
| 4         | Chef Anton's Cajun Seasoning       | 2         | 2          | 48 - 6 oz jars           | 22     |
| 5         | Chef Anton's Gumbo Mix             | 2         | 2          | 36 boxes                 | 21.35  |
| 6         | Grandma's Boysenberry Spread       | 3         | 2          | 12 - 8 oz jars           | 25     |
| 7         | Uncle Bob's Organic Dried Pears    | 3         | 7          | 12 - 1 lb pkgs.          | 30     |
| 8         | Northwoods Cranberry Sauce         | 3         | 2          | 12 - 12 oz jars          | 40     |
| 9         | Mishi Kobe Niku                    | 4         | 6          | 18 - 500 g pkgs.         | 97     |
| 10        | Ikura                              | 4         | 8          | 12 - 200 ml jars         | 31     |
| 11        | Queso Cabrales                     | 5         | 4          | 1 kg pkg.                | 21     |
| 12        | Queso Manchego La Pastora          | 5         | 4          | 10 - 500 g pkgs          | 38     |
| 13        | Konbu                              | 6         | 8          | 2 kg box                 | 6      |
| 14        | Tofu                               | 6         | 7          | 40 - 100 g pkgs          | 23.25  |
| 15        | Genen Shouyu                        | 6         | 2          | 24 - 250 ml bottles      | 15.5   |
| 16        | Pavlova                             | 7         | 3          | 32 - 500 g boxes         | 17.45  |
| 17        | Alice Mutton                        | 7         | 6          | 20 - 1 kg tins           | 39     |
| 18        | Carnarvon Tigers                     | 7         | 8          | 16 kg pkg.               | 62.5   |
| 19        | Teatime Chocolate Biscuits          | 8         | 3          | 10 boxes x 12 pieces     | 9.2    |
| 20        | Sir Rodney's Marmalade              | 8         | 3          | 30 gift boxes            | 81     |
| 21        | Sir Rodney's Scones                 | 8         | 3          | 24 pkgs. x 4 pieces      | 10     |
| 22        | Gustaf's Knäckebröd                 | 9         | 5          | 24 - 500 g pkgs          | 21     |
| 23        | Tunnbröd                            | 9         | 5          | 12 - 250 g pkgs          | 9      |
| 24        | Guaraná Fantástica                  | 10        | 1          | 12 - 355 ml cans         | 4.5    |
| 25        | NuNuCa Nuß-Nougat-Creme             | 11        | 3          | 20 - 450 g glasses       | 14     |
| 26        | Gumbär Gummibärchen                  | 11        | 3          | 100 - 250 g bags         | 31.23  |
| 27        | Schoggi Schokolade                  | 11        | 3          | 100 - 100 g pieces       | 43.9   |
| 28        | Rössle Sauerkraut                   | 12        | 7          | 25 - 825 g cans          | 45.6   |
| 29        | Thüringer Rostbratwurst             | 12        | 6          | 50 bags x 30 sausgs.     | 123.79 |
| 30        | Nord-Ost Matjeshering               | 13        | 8          | 10 - 200 g glasses       | 25.89  |
| 31        | Gorgonzola Telino                   | 14        | 4          | 12 - 100 g pkgs          | 12.5   |
| 32        | Mascarpone Fabioli                  | 14        | 4          | 24 - 200 g pkgs          | 32     |
| 33        | Geitost                             | 15        | 4          | 500 g                    | 2.5    |
| 34        | Sasquatch Ale                       | 16        | 1          | 24 - 12 oz bottles       | 14     |
| 36        | Inlagd Sill                          | 17        | 8          | 24 - 250 g jars          | 19     |
| 37        | Gravad lax                           | 17        | 8          | 12 - 500 g pkgs          | 26     |
| 38        | Côte de Blaye                        | 18        | 1          | 12 - 75 cl bottles       | 263.5  |
| 40        | Boston Crab Meat                     | 19        | 8          | 24 - 4 oz tins           | 18.4   |
| 41        | Jack's New England Clam Chowder      | 19        | 8          | 12 - 12 oz cans          | 9.65   |
| 42        | Singaporean Hokkien Fried Mee        | 20        | 5          | 32 - 1 kg pkgs           | 14     |
| 43        | Ipoh Coffee                          | 20        | 1          | 16 - 500 g tins          | 46     |
| 44        | Gula Malacca                         | 20        | 2          | 20 - 2 kg bags           | 19.45  |
| 45        | Røgede sild                          | 21        | 8          | 1k pkg.                  | 9.5    |
| 46        | Spegesild                            | 21        | 8          | 4 - 450 g glasses        | 12     |
| 47        | Zaanse koeken                        | 22        | 3          | 10 - 4 oz boxes          | 9.5    |
| 48        | Chocolade                            | 22        | 3          | 10 pkgs                  | 12.75  |
| 49        | Maxilaku                             | 23        | 3          | 24 - 50 g pkgs           | 20     |
| 50        | Valkoinen suklaa                     | 23        | 3          | 12 - 100 g bars          | 16.25  |
| 51        | Manjimup Dried Apples                | 24        | 7          | 50 - 300 g pkgs          | 53     |
| 52        | Filo Mix                             | 24        | 5          | 16 - 2 kg boxes          | 7      |
| 53        | Perth Pasties                        | 24        | 6          | 48 pieces                | 32.8   |
| 54        | Tourtière                             | 25        | 6          | 16 pies                  | 7.45   |
| 55        | Pâté chinois                          | 25        | 6          | 24 boxes x 2 pies        | 24     |
| 56        | Gnocchi di nonna Alice               | 26        | 5          | 24 - 250 g pkgs          | 38     |
| 57        | Ravioli Angelo                        | 26        | 5          | 24 - 250 g pkgs          | 19.5   |
| 58        | Escargots de Bourgogne                | 27        | 8          | 24 pieces                | 13.25  |
| 59        | Raclette Courdavault                  | 28        | 4          | 5 kg pkg.                | 55     |
| 60        | Camembert Pierrot                     | 28        | 4          | 15 - 300 g rounds        | 34     |
| 61        | Sirop d'érable                        | 29        | 2          | 24 - 500 ml bottles      | 28.5   |
| 62        | Tarte au sucre                        | 29        | 3          | 48 pies                  | 49.3   |
| 63        | Vegie-spread                          | 7         | 2          | 15 - 625 g jars          | 43.9   |
| 64        | Wimmers gute Semmelknödel             | 12        | 5          | 20 bags x 4 pieces       | 33.25  |
| 65        | Louisiana Fiery Hot Pepper Sauce      | 2         | 2          | 32 - 8 oz bottles        | 21.05  |
| 66        | Louisiana Hot Spiced Okra             | 2         | 2          | 24 - 8 oz jars           | 17     |
| 67        | Laughing Lumberjack Lager             | 16        | 1          | 24 - 12 oz bottles       | 14     |
| 68        | Scottish Longbreads                   | 8         | 3          | 10 boxes x 8 pieces      | 12.5   |
| 69        | Gudbrandsdalsost                       | 15        | 4          | 10 kg pkg.               | 36     |
| 70        | Outback Lager                         | 7         | 1          | 24 - 355 ml bottles      | 15     |
| 71        | Fløtemysost                           | 15        | 4          | 10 - 500 g pkgs          | 21.5   |
| 72        | Mozzarella di Giovanni                 | 14        | 4          | 24 - 200 g pkgs          | 34.8   |
| 73        | Röd Kaviar                             | 17        | 8          | 24 - 150 g jars          | 15     |
| 74        | Longlife Tofu                          | 4         | 7          | 5 kg pkg.                | 10     |
| 75        | Rhönbräu Klosterbier                   | 12        | 1          | 24 - 0.5 l bottles       | 7.75   |
| 77        | Original Frankfurter grüne Soße       | 12        | 2          | 12 boxes                 | 13     |
    
  </details>
  
  ##### BETWEEN 
  ```sql
  SELECT * FROM Products;
  WHERE Price BETWEEN 18 AND 20; 
  ```

  <details>
    <summary>Result: </summary>

| ProductID | ProductName           | SupplierID | CategoryID | Unit              | Price |
|-----------|----------------------|------------|------------|-----------------|-------|
| 51        | Manjimup Dried Apples | 24         | 7          | 50 - 300 g pkgs | 53    |
| 59        | Raclette Courdavault  | 28         | 4          | 5 kg pkg.       | 55    |
    
  </details>
  
  ##### LIKE
  ```sql
  SELECT * FROM Products;
  WHERE City LIKE 's%'; 
  ```
  <details>
    <summary>Result: </summary>

| CustomerID | CustomerName            | ContactName         | Address                               | City          | PostalCode  | Country   |
|------------|------------------------|------------------|---------------------------------------|---------------|------------|----------|
| 7          | Blondel père et fils    | Frédérique Citeaux | 24, place Kléber                      | Strasbourg    | 67000      | France   |
| 15         | Comércio Mineiro        | Pedro Afonso       | Av. dos Lusíadas, 23                  | São Paulo     | 05432-043  | Brazil   |
| 21         | Familia Arquibaldo      | Aria Cruz          | Rua Orós, 92                           | São Paulo     | 05442-030  | Brazil   |
| 30         | Godos Cocina Típica     | José Pedro Freyre  | C/ Romero, 33                          | Sevilla       | 41101      | Spain    |
| 35         | HILARIÓN-Abastos        | Carlos Hernández   | Carrera 22 con Ave. Carlos Soublette #8-35 | San Cristóbal | 5022       | Venezuela |
| 45         | Let's Stop N Shop       | Jaime Yorres       | 87 Polk St. Suite 5                    | San Francisco | 94117      | USA      |
| 59         | Piccolo und mehr        | Georg Pipps        | Geislweg 14                            | Salzburg      | 5020       | Austria  |
| 62         | Queen Cozinha           | Lúcia Carvalho     | Alameda dos Canàrios, 891             | São Paulo     | 05487-020  | Brazil   |
| 70         | Santé Gourmet           | Jonas Bergulfsen   | Erling Skakkes gate 78                 | Stavern       | 4110       | Norway   |
| 81         | Tradição Hipermercados  | Anabela Domingues  | Av. Inês de Castro, 414                | São Paulo     | 05634-030  | Brazil   |
| 86         | Die Wandernde Kuh       | Rita Müller        | Adenauerallee 900                       | Stuttgart     | 70563      | Germany  |
| 89         | White Clover Markets    | Karl Jablonski     | 305 - 14th Ave. S. Suite 3B           | Seattle       | 98128      | USA      |
    
  </details>
  
  ##### IN
  ```sql
  SELECT * FROM Products;
  WHERE Price IN ('Paris', 'London'); 
  ```
  <details>
    <summary>Result: </summary>

| CustomerID | CustomerName             | ContactName       | Address                           | City   | PostalCode | Country |
|------------|-------------------------|-----------------|----------------------------------|--------|------------|---------|
| 4          | Around the Horn         | Thomas Hardy     | 120 Hanover Sq.                  | London | WA1 1DP    | UK      |
| 11         | B's Beverages           | Victoria Ashworth| Fauntleroy Circus                | London | EC2 5NT    | UK      |
| 16         | Consolidated Holdings   | Elizabeth Brown  | Berkeley Gardens 12 Brewery      | London | WX1 6LT    | UK      |
| 19         | Eastern Connection      | Ann Devon        | 35 King George                   | London | WX3 6FW    | UK      |
| 53         | North/South             | Simon Crowther   | South House 300 Queensbridge     | London | SW7 1RZ    | UK      |
| 57         | Paris spécialités       | Marie Bertrand   | 265, boulevard Charonne          | Paris  | 75012      | France  |
| 72         | Seven Seas Imports      | Hari Kumar       | 90 Wadhurst Rd.                  | London | OX15 4NB   | UK      |
| 74         | Spécialités du monde    | Dominique Perrier| 25, rue Lauriston                | Paris  | 75016      | France  |

  </details>
  
</details>


  
  
