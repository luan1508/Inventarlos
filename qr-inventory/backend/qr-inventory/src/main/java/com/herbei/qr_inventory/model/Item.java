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
    public Item(String name, String beschreibung, String standort, String qrCode) {
        this.name = name;
        this.beschreibung = beschreibung;
        this.standort = standort;
        this.qrCode = qrCode;
    }

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBeschreibung() { return beschreibung; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }

    public String getStandort() { return standort; }
    public void setStandort(String standort) { this.standort = standort; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
}
