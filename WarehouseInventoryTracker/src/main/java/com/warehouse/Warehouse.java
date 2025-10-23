package com.warehouse;

import java.util.HashMap;
import java.util.Map;

public class Warehouse {
    private Map<String, Product> inventory = new HashMap<>();

    public void addProduct(Product product) {
        if (inventory.containsKey(product.getId())) {
            System.out.println("Product already exists!");
        } else {
            inventory.put(product.getId(), product);
            System.out.println("Product added: " + product.getName());
        }
    }

    public void viewInventory() {
        System.out.println("----- Current Inventory -----");
        if (inventory.isEmpty()) {
            System.out.println("No products available.");
            return;
        }
        for (Product p : inventory.values()) {
            System.out.println(p);
        }
    }
}
