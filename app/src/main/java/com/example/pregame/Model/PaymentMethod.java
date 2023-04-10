package com.example.pregame.Model;

import java.io.Serializable;

public class PaymentMethod {
    String cardNumber, cardholderName, expiryDate, securityCode;

    public PaymentMethod() {}

    public PaymentMethod(String cardNumber, String cardholderName, String expiryDate, String securityCode) {
        this.cardNumber = cardNumber;
        this.cardholderName = cardholderName;
        this.expiryDate = expiryDate;
        this.securityCode = securityCode;
    }

    public String getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardholderName() {
        return cardholderName;
    }
    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getSecurityCode() {
        return securityCode;
    }
    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }
}
