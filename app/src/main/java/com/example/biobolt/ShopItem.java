package com.example.biobolt;

public class ShopItem {
    private String name;
    private String info;
    private String price;
    private int imageResource;
    private int cartedCount;
    private String id;

    public ShopItem() {}

    public ShopItem(String name, String info, String price, int imageResource, int cartedCount) {
        this.name = name;
        this.info = info;
        this.price = price;
        this.imageResource = imageResource;
        this.cartedCount = cartedCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String _getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCartedCount() {
        return cartedCount;
    }
}
