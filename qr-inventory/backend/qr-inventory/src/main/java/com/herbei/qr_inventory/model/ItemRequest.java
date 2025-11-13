package com.herbei.qr_inventory.model;

public class ItemRequest {
    private String name;
    private String beschreibung;
    private String locationName;
    private String categoryName;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "ItemRequest{" +
                "name='" + name + '\'' +
                ", beschreibung='" + beschreibung + '\'' +
                ", locationId=" + locationName +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}