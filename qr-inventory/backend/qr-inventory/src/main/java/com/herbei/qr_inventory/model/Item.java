package com.herbei.qr_inventory.model;

import jakarta.persistence.*;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String beschreibung;

    @ManyToOne
    @JoinColumn(name = "CID")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "location_lid")
    private Location location;

    private String qrCode;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    // Standard-Konstruktor
    public Item() {}

    // Konstruktor mit Feldern
    public Item(String name, String beschreibung, Category category, Location location, String qrCode) {
        this.name = name;
        this.beschreibung = beschreibung;
        this.category = null;
        this.location = location;
        this.qrCode = qrCode;
    }

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBeschreibung() { return beschreibung; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }

    public Category getKategorie() { return category; }
    public void setKategorie(Category category) { this.category = category; }

    public Location getStandort() { return location; }
    public void setStandort(Location location) { this.location = location; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
}
