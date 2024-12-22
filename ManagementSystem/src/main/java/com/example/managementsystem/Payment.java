package com.example.managementsystem;
public class Payment {
    private int paymentId;
    private String method;
    private String currency;

    public Payment(String method, String currency) {
        this.method = method;
        this.currency = currency;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}