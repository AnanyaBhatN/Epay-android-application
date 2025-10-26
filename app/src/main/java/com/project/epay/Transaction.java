package com.project.epay;

public class Transaction {

    public enum Type { RECHARGE, SENDMONEY, PAID, RECEIVED, OTHER }

    private String id;
    private Type type;
    private String counterpart;
    private int amount;
    private String dateTime;  // single field now
    private String status;
    private String method;
    private boolean isExpanded = false;

    public Transaction(String id, Type type, String counterpart, int amount,
                       String dateTime, String status, String method) {
        this.id = id;
        this.type = type;
        this.counterpart = counterpart;
        this.amount = amount;
        this.dateTime = dateTime;
        this.status = status;
        this.method = method;
    }

    public String getId() { return id; }
    public Type getType() { return type; }
    public String getCounterpart() { return counterpart; }
    public int getAmount() { return amount; }
    public String getDateTime() { return dateTime; }
    public String getStatus() { return status; }
    public String getMethod() { return method; }
    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { isExpanded = expanded; }

    // ✅ helper to get only date or time for display
    public String getDate() {
        if (dateTime == null || !dateTime.contains(" ")) return dateTime;
        return dateTime.split(" ")[0];
    }

    public String getTime() {
        if (dateTime == null || !dateTime.contains(" ")) return "";
        return dateTime.split(" ")[1];
    }
}
