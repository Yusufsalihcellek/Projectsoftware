package com.mainPackage.randevuapp.Model;

public class User {
    private String idNumber;
    private String password;

    public User(String idNumber, String password) {
        this.idNumber = idNumber;
        this.password = password;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getPassword() {
        return password;
    }
}