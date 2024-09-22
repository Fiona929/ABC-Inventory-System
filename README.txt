ABC Inventory System
====================

Login:
------
(For Manager)
Clerk ID: 1
Password: 123
------------------
(For Simple Clerk)
Clerk ID: 2
Password: 456

Description:
------------
The ABC Inventory System is a Java-based application designed to manage inventory for a grocery store. It provides functionality for managing products, stock, suppliers, and inventory clerks.

Features:
---------
1. Product Management:
   - Add new products (Food and Beverages)
   - Modify product prices
   - Delete products
   - Search for products
   - Display all products

2. Stock Management:
   - Add stock
   - Deduct stock
   - View current stock levels

3. Supplier Management:
   - Add new suppliers
   - View supplier details

4. Inventory Clerk Management:
   - Add new inventory clerks
   - Modify clerk details
   - View clerk information

5. User Authentication:
   - Login system for inventory clerks
   - Different access levels for managers and regular clerks

Installation:
-------------
1. Ensure you have Java Development Kit (JDK) installed on your system.
2. Set up a MySQL database named 'groceries' on your local machine.
3. Update the database connection details in the SQLConnection.java file if necessary.
4. Compile all Java files in the project.
5. Run the Main.java file to start the application.

Usage:
------
1. Launch the application by running Main.java.
2. Log in using your clerk ID and password.
3. Navigate through the menu options to perform various operations.

File Structure:
---------------
- Main.java: Contains the main menu and program flow
- Product.java: Abstract class for products
- Food.java: Subclass of Product for food items
- Beverages.java: Subclass of Product for beverage items
- Stock.java: Handles stock-related operations
- Supplier.java: Manages supplier information
- InventoryClerk.java: Manages inventory clerk information
- Address.java: Handles address information
- Name.java: Handles name information
- SQLConnection.java: Manages database connections

Dependencies:
-------------
- MySQL Connector/J: JDBC driver for MySQL database connectivity

Notes:
------
- Ensure that the MySQL server is running before launching the application.
- Only managers have access to the Inventory Clerk management menu.

For any issues or suggestions, please contact the development team.
