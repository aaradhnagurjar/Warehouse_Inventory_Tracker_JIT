package com.warehouse.service;

import com.warehouse.entity.Product;
import com.warehouse.observer.StockObserver;

// This is the concrete service that handles the actual alerting action.
public class AlertService implements StockObserver {
    @Override
    public void onLowStock(Product product) {
        // Simple console output for now, but imagine this sends an email!
        System.out.println("  RESTOCK ALERT: Low stock for " + product.getName()
                + " – only " + product.getQuantity() + " left! (Need to hit " + product.getReorderThreshold() + ")");
    }
}
