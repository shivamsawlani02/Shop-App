package com.dsdairysytem.dairyshop.add_client;

import com.google.firebase.firestore.PropertyName;
import com.google.gson.annotations.SerializedName;

public class ClientModel {
    @SerializedName("Name") private String Name;
    private String mobileNumber;

    public ClientModel(){}

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @PropertyName("Mobile Number")
    public String getMobileNumber() {
        return mobileNumber;
    }

    @PropertyName("Mobile Number")
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
