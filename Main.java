package com.warehouse;

public class Main {
    public static void main(String[] args) {
        Warehouse warehouse = new Warehouse();

        Product p1 = new Product("P001", "Laptop", 10, 5);
        Product p2 = new Product("P002", "Mouse", 20, 10);

        warehouse.addProduct(p1);
        warehouse.addProduct(p2);
        warehouse.viewInventory();
    }
}
