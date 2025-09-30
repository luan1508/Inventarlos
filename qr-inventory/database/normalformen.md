# 📄 Normalisierung der Datenbank

Dieses Dokument beschreibt die Normalisierung der Datenbank des Inventarisierungssystems bis zur **3. Normalform (3NF)**.

---

## 1. Anforderungen

Das System soll **Inventarobjekte** (z. B. Laptop, Beamer) verwalten.  
Typische Informationen könnten sein:

- Inventar-ID  
- Name / Bezeichnung  
- Kategorie (Laptop, Beamer, …)  
- Standort (Raum, Gebäude)  
- Kaufdatum  
- Preis  
- Status (aktiv, gelöscht)  
- evtl. Benutzer, dem das Gerät zugeordnet ist  

---

## 2. Erste Annahme: *"Unsaubere"* Tabelle (Unnormalisiert)

**Beispiel:**

```
Inventar (
    InventarID,
    Bezeichnung,
    Kategorie,
    Standort,
    Kaufdatum,
    Preis,
    Status,
    BenutzerName,
    BenutzerAbteilung
)
```

**Probleme:**
- Redundanzen (mehrfach gleiche Kategorie, Standort, Abteilung)  
- Abhängigkeiten zwischen Nicht-Schlüsselattributen (z. B. BenutzerName → BenutzerAbteilung)  

---

## 3. Normalisierung

### 3.1 Erste Normalform (1NF)

**Kriterium:**  
- Atomare Werte: Keine mehrfachen oder zusammengesetzten Attribute  
- Alle Werte sind elementar, keine Listen oder Wiederholungen  

**Schema in 1NF:**

```
Inventar (
    InventarID (PK),
    Bezeichnung,
    Kategorie,
    Standort,
    Kaufdatum,
    Preis,
    Status,
    BenutzerID (FK)
)

Benutzer (
    BenutzerID (PK),
    BenutzerName,
    BenutzerAbteilung
)
```

---

### 3.2 Zweite Normalform (2NF)

**Kriterium:**  
- 1NF erfüllt  
- Jedes Nicht-Schlüsselattribut hängt **voll funktional** vom Primärschlüssel ab  
- Keine Teilabhängigkeiten  

Da `InventarID` ein einfacher Schlüssel ist → keine Teilabhängigkeiten.  
Aber: *Kategorie* und *Standort* sind Kandidaten für eigene Tabellen (da sie mehrfach vorkommen).

**Schema in 2NF:**

```
Inventar (
    InventarID (PK),
    Bezeichnung,
    KategorieID (FK),
    StandortID (FK),
    Kaufdatum,
    Preis,
    Status,
    BenutzerID (FK)
)

Kategorie (
    KategorieID (PK),
    Kategoriename
)

Standort (
    StandortID (PK),
    StandortName
)

Benutzer (
    BenutzerID (PK),
    BenutzerName,
    BenutzerAbteilung
)
```

---

### 3.3 Dritte Normalform (3NF)

**Kriterium:**  
- 2NF erfüllt  
- Keine transitiven Abhängigkeiten  

Beispiel: `BenutzerAbteilung` hängt von `BenutzerName` ab → bereits korrekt ausgelagert.  
Kategorien und Standorte haben keine weiteren Attribute → passt.

**Endgültiges Schema (3NF):**

```
Inventar (
    InventarID (PK),
    Bezeichnung,
    KategorieID (FK),
    StandortID (FK),
    Kaufdatum,
    Preis,
    Status,
    BenutzerID (FK)
)

Kategorie (
    KategorieID (PK),
    Kategoriename
)

Standort (
    StandortID (PK),
    StandortName
)

Benutzer (
    BenutzerID (PK),
    BenutzerName,
    BenutzerAbteilung
)
```

---

✅ Damit ist die Datenbank bis zur **3. Normalform** normalisiert.  
Dies reduziert Redundanz, verbessert Konsistenz und erleichtert Erweiterungen.
