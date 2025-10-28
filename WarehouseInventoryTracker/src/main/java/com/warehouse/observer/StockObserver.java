package com.warehouse.observer;

import com.warehouse.entity.Product;

// Our Observer interface—the contract for anything that wants to know about low stock.
public interface StockObserver {
    void onLowStock(Product product);
}
