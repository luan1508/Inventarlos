package com.herbei.qr_inventory.model;

import jakarta.persistence.*;

@Entity
public class Item
{
    //#region Attribute
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ITEMID;

    private String itemName;
    private String itemBeschreibung;
    private String itemQrCode;

    @ManyToOne
    @JoinColumn(name = "Category_ID")
    private Category category_ID;

    @ManyToOne
    @JoinColumn(name = "Location_ID")
    private Location location_ID;
    //#endregion
   
    //#region Konstruktor
    public Item() {}

    public Item(String name, String beschreibung, Category category, Location location, String qrCode) 
    {
        this.itemName = name;
        this.itemBeschreibung = beschreibung;
        this.category_ID = category;
        this.location_ID = location;
        this.itemQrCode = qrCode;
    }
    //#endregion

    //#region Getter & Setter
    public Long getId() { return ITEMID; }
    public String getName() { return this.itemName; }
    public String getBeschreibung() { return this.itemBeschreibung; }
    public Category getCategory()  { return this.category_ID;  }
    public Location getLocation()  { return this.location_ID; }
    public String getQrCode() { return this.itemQrCode ; }
    
    public void setId(Long id) { this.ITEMID = id; }
    public void setName(String name) { this.itemName = name; }
    public void setBeschreibung(String beschreibung) { this.itemBeschreibung = beschreibung; }
    public void setCategory(Category category) { this.category_ID = category;}
    public void setLocation(Location location)  { this.location_ID = location; }
    public void setQrCode(String qrCode) { this.itemQrCode = qrCode; }
    //#endregion
    
}
