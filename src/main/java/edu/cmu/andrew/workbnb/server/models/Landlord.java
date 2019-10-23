package edu.cmu.andrew.workbnb.server.models;

public class Landlord {
    String firstName;
    String lastName;
    String phoneNumber;
    Boolean subLeaseAuth;
    String bankAccountNumber;

    public Landlord(String firstName, String lastName, String phoneNumber, Boolean subLeaseAuth, String bankAccountNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.subLeaseAuth = subLeaseAuth;
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getSubLeaseAuth() {
        return subLeaseAuth;
    }

    public void setSubLeaseAuth(Boolean subLeaseAuth) {
        this.subLeaseAuth = subLeaseAuth;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

}
