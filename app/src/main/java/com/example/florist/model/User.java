package com.example.florist.model;

public class User {
    private String userId;
    private String username;
    private String email;
    private String password;
    private String phoneNumber;
    private boolean isLogin;

    public User(String username, String email, String password, String phoneNumber, boolean isLogin) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.isLogin = isLogin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean getIsLogin() {return isLogin;}

    public void setIslogin() {this.isLogin = isLogin; }

}
