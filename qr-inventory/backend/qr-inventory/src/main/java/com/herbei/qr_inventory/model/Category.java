package com.herbei.qr_inventory.model;

import jakarta.persistence.*;

@Entity
public class Category 
{

    //#region Attribute
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long CATEGORYID;

    private String categoryName;
    //#endregion

    //#region Konstruktoren
    public Category() {}

    public Category(String name)  { this.categoryName = name;  }
    //#endregion

    //#region Getter & Setter
    public Long getCID() { return this.CATEGORYID; }
    public String getName() { return categoryName; }
    public void setCID(Long categoryID) {  this.CATEGORYID = categoryID; }
    public void setName(String name) { this.categoryName = name; }
    //#endregion


    @Override
    public String toString() {
        return "Category{" +
                "CATEGORYID=" + CATEGORYID +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}