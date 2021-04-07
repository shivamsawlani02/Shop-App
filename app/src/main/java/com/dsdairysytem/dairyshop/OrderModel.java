package com.dsdairysytem.dairyshop;

public class OrderModel {
    String date,time,quantity,amount,fatUnits,name,rate;

    public OrderModel(String date, String time, String quantity, String amount, String fatUnits, String name, String rate) {
        this.date = date;
        this.time = time;
        this.quantity = quantity;
        this.amount = amount;
        this.fatUnits = fatUnits;
        this.name = name;
        this.rate = rate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getFatUnits() {
        return fatUnits;
    }

    public void setFatUnits(String fatUnits) {
        this.fatUnits = fatUnits;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
