package com.vibes.vibes.tracking;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a product within an end-user's app that a user of the app has interacted with for tracking purposes.
 */
public class Product {
    private String id;
    private Double price;
    private String name;
    private String brand;
    private String category;
    private String variant;
    private Integer quantity;
    private String coupon;
    private String position;

    public Product() {
    }

    public Product(String id, Double price, String name, String brand, String category, String variant, Integer quantity, String coupon, String position) {
        this.id = id;
        this.price = price;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.variant = variant;
        this.quantity = quantity;
        this.coupon = coupon;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * Encodes this {@link Product} object as a String of JSON.
     */
    public JSONObject encode() {
        try {
            JSONObject product = new JSONObject();
            product.put("id", getId());
            product.put("price", getPrice());
            product.put("name", getName());
            product.put("brand", getBrand());
            product.put("category", getCategory());
            product.put("variant", getVariant());
            product.put("quantity", getQuantity());
            product.put("coupon", getCoupon());
            product.put("position", getPosition());
            return product;
        } catch (JSONException exception) {
            return new JSONObject();
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", category='" + category + '\'' +
                ", variant='" + variant + '\'' +
                ", quantity=" + quantity +
                ", coupon='" + coupon + '\'' +
                ", position='" + position + '\'' +
                '}';
    }
}
