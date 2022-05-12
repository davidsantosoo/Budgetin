package com.hfad.budgetin.model;

public class ImageData {
    private String imageUrl;
    public ImageData(){

    }

    public ImageData(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
