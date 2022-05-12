package com.hfad.budgetin.model;

public class StockData {
   private String itemCategory,  itemId , itemNames,itemCode,itemSize, itemDate,itemImage;
    int itemPrice,itemStock;
    public StockData() {
    }


    public StockData(String itemCategory, String itemDate, String itemId, String itemNames, String itemCode, String itemSize, int itemPrice, int itemStock, String itemImage ) {
        this.itemCategory = itemCategory;
        this.itemDate = itemDate;
        this.itemId = itemId;
        this.itemNames = itemNames;
        this.itemCode = itemCode;
        this.itemSize = itemSize;
        this.itemPrice = itemPrice;
        this.itemStock = itemStock;
        this.itemImage = itemImage;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getItemDate() {
        return itemDate;
    }



    public String getId() {
        return itemId;
    }

    public void setId(String itemId) {
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

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }
}
