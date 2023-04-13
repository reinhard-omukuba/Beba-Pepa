package com.beba.bepa;

public class User {
    private String full_name;
    private String email;
    private String userType;

    private Double longitude;
    private Double latitude;
    private String userId;
    private  String matatuName;
    private String regno;

    public User() {}

    public User(String full_name, String email, String userType, Double longitude, Double latitude, String userId, String matatuName, String regno) {
        this.full_name = full_name;
        this.email = email;
        this.userType = userType;
        this.longitude = longitude;
        this.latitude = latitude;
        this.userId = userId;
        this.matatuName = matatuName;
        this.regno = regno;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMatatuName() {
        return matatuName;
    }

    public void setMatatuName(String matatuName) {
        this.matatuName = matatuName;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }
}
