package com.project.epay;



// This is a "POJO" (Plain Old Java Object) to hold your bank data.
// Firebase can use this class to automatically write the data.
public class BankAccount {

    public String bankName;
    public String accountNumber;
    public String cardNumber;
    public String expiryDate;
    public String pin;

    // A required empty constructor for Firebase
    public BankAccount() {
    }

    public BankAccount(String bankName, String accountNumber, String cardNumber, String expiryDate, String pin) {
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.pin = pin;
    }
}