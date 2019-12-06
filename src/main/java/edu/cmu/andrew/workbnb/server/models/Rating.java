package edu.cmu.andrew.workbnb.server.models;

public class Rating {

    String id;
    String renterId;
    String listingId;
    String stars;
    String userId;

    public Rating(String id, String renterId, String listingId, String stars, String userId) {
        this.id = id;
        this.renterId = renterId;
        this.listingId = listingId;
        this.stars = stars;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRenterId() {
        return renterId;
    }

    public void setRenterId(String renterId) {
        this.renterId = renterId;
    }

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) { this.listingId = listingId; }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
