import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

public class Main {
    private static InventoryClerk currentClerk;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        displayLogo();  // Display the logo once at the start of the program
        System.out.println("Welcome to ABC Inventory System");

        while (currentClerk == null) {
            login(scanner);
        }
        
        System.out.println("Login successful. Welcome, " + currentClerk.getName());
        showMainMenu(scanner);
        scanner.close();
    }

    private static void displayLogo() {
        System.out.println("    _    ____   ____   _                      _                   ");
        System.out.println("   / \\  | __ ) / ___| (_)_ ____   _____ _ __ | |_ ___  _ __ _   _ ");
        System.out.println("  / _ \\ |  _ \\| |     | | '_ \\ \\ / / _ \\ '_ \\| __/ _ \\| '__| | | |");
        System.out.println(" / ___ \\| |_) | |___  | | | | \\ V /  __/ | | | || (_) | |  | |_| |");
        System.out.println("/_/   \\_\\____/ \\____| |_|_| |_|\\_/ \\___|_| |_|\\__\\___/|_|   \\__, |");
        System.out.println("                                                            |___/ ");
        System.out.println("                  ____            _                              ");
        System.out.println("                 / ___| _   _ ___| |_ ___ _ __ ___               ");
        System.out.println("                 \\___ \\| | | / __| __/ _ \\ '_ ` _ \\              ");
        System.out.println("                  ___) | |_| \\__ \\ ||  __/ | | | | |             ");
        System.out.println("                 |____/ \\__, |___/\\__\\___|_| |_| |_|             ");
        System.out.println("                         |___/                                    ");
        System.out.println();
    }

    private static void login(Scanner scanner) {
        System.out.print("Enter clerk ID: ");
        int clerkID = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        currentClerk = InventoryClerk.login(clerkID, password);
        if (currentClerk == null) {
            System.out.println("Login failed. Invalid clerk ID or password. Please try again.");
        }
    }

    //Get valid int input
    private static int getValidIntInput(Scanner scanner, int min, int max) {
        int choice;

        do {
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Consume the invalid input
            }
            choice = scanner.nextInt();
            if (choice < min || choice > max) {
                System.out.println("Invalid choice. Please enter a number between " + min + " and " + max + ".");
            }
        } while (choice < min || choice > max);
        scanner.nextLine(); // Consume the newline
        return choice;
    }

    private static void showMainMenu(Scanner scanner) {
        while (true) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Product Menu");
            System.out.println("2. Stock Menu");
            System.out.println("3. Supplier Menu");
            if (currentClerk.isManager()) {
                System.out.println("4. Inventory Clerk Menu");
            }
            System.out.println("5. Log Out");
            System.out.print("Enter your choice: ");

            int choice = getValidIntInput(scanner);

            switch (choice) {
                case 1:
                    showProductMenu(scanner);
                    break;
                case 2:
                    stockMenu(scanner);
                    break;
                case 3:
                    supplierMenu(scanner);
                    break;
                case 4:
                    if (currentClerk.isManager()) {
                        inventoryClerkMenu(scanner);
                    } else {
                        System.out.println("Invalid choice. Please try again.");
                    }
                    break;
                case 5:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

//Product menu
    private static void showProductMenu(Scanner scanner) {
        while (true) {
            System.out.println("\nProduct Menu:");
            System.out.println("1. Create a New Product");
            System.out.println("2. Search Product");
            System.out.println("3. Display All Products");
            System.out.println("4. Modify Price");
            System.out.println("5. Delete Product");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            try {
                int choice = getValidIntInput(scanner, 1, 6);
                
                switch (choice) {
                    case 1:
                        createNewProduct(scanner);
                        break;
                    case 2:
                        searchProduct(scanner);
                        break;
                    case 3:
                        displayAllProducts();
                        break;
                    case 4:
                        modifyPrice(scanner);
                        break;
                    case 5:
                        deleteProduct(scanner);
                        break;
                    case 6:
                        return;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    //Get valid double input 
    private static double getValidDoubleInput(Scanner scanner, double min, double max) {
        double value;
        do {
            while (!scanner.hasNextDouble()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Consume the invalid input
            }
            value = scanner.nextDouble();
            if (value < min || value > max) {
                System.out.println("Invalid value. Please enter a number between " + min + " and " + max + ".");
            }
        } while (value < min || value > max);
        scanner.nextLine(); // Consume the newline
        return value;
    }

    //Create new product
    private static void createNewProduct(Scanner scanner) {
        System.out.print("Enter product name: ");
        String productName = scanner.nextLine();

        System.out.print("Enter quantity: ");
        int quantity = getValidIntInput(scanner);

        System.out.print("Enter price: ");
        double price = getValidDoubleInput(scanner);

        String category = getCategoryInput(scanner);
        System.out.println("Enter category (F for Food, B for Beverages): " + (category.equals("food") ? "F" : "B"));

        boolean isHalal = false;
        boolean isAlcohol = false;

        if (category.equals("food")) {
            isHalal = getBooleanInput(scanner, "Is the food halal? (T/F): ");
        } else {
            isAlcohol = getBooleanInput(scanner, "Is the beverage alcoholic? (T/F): ");
        }

        System.out.print("Enter supplier ID: ");
        int supplierID = getValidIntInput(scanner);

        if (category.equals("food")) {
            Food food = new Food(productName, quantity, price, isHalal, supplierID);
            food.saveProduct();
        } else {
            Beverages beverage = new Beverages(productName, quantity, price, isAlcohol, supplierID);
            beverage.saveProduct();
        }
        System.out.println("Product added successfully.");
    }

    private static String getCategoryInput(Scanner scanner) {
        while (true) {
            System.out.print("Enter category (F for Food, B for Beverages): ");
            String input = scanner.nextLine().toUpperCase();
            if (input.equals("F")) {
                return "food";
            } else if (input.equals("B")) {
                return "beverages";
            } else {
                System.out.println("Invalid input. Please enter 'F' for Food or 'B' for Beverages.");
            }
        }
    }

    private static boolean getBooleanInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().toUpperCase();
            if (input.equals("T")) {
                return true;
            } else if (input.equals("F")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'T' for True or 'F' for False.");
            }
        }
    }

    private static int getValidIntInput(Scanner scanner) {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid integer: ");
            }
        }
    }

    private static double getValidDoubleInput(Scanner scanner) {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
    }

    //Search product    
    private static void searchProduct(Scanner scanner) {
        System.out.print("Enter product name to search: ");
        String productName = scanner.nextLine();

        try {
            Product product = Product.getProductByName(productName);
            if (product != null) {
                product.displayDetails();
            } else {
                System.out.println("Product not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving product: " + e.getMessage());
        }
    }

    //Display all products
    private static void displayAllProducts() {
        String query = "SELECT * FROM product";
        try (Connection conn = SQLConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("\nAll Products:");
            System.out.println("+--------+-----------------+-----------+----------+----------+----------+----------+------------+");
            System.out.println("| ProdID |   Product Name  |  Category | Quantity |  Price   |   Halal  |  Alcohol | SupplierID |");
            System.out.println("+--------+-----------------+-----------+----------+----------+----------+----------+------------+");
            
            while (rs.next()) {
                int productID = rs.getInt("productID");
                String name = rs.getString("productName");
                String category = rs.getString("category");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                boolean halal = rs.getBoolean("halal");
                boolean alcohol = rs.getBoolean("alcohol");
                int supplierID = rs.getInt("supplierID");
                
                System.out.printf("| %-6d | %-15s | %-9s | %-8d | RM %-5.2f | %-8s | %-8s | %-10d |\n",
                                productID, name, category, quantity, price, 
                                halal ? "Yes" : "No", alcohol ? "Yes" : "No", supplierID);
            }
            System.out.println("+--------+-----------------+-----------+----------+----------+----------+----------+------------+");
        } catch (SQLException e) {
            System.out.println("Error displaying products: " + e.getMessage());
            e.printStackTrace();
        }
    }

	//Modify product
    private static void modifyPrice(Scanner scanner) {
        System.out.println("Do you want to modify a food or beverage product?");
        System.out.println("1. Food");
        System.out.println("2. Beverage");
        System.out.print("Enter your choice (1 or 2): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String category = (choice == 1) ? "food" : "beverages";

        // Display products of the chosen category
        List<Product> products = Product.getProductsByCategory(category);
        if (products.isEmpty()) {
            System.out.println("No " + category + " products found.");
            return;
        }

        System.out.println("Available " + category + " products:");
        for (Product product : products) {
            product.displayDetails();
        }

        System.out.print("Enter the name of the product you want to modify: ");
        String productName = scanner.nextLine();

        try {
            Product productToModify = Product.getProductByName(productName);
            if (productToModify == null) {
                System.out.println("Product not found.");
                return;
            }

            System.out.print("Enter the new price: ");
            double newPrice = scanner.nextDouble();

            try {
                productToModify.modifyPrice(newPrice);
                System.out.println("Price updated successfully.");
            } catch (SQLException e) {
                System.out.println("Error updating price: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving product: " + e.getMessage());
        }
    }

    //Delete product
    private static void deleteProduct(Scanner scanner) {
        System.out.print("Enter product name to delete: ");
        String productName = scanner.nextLine();

        try {
            Product product = Product.getProductByName(productName);

            if (product == null) {
                System.out.println("Product not found.");
                return;
            }

            System.out.println("Product details:");
            product.displayDetails();

            System.out.print("Are you sure you want to delete this product? (yes/no): ");
            String confirmation = scanner.nextLine().toLowerCase();

            if (confirmation.equals("yes")) {
                boolean deleted = product.deleteProduct();
                if (deleted) {
                    System.out.println("Product deleted successfully.");
                } else {
                    System.out.println("Failed to delete product.");
                }
            } else {
                System.out.println("Deletion cancelled.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving product: " + e.getMessage());
        }
    }
//End product

//Stock menu
    private static void stockMenu(Scanner scanner) {
        int choice;
        do {
            System.out.println("\nStock Menu:");
            System.out.println("1. Add Stock");
            System.out.println("2. Deduct Stock");
            System.out.println("3. View Stock");
            System.out.println("4. Return to Main Menu");
            System.out.print("Enter your choice: ");

            choice = getValidIntInput(scanner, 1, 4);

            switch (choice) {
                case 1:
                    addStock(scanner);
                    break;
                case 2:
                    deductStock(scanner);
                    break;
                case 3:
                    viewStock();
                    break;
                case 4:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 4);
    }

    private static void addStock(Scanner scanner) {
        System.out.print("Enter product name: ");
        String productName = scanner.nextLine();
        System.out.print("Enter quantity to add: ");
        int quantity = getValidIntInput(scanner, 0, Integer.MAX_VALUE);
        Stock.addStock(productName, quantity);
    }

    private static void deductStock(Scanner scanner) {
        System.out.print("Enter product name: ");
        String productName = scanner.nextLine();
        System.out.print("Enter quantity to deduct: ");
        int quantity = getValidIntInput(scanner, 0, Integer.MAX_VALUE);
        Stock.deductStock(productName, quantity);
    }

    private static void viewStock() {
        Stock.viewStock();
    }
//End stock 

//Supplier menu
    private static void supplierMenu(Scanner scanner) {
        while (true) {
            System.out.println("\nSupplier Menu:");
            System.out.println("1. Add Supplier");
            System.out.println("2. View Supplier");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = getValidIntInput(scanner);

            switch (choice) {
                case 1:
                    try {
                        addSupplier(scanner);
                    } catch (SQLException e) {
                        System.out.println("Error adding supplier: " + e.getMessage());
                    }
                    break;
                case 2:
                    viewSupplier(scanner);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    //View supplier 
    private static void printTableHeader() {
        System.out.println("+------------+----------------------+--------------------------------------------------+------------------+");
        System.out.println("| Supplier ID| Name                 | Address                                          | Contact Info     |");
        System.out.println("+------------+----------------------+--------------------------------------------------+------------------+");
    }

    private static void printSupplierRow(Supplier supplier) {
        String fullAddress = supplier.getAddress().getFullAddress();
        String[] addressLines = splitAddress(fullAddress);
        
        System.out.printf("| %-10d | %-20s | %-48s | %-16s |\n", 
            supplier.getSupplierID(), 
            supplier.getName(), 
            addressLines[0], 
            supplier.getContactInfo());
        
        for (int i = 1; i < addressLines.length; i++) {
            System.out.printf("|            |                      | %-48s |                  |\n", addressLines[i]);
        }
        System.out.println("+------------+----------------------+--------------------------------------------------+------------------+");
    }

    private static String[] splitAddress(String fullAddress) {
        String[] parts = fullAddress.split(", ");
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        
        for (String part : parts) {
            if (currentLine.length() + part.length() + 2 > 48) { // +2 for ", "
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(part);
            } else {
                if (currentLine.length() > 0) {
                    currentLine.append(", ");
                }
                currentLine.append(part);
            }
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines.toArray(new String[0]);
    }

    private static void viewSupplier(Scanner scanner) {
        System.out.print("Enter supplier ID to view (or 0 to view all): ");
        int supplierID = getValidIntInput(scanner);
        System.out.println("Enter supplier ID to view (or 0 to view all): " + supplierID);

        if (supplierID == 0) {
            List<Supplier> suppliers = Supplier.getAllSuppliers();
            if (suppliers.isEmpty()) {
                System.out.println("No suppliers found.");
            } else {
                System.out.println("\nAll Suppliers:");
                printTableHeader();
                for (Supplier supplier : suppliers) {
                    printSupplierRow(supplier);
                }
            }
        } else {
            Supplier supplier = Supplier.getSupplierById(supplierID);
            if (supplier != null) {
                System.out.println("\nSupplier Details:");
                printTableHeader();
                printSupplierRow(supplier);
            } else {
                System.out.println("Supplier not found.");
            }
        }
    }

    //Add Supplier
    private static void addSupplier(Scanner scanner) throws SQLException {
        System.out.println("Enter supplier name: ");
        String supplierName = scanner.nextLine();

        System.out.println("Enter contact information: ");
        String contactInfo = scanner.nextLine();

        // Address input
        System.out.println("Enter street address: ");
        String street = scanner.nextLine();

        System.out.println("Enter city: ");
        String city = scanner.nextLine();

        System.out.println("Enter state: ");
        String state = scanner.nextLine();

        int postcode = 0;
        boolean validPostcode = false;
        while (!validPostcode) {
            System.out.println("Enter postcode: ");
            try {
                postcode = Integer.parseInt(scanner.nextLine());
                validPostcode = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid postcode. Please enter a number.");
            }
        }

        System.out.println("Enter country: ");
        String country = scanner.nextLine();

        try {
            Address address = new Address(street, city, state, postcode, country);
            Supplier supplier = new Supplier(supplierName, contactInfo, address);
            supplier.saveSupplier();
            System.out.println("Supplier added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating address: " + e.getMessage());
        }
        // The SQLException is now properly declared in the method signature
    }
//End Supplier

//Inventory Clerk Menu
    private static void inventoryClerkMenu(Scanner scanner) {
        if (!currentClerk.isManager()) {
            System.out.println("Access denied. Only managers can access this menu.");
            return;
        }

        while (true) {
            System.out.println("\nInventory Clerk Menu:");
            System.out.println("1. Add Inventory Clerk");
            System.out.println("2. Modify Inventory Clerk");
            System.out.println("3. View Inventory Clerk");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = getValidIntInput(scanner);

            switch (choice) {
                case 1:
                    addNewInventoryClerk(scanner);
                    break;
                case 2:
                    modifyInventoryClerk(scanner);
                    break;
                case 3:
                    viewInventoryClerk(scanner);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addNewInventoryClerk(Scanner scanner) {
        System.out.println("Adding a new inventory clerk:");
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter gender: ");
        String gender = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // Get address details
        System.out.print("Enter street: ");
        String street = scanner.nextLine();
        System.out.print("Enter city: ");
        String city = scanner.nextLine();
        System.out.print("Enter state: ");
        String state = scanner.nextLine();
        System.out.print("Enter postcode: ");
        int postcode = getValidIntInput(scanner);
        System.out.print("Enter country: ");
        String country = scanner.nextLine();

        System.out.print("Is this clerk a manager? (true/false): ");
        boolean isManager = Boolean.parseBoolean(scanner.nextLine());

        Address address = new Address(street, city, state, postcode, country);

        if (InventoryClerk.addClerk(firstName, lastName, gender, address, email, password, isManager)) {
            System.out.println("New inventory clerk added successfully.");
        } else {
            System.out.println("Failed to add new inventory clerk.");
        }
    }

    //Modify Inventory Clerk
    private static void modifyInventoryClerk(Scanner scanner) {
        System.out.print("Enter clerk ID to modify: ");
        int clerkID = getValidIntInput(scanner);

        InventoryClerk clerk = InventoryClerk.getClerkById(clerkID);
        if (clerk == null) {
            System.out.println("Clerk not found.");
            return;
        }

        // Display clerk information before modification
        System.out.println("\nCurrent Clerk Information:");
        clerk.displayClerkInfoTable();
        System.out.println(); // Add a blank line for better readability

        System.out.println("What do you want to modify?");
        System.out.println("1. Email");
        System.out.println("2. Address");
        System.out.println("3. Manager Status");
        System.out.println("4. Cancel Modification");
        System.out.print("Enter your choice: ");

        int choice = getValidIntInput(scanner);

        switch (choice) {
            case 1:
                modifyClerkEmail(scanner, clerk);
                break;
            case 2:
                modifyClerkAddress(scanner, clerk);
                break;
            case 3:
                modifyClerkManagerStatus(scanner, clerk);
                break;
            case 4:
                System.out.println("Modification cancelled.");
                return;
            default:
                System.out.println("Invalid choice. Modification cancelled.");
                return;
        }

        // Display updated clerk information after modification
        System.out.println("\nUpdated Clerk Information:");
        clerk.displayClerkInfoTable();
    }
    
    private static void modifyClerkEmail(Scanner scanner, InventoryClerk clerk) {
        System.out.print("Enter new email: ");
        String newEmail = scanner.nextLine();
        clerk.modifyEmail(newEmail);
    }

    private static void modifyClerkAddress(Scanner scanner, InventoryClerk clerk) {
        System.out.print("Enter new street: ");
        String street = scanner.nextLine();
        System.out.print("Enter new city: ");
        String city = scanner.nextLine();
        System.out.print("Enter new state: ");
        String state = scanner.nextLine();
        System.out.print("Enter new postcode: ");
        int postcode = getValidIntInput(scanner);
        System.out.print("Enter new country: ");
        String country = scanner.nextLine();

        Address newAddress = new Address(street, city, state, postcode, country);
        clerk.modifyAddress(newAddress);
    }

    private static void modifyClerkManagerStatus(Scanner scanner, InventoryClerk clerk) {
        System.out.print("Set as manager? (true/false): ");
        boolean isManager = Boolean.parseBoolean(scanner.nextLine());
        clerk.modifyManagerStatus(isManager);
    }

    //View Inventory Clerk 
    private static void viewInventoryClerk(Scanner scanner) {
        System.out.println("\nViewing Inventory Clerk:");
        System.out.println("1. View Individual Clerk");
        System.out.println("2. View All Clerks");
        System.out.print("Enter your choice: ");
        int choice = getValidIntInput(scanner);
        
        switch (choice) {
            case 1:
                System.out.print("Enter the Clerk ID to view: ");
                int clerkId = getValidIntInput(scanner);
                InventoryClerk clerk = InventoryClerk.getClerkById(clerkId);
                if (clerk != null) {
                    clerk.displayClerkInfoTable();
                } else {
                    System.out.println("Clerk not found.");
                }
                break;
            case 2:
                InventoryClerk.displayAllClerksTable();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
//End Inventory Clerk 
}
