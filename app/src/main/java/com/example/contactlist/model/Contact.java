package com.example.contactlist.model;

public class Contact {

    private int id;
    private String name;
    private String phoneNumber;
    private String email;
    private String picture;

    public Contact() {
    }

    public Contact(int id, String name, String phoneNumber, String email, String picture) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.picture = picture;
    }

    public Contact(int id, String picture) {
        this.id = id;
        this.picture = picture;
    }

    public Contact(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public Contact(String name, String phoneNumber, String email , String picture) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.picture =picture;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
