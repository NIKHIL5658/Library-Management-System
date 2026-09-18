package com.library.model;

public abstract class Person {

    protected int id;
    protected String name;
    protected String email;
    protected String phone;

    public Person(int id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public abstract String displayRole();

    @Override
    public String toString() {
        return String.format("[%s] ID=%d, Name=%s, Email=%s, Phone=%s",
                displayRole(), id, name, email, phone);
    }
}
