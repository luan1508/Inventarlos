package com.herbei.qr_inventory.model;

public class ItemRequest
{
    //#region Attribute
    private String name;
    private String beschreibung;
    private String locationName;
    private String categoryName;
    //#endregion

    //#region Getter & Setter
    public String getName() {
        return name;
    }

    public String getBeschreibung() {
        return beschreibung;
    }
    public String getLocationName() {
        return locationName;
    }
    public String getCategoryName() {
        return categoryName;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    //#endregion

    @Override
    public String toString()
    {
        return "ItemRequest{" +
                "name='" + name + '\'' +
                ", beschreibung='" + beschreibung + '\'' +
                ", locationId=" + locationName +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}