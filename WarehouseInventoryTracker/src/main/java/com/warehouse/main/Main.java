package com.warehouse.main;

import com.warehouse.entity.Product;
import com.warehouse.service.AlertService;
import com.warehouse.service.Warehouse;
import java.util.Scanner; 

public class Main {
    public static void main(String[] args) throws InterruptedException {
        
        Warehouse warehouse = new Warehouse(); 
        AlertService alertService = new AlertService();
        warehouse.addObserver(alertService);

        System.out.println("\n*** WELCOME TO WAREHOUSE INVENTORY TRACKER ***");
        
        // Initial setup for mandatory assignment demo (Laptop)
        runInitialTests(warehouse);

        // --- Interactive Menu Loop ---
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n--- MAIN MENU ---");
                System.out.println("1.  Add New Product");
                System.out.println("2.  View Current Inventory List");
                System.out.println("3.  Run Concurrent Simulation");
                System.out.println("4.  Delete Product");
                System.out.println("5.  Save & Exit");
                System.out.print("Enter your choice (1-5): ");

                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        addProductFromUserInput(warehouse, scanner);
                        break;
                    case "2":
                        warehouse.displayInventory();
                        break;
                    case "3":
                        runConcurrentSimulation(warehouse, scanner);
                        break;
                    case "4":
                        deleteProductFromUserInput(warehouse, scanner);
                        break;
                    case "5":
                        warehouse.saveInventory();
                        System.out.println(" Thank you for using Inventory Tracker. Exiting...");
                        return;
                    default:
                        System.out.println(" Invalid choice. Please enter a number between 1 and 5.");
                }
            }
        } catch (Exception e) {
            System.err.println(" An unexpected error occurred: " + e.getMessage());
        }
    }
    
    // --- Helper Methods ---

    private static void runInitialTests(Warehouse warehouse) {
        System.out.println("\n--- Initializing Core Workflow (P001: Laptop) ---");
        String laptopId = "P001";
        
        if (warehouse.checkProductExists(laptopId)) {
             System.out.println("Laptop already in inventory. Skipping initial setup.");
             return;
        }

        warehouse.addProduct(new Product(laptopId, "Laptop", 0, 5)); 
        warehouse.receiveShipment(laptopId, 10);
        warehouse.fulfillOrder(laptopId, 6); 
    }

    private static void addProductFromUserInput(Warehouse warehouse, Scanner scanner) {
        System.out.println("\n--- ADD NEW PRODUCT ---");
        try {
            System.out.print("Enter Product ID (e.g., P003): ");
            String id = scanner.nextLine().trim();
            
            System.out.print("Enter Product Name: ");
            String name = scanner.nextLine().trim();
            
            System.out.print("Enter Initial Quantity: ");
            int quantity = Integer.parseInt(scanner.nextLine().trim()); 
            
            System.out.print("Enter Reorder Threshold: ");
            int threshold = Integer.parseInt(scanner.nextLine().trim());
            
            if (warehouse.checkProductExists(id)) {
                System.out.println(" Product ID " + id + " already exists. Please choose a different ID.");
                return;
            }
            
            warehouse.addProduct(new Product(id, name, quantity, threshold));
            
        } catch (NumberFormatException e) {
            System.err.println(" Error: Quantity and Threshold must be valid numbers.");
        } catch (Exception e) {
             System.err.println(" Error adding product: " + e.getMessage());
        }
    }
    
    // NEW METHOD: Handles deleting a product based on user input ID
    private static void deleteProductFromUserInput(Warehouse warehouse, Scanner scanner) {
        System.out.println("\n--- DELETE PRODUCT ---");
        System.out.print("Enter the ID of the product to delete: ");
        String idToDelete = scanner.nextLine().trim();
        
        warehouse.deleteProduct(idToDelete);
    }
    
    // Updated method to take user input for which product to simulate
    private static void runConcurrentSimulation(Warehouse warehouse, Scanner scanner) throws InterruptedException {
        
        System.out.println("\n--- RUN CONCURRENT SIMULATION ---");
        System.out.print("Enter the Product ID for simulation (e.g., P005): ");
        
        String simulationId = scanner.nextLine().trim();

        if (!warehouse.checkProductExists(simulationId)) {
            System.out.println(" Error: Product ID '" + simulationId + "' not found in inventory. Please add it first.");
            return;
        }

        String productName = warehouse.getProductName(simulationId);
        
        System.out.println("--- Simulating Concurrent Access on '" + productName + "' (" + simulationId + ") ---");
        
        // Ensure the product exists before starting threads (e.g., Keyboard)
        if (simulationId.equals("P005") && !warehouse.checkProductExists("P005")) {
             warehouse.addProduct(new Product("P005", "Keyboard", 15, 10)); 
        }

        // Thread 1: Shipments
        Thread shipmentThread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                warehouse.receiveShipment(simulationId, 3); 
                try { Thread.sleep(50); } catch (InterruptedException ignored) {}
            }
        });

        // Thread 2: Orders
        Thread orderThread = new Thread(() -> {
            for (int i = 0; i < 12; i++) {
                warehouse.fulfillOrder(simulationId, 2); 
                try { Thread.sleep(70); } catch (InterruptedException ignored) {}
            }
        });

        shipmentThread.start();
        orderThread.start();

        shipmentThread.join();
        orderThread.join();
        
        System.out.println("--- Concurrent Operations Complete ---");
    }
}
