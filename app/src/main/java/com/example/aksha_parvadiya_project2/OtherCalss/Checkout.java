package com.example.aksha_parvadiya_project2.OtherCalss;

public class Checkout {

    String name;
    String email;
    String address;
    String phone;
    double totalprice;

    public Checkout(String name, String email, String address, String phone, double totalprice) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.phone = phone;

        this.totalprice = totalprice;
    }

    String expiry;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(double totalprice) {
        this.totalprice = totalprice;
    }


}
