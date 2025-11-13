package com.herbei.qr_inventory.model;

import jakarta.persistence.*;

@Entity
public class Location
{
    //#region Attribute
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long LOCATIONID;

    private String locationName;
    //#endregion

    //#region Konstruktoren
    public Location() {}

    public Location(String name) { this.locationName = name;  }
    //#endregion

    //#region Getter & Setter
    public Long getLID() {  return this.LOCATIONID; }
    public String getName() { return this.locationName;  }

    public void setLID(Long LID) { this.LOCATIONID = LID; }
    public void setName(String name) { this.locationName = name; }
    //#endregion


    @Override
    public String toString() {
        return "Location{" +
                "LOCATIONID=" + LOCATIONID +
                ", locationName='" + locationName + '\'' +
                '}';
    }
}
