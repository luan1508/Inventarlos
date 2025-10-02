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
 

---

## 2. Erste Annahme: *"Unsaubere"* Tabelle (Unnormalisiert)

**Beispiel:**

```
Item (
    ID,
    Bezeichnung,
    Category,
    Location,
)
```

---

## 3. Normalisierung

### 3.1 Erste Normalform (1NF)

**Kriterium:**  
- Atomare Werte: Keine mehrfachen oder zusammengesetzten Attribute  
- Alle Werte sind elementar, keine Listen oder Wiederholungen  

**Schema in 1NF:**

```
Item (
    IID (PK),
    Name,
    Beschreibung,
    Categoriy,
    Location,
)


```

---

### 3.2 Zweite Normalform (2NF)

**Kriterium:**  
- 1NF erfüllt  
- Jedes Nicht-Schlüsselattribut hängt **voll funktional** vom Primärschlüssel ab  
- Keine Teilabhängigkeiten  

Da `IID` ein einfacher Schlüssel ist → keine Teilabhängigkeiten.  
Aber: *Kategorie* und *Standort* sind Kandidaten für eigene Tabellen (da sie mehrfach vorkommen).

**Schema in 2NF:**

```
Item (
    IID (PK),
    Name,
    Beschreibung,
    CID (FK),
    SID (FK),
)

Category (
    CID (PK),
    Name
)

Location (
    LID (PK),
    Name
)


```

---

### 3.3 Dritte Normalform (3NF)

**Kriterium:**  
- 2NF erfüllt  
- Keine transitiven Abhängigkeiten  

**Endgültiges Schema (3NF):**

```
Item (
    InventarID (PK),
    Name,
    Beschreibung,
    CID (FK),
    LID (FK),
)

Category (
    CID (PK),
    Name
)

Location (
    LID (PK),
    Name
)


```

---

Damit ist die Datenbank bis zur **3. Normalform** normalisiert.  
Dies reduziert Redundanz, verbessert Konsistenz und erleichtert Erweiterungen.
