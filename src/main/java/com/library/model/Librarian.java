package com.library.model;

public class Librarian extends Person {

    private String employeeId;
    private String designation;

    public Librarian(int id, String name, String email, String phone,
                      String employeeId, String designation) {
        super(id, name, email, phone);
        this.employeeId = employeeId;
        this.designation = designation;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getDesignation() {
        return designation;
    }

    @Override
    public String displayRole() {
        return "Librarian";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(", EmployeeID=%s, Designation=%s",
                employeeId, designation);
    }
}
