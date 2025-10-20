package com.project.epay;

public class Plan {
    private String price;
    private String benefits;
    private String validity;

    public Plan(String price, String benefits, String validity) {
        this.price = price;
        this.benefits = benefits;
        this.validity = validity;
    }

    public String getPrice() { return price; }
    public String getBenefits() { return benefits; }
    public String getValidity() { return validity; }
}

