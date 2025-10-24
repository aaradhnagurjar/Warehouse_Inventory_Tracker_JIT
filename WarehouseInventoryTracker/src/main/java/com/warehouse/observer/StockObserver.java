package com.warehouse.observer;

import com.warehouse.entity.Product;

public interface StockObserver {
    void onLowStock(Product product);
}
