package edu.cmu.andrew.workbnb.server.models;

import com.sun.org.apache.xpath.internal.operations.Bool;

public class Favorite {
    String id;
    String renterId;
    String listingId;
    Boolean favoriteWs;

    public Favorite(String id, String renterId, String listingId, Boolean favoriteWs) {
        this.id = id;
        this.renterId = renterId;
        this.listingId = listingId;
        this.favoriteWs = favoriteWs;
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

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    public Boolean getFavoriteWs() {
        return favoriteWs;
    }

    public void setFavoriteWs(Boolean favoriteWs) {
        this.favoriteWs = favoriteWs;
    }
}
