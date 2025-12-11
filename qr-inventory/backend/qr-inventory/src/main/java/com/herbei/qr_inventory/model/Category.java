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
    public String getName() { return categoryName; }
    public Long getCID() { return this.CATEGORYID; }
    public void setName(String name) { this.categoryName = name; }
    public void setCID(Long categoryID) {  this.CATEGORYID = categoryID; }
    //#endregion


    @Override
    public String toString()
    {
        return "Category{" +
                "CATEGORYID=" + CATEGORYID +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}