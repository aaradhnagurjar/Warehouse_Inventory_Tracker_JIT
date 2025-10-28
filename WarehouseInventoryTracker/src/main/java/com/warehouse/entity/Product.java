package com.warehouse.entity;

// The heart of our system: what we track.
public class Product {
    private String id;
    private String name;
    private int quantity;
    private int reorderThreshold;

    // Constructor to nail down the initial state.
    public Product(String id, String name, int quantity, int reorderThreshold) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.reorderThreshold = reorderThreshold;
    }

    // Standard getters—keeping those fields private, as we should! (Encapsulation)
    public String getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public int getReorderThreshold() { return reorderThreshold; }

    // Adds stock—used when a shipment finally shows up.
    public void increaseStock(int amount) {
        if (amount > 0) this.quantity += amount;
    }

    // Reduces stock—for customer orders. Critical logic here.
    public void decreaseStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Order quantity must be positive. Can't ship zero!");
        }
        // Graceful handling for those big orders we can't fulfill yet.
        if (amount > quantity) {
            throw new IllegalArgumentException("Insufficient stock for " + name + ". Only have " + quantity + " left.");
        }
        this.quantity -= amount;
    }

    // Quick summary for logging.
    @Override
    public String toString() {
        return String.format("%s (Qty: %d, Threshold: %d)", name, quantity, reorderThreshold);
    }
}
