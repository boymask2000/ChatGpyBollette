package com.boymask.testpay.retrofit_boot;



public class PaymentRequest {

    private String productName;
    private long amount;

    public PaymentRequest(String productName, long amount) {
        this.productName = productName;
        this.amount = amount;
    }
}