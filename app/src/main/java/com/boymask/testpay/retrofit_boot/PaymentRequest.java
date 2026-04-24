package com.boymask.testpay.retrofit_boot;



public class PaymentRequest {

    private String productName;
    private int amount;

    public PaymentRequest(String productName, int amount) {
        this.productName = productName;
        this.amount = amount;
    }
}