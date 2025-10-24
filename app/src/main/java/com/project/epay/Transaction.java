//package com.project.epay;
//
//public class Transaction {
//
//    public enum Type { RECEIVED, PAID, RECHARGE , SENDMONEY }
//
//    private String id;
//    private Type type;
//    private String counterpart;  // operator name
//    private int amount;
//    private String date;
//    private String time;
//    private String status;
//    private String method;
//
//    private boolean expanded;
//
//    public Transaction() {} // Firebase requires default constructor
//
//    // Constructor matching your DB
//    public Transaction(String id, Type type, String counterpart, int amount,
//                       String dateTime, String status, String method) {
//        this.id = id;
//        this.type = type;
//        this.counterpart = counterpart;
//        this.amount = amount;
//
//        // Split dateTime if available
//        if (dateTime != null && dateTime.contains(" ")) {
//            String[] parts = dateTime.split(" ");
//            this.date = parts[0];
//            this.time = parts[1];
//        } else {
//            this.date = dateTime != null ? dateTime : "N/A";
//            this.time = "N/A";
//        }
//
//        this.status = status != null ? status : "Success";
//        this.method = method != null ? method : "Mobile Recharge";
//        this.expanded = false;
//    }
//
//    // Getters and setters
//    public String getId() { return id; }
//    public Type getType() { return type; }
//    public String getCounterpart() { return counterpart; }
//    public int getAmount() { return amount; }
//    public String getDate() { return date; }
//    public String getTime() { return time; }
//    public String getStatus() { return status; }
//    public String getMethod() { return method; }
//    public boolean isExpanded() { return expanded; }
//
//    public void setId(String id) { this.id = id; }
//    public void setType(Type type) { this.type = type; }
//    public void setCounterpart(String counterpart) { this.counterpart = counterpart; }
//    public void setAmount(int amount) { this.amount = amount; }
//    public void setDate(String date) { this.date = date; }
//    public void setTime(String time) { this.time = time; }
//    public void setStatus(String status) { this.status = status; }
//    public void setMethod(String method) { this.method = method; }
//    public void setExpanded(boolean expanded) { this.expanded = expanded; }
//}

package com.project.epay;

public class Transaction {

    public enum Type { RECEIVED, PAID, RECHARGE, SENDMONEY, OTHER }

    private String id;
    private Type type;
    private String counterpart;  // operator, recipient, or other
    private int amount;
    private String date;
    private String time;
    private String status;
    private String method;
    private boolean expanded;

    public Transaction() {} // Firebase requires default constructor

    public Transaction(String id, Type type, String counterpart, int amount,
                       String dateTime, String status, String method) {
        this.id = id;
        this.type = type;
        this.counterpart = counterpart;
        this.amount = amount;

        if (dateTime != null && dateTime.contains(" ")) {
            String[] parts = dateTime.split(" ");
            this.date = parts[0];
            this.time = parts[1];
        } else {
            this.date = dateTime != null ? dateTime : "N/A";
            this.time = "N/A";
        }

        this.status = status != null ? status : "Success";
        this.method = method != null ? method : "N/A";
        this.expanded = false;
    }

    // Getters and setters
    public String getId() { return id; }
    public Type getType() { return type; }
    public String getCounterpart() { return counterpart; }
    public int getAmount() { return amount; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
    public String getMethod() { return method; }
    public boolean isExpanded() { return expanded; }

    public void setId(String id) { this.id = id; }
    public void setType(Type type) { this.type = type; }
    public void setCounterpart(String counterpart) { this.counterpart = counterpart; }
    public void setAmount(int amount) { this.amount = amount; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setStatus(String status) { this.status = status; }
    public void setMethod(String method) { this.method = method; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }
}

