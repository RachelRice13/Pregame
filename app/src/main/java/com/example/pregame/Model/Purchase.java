package com.example.pregame.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Purchase implements Serializable {
    ArrayList<CartItem> items;
    double totalPrice, subtotal, discountAmount;
    String date, discountType;
    PaymentMethod paymentMethod;

    public Purchase() {}

    public Purchase(ArrayList<CartItem> items, double totalPrice, double subtotal, double discountAmount, String date, String discountType, PaymentMethod paymentMethod) {
        this.items = items;
        this.totalPrice = totalPrice;
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.date = date;
        this.discountType = discountType;
        this.paymentMethod = paymentMethod;
    }

    public ArrayList<CartItem> getItems() {
        return items;
    }
    public void setItems(ArrayList<CartItem> items) {
        this.items = items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }
    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getDiscountType() {
        return discountType;
    }
    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
