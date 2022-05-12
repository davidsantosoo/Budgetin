package com.hfad.budgetin.model;

public class SalesData {
    private String itemId ,itemNames,itemCode,itemSize,itemDate,itemPaymentMethod, itemCategory;
    int itemPrice,itemStock;
    public SalesData() {
    }

    public SalesData(String itemId, String itemNames, String itemCode, String itemSize, String itemDate, String itemPaymentMethod, int itemPrice, int itemStock, String itemCategory) {
        this.itemId = itemId;
        this.itemNames = itemNames;
        this.itemCode = itemCode;
        this.itemSize = itemSize;
        this.itemDate = itemDate;
        this.itemPaymentMethod = itemPaymentMethod;
        this.itemPrice = itemPrice;
        this.itemStock = itemStock;
        this.itemCategory = itemCategory;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemNames() {
        return itemNames;
    }

    public void setItemNames(String itemNames) {
        this.itemNames = itemNames;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemSize() {
        return itemSize;
    }

    public void setItemSize(String itemSize) {
        this.itemSize = itemSize;
    }

    public String getItemDate() {
        return itemDate;
    }

    public void setItemDate(String itemDate) {
        this.itemDate = itemDate;
    }

    public String getItemPaymentMethod() {
        return itemPaymentMethod;
    }

    public void setItemPaymentMethod(String itemPaymentMethod) {
        this.itemPaymentMethod = itemPaymentMethod;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getItemStock() {
        return itemStock;
    }

    public void setItemStock(int itemStock) {
        this.itemStock = itemStock;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }
}
