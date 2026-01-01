
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.guild.inventory;

/**
 *
 * @author Nur Hasna Nadirah
 */

import java.util.List;

public class MagicInventoryItem {
    private String itemId;
    private Integer stock;
    private static final int MAX_STOCK = 1000;
    private static int totalItems = 0;

    public MagicInventoryItem() {
        this.itemId = "Unnamed Magic Item";
        this.stock = null;
        totalItems++;
    }

    public MagicInventoryItem(String itemId, int stock) {
        this.itemId = itemId;
        this.stock = (stock > MAX_STOCK) ? MAX_STOCK : stock; 
        totalItems++;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new IllegalArgumentException("Item ID cannot be empty");
        }
        this.itemId = itemId;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(int stock) {  
        if (stock < 0 || stock > MAX_STOCK) {
            throw new IllegalArgumentException("Stock value is out of range"); // 
        }
        this.stock = stock;
    }

    public static int getTotalItems() {
        return totalItems;
    }

    public static Integer calculateTotalStock(List<MagicInventoryItem> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        int sum = 0;
        for (MagicInventoryItem item : items) {
            if (item.getStock() != null) {
                sum += item.getStock();
            }
        }
        return sum;
    }
}