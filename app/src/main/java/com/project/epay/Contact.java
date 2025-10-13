package com.project.epay;

public class Contact {
    private String name;
    private String initial;
    private int color;
    private String phone;

    public Contact(String name, String initial, int color, String phone) {
        this.name = name;
        this.initial = initial;
        this.color = color;
        this.phone = phone;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getInitial() {
        return initial;
    }

    public int getColor() {
        return color;
    }

    public String getPhone() {
        return phone;
    }
}
