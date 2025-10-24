package com.warehouse.main;

import com.warehouse.entity.Product;
import com.warehouse.service.AlertService;
import com.warehouse.service.Warehouse;

public class Main {
    public static void main(String[] args) {
        Warehouse warehouse = new Warehouse();
        AlertService alertService = new AlertService();
        warehouse.addObserver(alertService);

        // Add product
        Product laptop = new Product("P001", "Laptop", 0, 5);
        warehouse.addProduct(laptop);

        // Receive shipment
        warehouse.receiveShipment("P001", 10);

        // Fulfill orders
        warehouse.fulfillOrder("P001", 6);

        // Save inventory
        warehouse.saveInventory();
    }
}
