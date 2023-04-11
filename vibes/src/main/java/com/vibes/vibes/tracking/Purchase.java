package com.vibes.vibes.tracking;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Represents a purchase within an end-user's app that a user of the app has interacted with for tracking purposes.
 */
public class Purchase {
    private String id;
    private String affiliation;
    private Double revenue;
    private Double tax;
    private Double shipping;
    private Double coupon;
    private String list;
    private String step;
    private String option;
    private List<Product> products;

    public Purchase() {

    }

    public Purchase(String id, String affiliation, Double revenue, Double tax, Double shipping,
                    Double coupon, String list, String step, String option, List<Product> products) {
        this.id = id;
        this.affiliation = affiliation;
        this.revenue = revenue;
        this.tax = tax;
        this.shipping = shipping;
        this.coupon = coupon;
        this.list = list;
        this.step = step;
        this.option = option;
        this.products = products;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Double getShipping() {
        return shipping;
    }

    public void setShipping(Double shipping) {
        this.shipping = shipping;
    }

    public Double getCoupon() {
        return coupon;
    }

    public void setCoupon(Double coupon) {
        this.coupon = coupon;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    /**
     * Encodes this {@link Product} object as a String of JSON.
     */
    public JSONObject encode() {
        try {
            JSONObject purchase = new JSONObject();
            purchase.put("id", getId());
            purchase.put("affiliation", getAffiliation());
            purchase.put("revenue", getRevenue());
            purchase.put("tax", getTax());
            purchase.put("shipping", getShipping());
            purchase.put("coupon", getCoupon());
            purchase.put("list", getList());
            purchase.put("step", getStep());
            purchase.put("option", getOption());
            purchase.put("products", getProducts());
            return purchase;
        } catch (JSONException exception) {
            return new JSONObject();
        }
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "id='" + id + '\'' +
                ", affiliation='" + affiliation + '\'' +
                ", revenue=" + revenue +
                ", tax=" + tax +
                ", shipping=" + shipping +
                ", coupon=" + coupon +
                ", list='" + list + '\'' +
                ", step='" + step + '\'' +
                ", option='" + option + '\'' +
                ", products=" + products +
                '}';
    }
}
