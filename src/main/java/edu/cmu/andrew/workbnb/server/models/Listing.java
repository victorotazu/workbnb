package edu.cmu.andrew.workbnb.server.models;

public class Listing {

    String id;
    String landlordId;
    String address;
    String type;
    String images;
    Double price;
    String availability;
    String details;

    public Listing(String id, String landlordId, String address, String type, String images, Double price, String availability, String details) {
        this.id = id;
        this.landlordId = landlordId;
        this.address = address;
        this.type = type;
        this.images = images;
        this.price = price;
        this.availability = availability;
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLandlordId() {
        return landlordId;
    }

    public void setLandlordId(String landlordId) {
        this.landlordId = landlordId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

}
