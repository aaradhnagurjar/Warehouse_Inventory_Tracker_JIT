package com.warehouse.service;

import com.warehouse.entity.Product;
import com.warehouse.observer.StockObserver;
import java.io.*;
import java.util.*;

// The central hub (Subject in Observer Pattern) and business logic controller.
public class Warehouse {
    private Map<String, Product> inventory = new HashMap<>(); 
    private List<StockObserver> observers = new ArrayList<>();
    private final String FILE_PATH = "data/inventory.txt"; 

    // Constructor: attempts to load state from file on startup
    public Warehouse() {
        loadInventory();
    }

    //  Persistence Methods 

    // Pulls data from the file to populate the inventory map.
    private void loadInventory() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    Product product = new Product(
                        parts[0], 
                        parts[1], 
                        Integer.parseInt(parts[2]), 
                        Integer.parseInt(parts[3])
                    );
                    inventory.put(product.getId(), product);
                    
                    // NEW: Check if stock is low immediately upon loading
                    if (product.getQuantity() < product.getReorderThreshold()) {
                        notifyLowStock(product);
                    }
                }
            }
            System.out.println("Inventory data loaded from previous session.");
        } catch (FileNotFoundException e) {
            System.out.println("Inventory file not found. Starting with an empty warehouse.");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Critical Error loading inventory: " + e.getMessage());
        }
    }

    // Writes the current state of the inventory back to the file (Synchronized for safety).
    public synchronized void saveInventory() {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs(); 
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Product p : inventory.values()) {
                writer.write(p.getId() + "," + p.getName() + "," + p.getQuantity() + "," + p.getReorderThreshold());
                writer.newLine();
            }
            System.out.println("Inventory state saved successfully.");
        } catch (IOException e) {
            System.out.println(" Error saving inventory: " + e.getMessage());
        }
    }

    //  Utility Methods 
    
    // Checks if a product ID already exists
    public boolean checkProductExists(String id) {
        return inventory.containsKey(id);
    }
    
    // Retrieves product name for display in Main.java
    public String getProductName(String id) {
        Product product = inventory.get(id);
        return (product != null) ? product.getName() : "Unknown Product";
    }

    // Allows services (AlertService) to register for notifications
    public void addObserver(StockObserver observer) {
        observers.add(observer);
    }

    // Broadcasts the low stock event to everyone who signed up.
    private void notifyLowStock(Product product) {
        for (StockObserver observer : observers) {
            observer.onLowStock(product);
        }
    }
    
    //  Inventory Management Methods (Synchronized for Multithreading) 

    // Adds a brand new item to the tracker.
    public synchronized void addProduct(Product product) {
        inventory.put(product.getId(), product);
        System.out.println("New Item Tracked: " + product.getName() + " " + product);
        
        // NEW: Check if stock is low immediately upon adding
        if (product.getQuantity() < product.getReorderThreshold()) {
            notifyLowStock(product);
        }
    }

    // Deletes a product from the inventory by ID (NEW METHOD)
    public synchronized void deleteProduct(String id) {
        if (inventory.containsKey(id)) {
            Product removedProduct = inventory.remove(id);
            System.out.println(" Successfully deleted product: " + removedProduct.getName() + " (" + id + ")");
        } else {
            System.out.println(" Error: Product ID '" + id + "' not found in inventory. Nothing deleted.");
        }
    }
    
    // Increases stock when a shipment arrives.
    public synchronized void receiveShipment(String id, int quantity) {
        Product product = inventory.get(id);
        if (product != null) {
            product.increaseStock(quantity);
            System.out.println("Shipment received for " + product.getName() + ": +" + quantity + " (New Qty: " + product.getQuantity() + ")");
        } else {
            System.out.println("Oops, that product ID (" + id + ") doesn't exist yet.");
        }
    }

    // Decreases stock and checks for alerts immediately.
    public synchronized void fulfillOrder(String id, int quantity) {
        Product product = inventory.get(id);
        if (product != null) {
            try {
                product.decreaseStock(quantity);
                System.out.println("Order fulfilled for " + product.getName() + ": -" + quantity + " (New Qty: " + product.getQuantity() + ")");
                
                // If the stock dips too low, fire the alert!
                if (product.getQuantity() < product.getReorderThreshold()) {
                    notifyLowStock(product);
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println(" Can't fulfill order. Product ID (" + id + ") not found.");
        }
    }
    
    //  Display Method 

    public void displayInventory() {
        if (inventory.isEmpty()) {
            System.out.println("\n The warehouse inventory is currently empty.");
            return;
        }
        System.out.println("\n--- Current Warehouse Inventory Snapshot ---");
        System.out.printf("%-8s | %-20s | %-8s | %-10s%n", "ID", "Name", "Quantity", "Threshold");
        System.out.println("-------------------------------------------------------");

        for (Product p : inventory.values()) {
            System.out.printf("%-8s | %-20s | %-8d | %-10d%n", 
                p.getId(), p.getName(), p.getQuantity(), p.getReorderThreshold());
        }
        System.out.println("-------------------------------------------------------");
    }
}
