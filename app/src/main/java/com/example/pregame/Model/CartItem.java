package com.example.pregame.Model;

public class CartItem extends CartItemId{
    String membershipType;
    double price;

    public CartItem() {}

    public CartItem(String membershipType, double price) {
        this.membershipType = membershipType;
        this.price = price;
    }

    public String getMembershipType() {
        return membershipType;
    }
    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
}
