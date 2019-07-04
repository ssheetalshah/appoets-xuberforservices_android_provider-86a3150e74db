package com.xuber_for_services.provider.Models;

import java.io.Serializable;

public class SubServiceModel implements Serializable {

    private String id;
    private String name;
    private String image;
    private boolean isSelected;
    String available,pricePerHour;

    public SubServiceModel(String available, String pricePerHour) {
        this.available = available;
        this.pricePerHour = pricePerHour;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public SubServiceModel(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
