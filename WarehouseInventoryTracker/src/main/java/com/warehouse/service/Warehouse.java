package com.warehouse.service;

import com.warehouse.entity.Product;
import com.warehouse.observer.StockObserver;
import java.io.*;
import java.util.*;

public class Warehouse {
    private Map<String, Product> inventory = new HashMap<>();
    private List<StockObserver> observers = new ArrayList<>();
    private final String FILE_PATH = "data/inventory.txt";

    public void addObserver(StockObserver observer) {
        observers.add(observer);
    }

    public void addProduct(Product product) {
        inventory.put(product.getId(), product);
        System.out.println("✅ Added: " + product);
    }

    public void receiveShipment(String id, int quantity) {
        Product product = inventory.get(id);
        if (product != null) {
            product.increaseStock(quantity);
            System.out.println("📦 Shipment received for " + product.getName() + ": +" + quantity);
        } else {
            System.out.println("❌ Product not found.");
        }
    }

    public void fulfillOrder(String id, int quantity) {
        Product product = inventory.get(id);
        if (product != null) {
            try {
                product.decreaseStock(quantity);
                System.out.println("🛒 Order fulfilled for " + product.getName() + ": -" + quantity);
                if (product.getQuantity() < product.getReorderThreshold()) {
                    notifyLowStock(product);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("⚠️ " + e.getMessage());
            }
        } else {
            System.out.println("❌ Product not found.");
        }
    }

    private void notifyLowStock(Product product) {
        for (StockObserver observer : observers) {
            observer.onLowStock(product);
        }
    }

    public void saveInventory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Product p : inventory.values()) {
                writer.write(p.getId() + "," + p.getName() + "," + p.getQuantity() + "," + p.getReorderThreshold());
                writer.newLine();
            }
            System.out.println("💾 Inventory saved to file.");
        } catch (IOException e) {
            System.out.println("❌ Error saving inventory: " + e.getMessage());
        }
    }
}
