package com.project.epay;

public class Offer {
    private String category;
    private String title;
    private String description;
    private String code;

    public Offer(String category, String title, String description, String code) {
        this.category = category;
        this.title = title;
        this.description = description;
        this.code = code;
    }

    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCode() { return code; }
}