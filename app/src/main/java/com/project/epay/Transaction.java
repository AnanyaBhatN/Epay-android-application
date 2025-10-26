package com.project.epay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {

    public enum Type { RECHARGE, SENDMONEY, RECEIVED, PAID, OTHER }

    private String id;
    private Type type;
    private String counterpart;
    private int amount;
    private String dateTime; // full "yyyy-MM-dd HH:mm:ss"
    private String status;
    private String method;
    private boolean expanded;

    public Transaction(String id, Type type, String counterpart, int amount, String dateTime, String status, String method) {
        this.id = id;
        this.type = type;
        this.counterpart = counterpart;
        this.amount = amount;
        this.dateTime = dateTime != null ? dateTime : "";
        this.status = status != null ? status : "Success";
        this.method = method != null ? method : "";
        this.expanded = false;
    }

    public String getId() { return id; }
    public Type getType() { return type; }
    public String getCounterpart() { return counterpart; }
    public int getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getMethod() { return method; }
    public boolean isExpanded() { return expanded; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }
    public String getDateTime() { return dateTime; }

    // ✅ Convert dateTime string to Date object safely
    public Date getDateTimeObject() {
        if (dateTime.isEmpty()) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
        } catch (ParseException e) {
            return null;
        }
    }
}
