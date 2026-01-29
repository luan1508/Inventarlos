# Spring Security Integration - QR-Inventar

## Übersicht

Das QR-Inventar-Projekt wurde erfolgreich um **Spring Security** mit rollenbasierter Authentifizierung und Autorisierung erweitert.

## Implementierte Features

### 1. Rollen-System

Es gibt zwei Benutzerrollen:

- **ROLE_ADMIN**: Vollständige Verwaltungsrechte
- **ROLE_USER**: Eingeschränkte Rechte

### 2. Berechtigungen nach Rolle

#### Administrator (ROLE_ADMIN)
- ✅ Items **erstellen** (POST)
- ✅ Items **ansehen** (GET)
- ✅ Items **bearbeiten** (PUT)
- ✅ Items **löschen** (DELETE)
- ✅ Kategorien und Standorte verwalten

#### Standard-User (ROLE_USER)
- ✅ Items **erstellen** (POST)
- ✅ Items **ansehen** (GET)
- ❌ Items **bearbeiten** (PUT) - nur Admin
- ❌ Items **löschen** (DELETE) - nur Admin

### 3. Neue Backend-Komponenten

#### Entities
- **User.java**: Benutzer-Entität mit Username, Passwort und Rollen
- **Role.java**: Enum für Benutzerrollen (ROLE_ADMIN, ROLE_USER)

#### Repositories
- **UserRepository.java**: JPA Repository für User-Verwaltung

#### Services
- **CustomUserDetailsService.java**: Implementiert Spring Security UserDetailsService
- **UserService.java**: Business-Logik für User-Verwaltung

#### Security
- **SecurityConfig.java**: Zentrale Security-Konfiguration
  - HTTP-Security mit rollenbasierten Zugriffsregeln
  - BCrypt-Passwort-Verschlüsselung
  - Form-basiertes Login/Logout

#### Controller
- **AuthController.java**: REST-Endpunkte für Authentifizierung
  - `/auth/user-info`: Gibt Informationen zum eingeloggten User zurück
  - `/auth/check`: Prüft Authentifizierungsstatus

#### Konfiguration
- **DataInitializer.java**: Initialisiert Demo-User beim ersten Start

### 4. Frontend-Anpassungen

#### Neue Seiten
- **login.html**: Login-Seite mit Demo-Zugangsdaten

#### Angepasste Seiten
- **index.html**: 
  - Header mit User-Info und Logout-Button
  - Rollenbasierte Funktionsanzeige
  
- **script.js**:
  - Automatische User-Info-Abfrage beim Laden
  - Rollenbasierte Button-Anzeige (Bearbeiten/Löschen nur für Admin)
  - Berechtigungsprüfungen für Aktionen

- **style.css**:
  - Styles für Login-Seite
  - Header-Bar Styles
  - Logout-Button Styles

### 5. Security-Konfiguration Details

#### Geschützte Endpunkte
```java
// Öffentlich zugänglich
/login.html
/style.css
/img/**
/auth/**

// Nur für Admin
PUT /items/**
DELETE /items/**

// Für User und Admin
POST /items/**
GET /items/**
```

#### Passwort-Verschlüsselung
- Verwendet **BCryptPasswordEncoder**
- Passwörter werden niemals im Klartext gespeichert

#### Session-Management
- Form-basiertes Login
- Session-basierte Authentifizierung
- Automatisches Logout bei Session-Ablauf

## Test-Zugangsdaten

Bei der ersten Ausführung werden automatisch zwei Demo-User angelegt:

### Administrator
```
Benutzername: admin
Passwort: admin123
Rolle: ROLE_ADMIN
```

**Berechtigungen:**
- Vollzugriff auf alle Item-Operationen
- Kann Items erstellen, bearbeiten und löschen

### Standard-User
```
Benutzername: user
Passwort: user123
Rolle: ROLE_USER
```

**Berechtigungen:**
- Kann Items erstellen und ansehen
- Kann Items NICHT bearbeiten oder löschen

## Verwendung

### 1. Anwendung starten

```bash
cd qr-inventory/backend/qr-inventory
./mvnw spring-boot:run
```

### 2. Im Browser öffnen

```
http://localhost:8080
```

Sie werden automatisch zur Login-Seite weitergeleitet.

### 3. Anmelden

Verwenden Sie eine der oben genannten Test-Zugangsdaten.

### 4. Features testen

**Als Admin:**
- Melden Sie sich mit `admin` / `admin123` an
- Sie sehen alle Buttons (Bearbeiten, Löschen)
- Sie können Items vollständig verwalten

**Als User:**
- Melden Sie sich mit `user` / `user123` an
- Sie sehen keine Bearbeiten/Löschen-Buttons
- Sie können nur Items erstellen und ansehen

### 5. Abmelden

Klicken Sie auf den "Abmelden"-Button oben rechts.

## Datenbankstruktur

### Neue Tabellen

#### users
- `id` (PRIMARY KEY)
- `username` (UNIQUE, NOT NULL)
- `password` (NOT NULL, BCrypt-verschlüsselt)
- `enabled` (BOOLEAN, NOT NULL)

#### user_roles
- `user_id` (FOREIGN KEY → users.id)
- `role` (ENUM: ROLE_ADMIN, ROLE_USER)

## Sicherheitshinweise

### Für Produktivumgebung

⚠️ **Wichtig**: Die Demo-User sind nur für Entwicklung/Tests gedacht!

Für den Produktionseinsatz sollten Sie:

1. **Eigene Benutzer anlegen**:
   - Demo-User deaktivieren oder löschen
   - Sichere Passwörter verwenden

2. **HTTPS verwenden**:
   - Konfigurieren Sie SSL/TLS
   - Erzwingen Sie HTTPS-Verbindungen

3. **Session-Sicherheit erhöhen**:
   - Session-Timeout konfigurieren
   - CSRF-Schutz aktivieren (derzeit für REST-API deaktiviert)

4. **Passwort-Richtlinien**:
   - Mindestlänge festlegen
   - Komplexität erzwingen
   - Regelmäßige Passwortänderungen

## Erweiterungsmöglichkeiten

### Zusätzliche Features, die implementiert werden können:

1. **Registrierungsfunktion**
   - Neue User können sich selbst registrieren
   - Admin-Freigabe erforderlich

2. **Passwort-Reset**
   - "Passwort vergessen"-Funktion
   - E-Mail-basierter Reset

3. **Erweiterte Rollen**
   - Weitere Rollen wie ROLE_MODERATOR
   - Feinere Berechtigungskontrolle

4. **User-Verwaltung**
   - Admin-Interface zum Verwalten von Usern
   - Rollen zuweisen/entfernen

5. **JWT-Authentifizierung**
   - Token-basiert statt Session-basiert
   - Bessere API-Integration

6. **OAuth2 / Social Login**
   - Login mit Google, GitHub, etc.
   - Single Sign-On (SSO)

## Troubleshooting

### Problem: "403 Forbidden" beim Zugriff
**Lösung**: Stellen Sie sicher, dass Sie eingeloggt sind und die richtige Rolle haben.

### Problem: Login funktioniert nicht
**Lösung**: 
- Überprüfen Sie die Zugangsdaten
- Prüfen Sie die Konsole auf Fehlermeldungen
- Löschen Sie Browser-Cookies und versuchen Sie es erneut

### Problem: Benutzer werden nicht initialisiert
**Lösung**: 
- Prüfen Sie die Konsole beim Start
- DataInitializer sollte die Meldung "✓ Admin-User erstellt" anzeigen
- Löschen Sie ggf. die Datenbank und starten neu

## Projektstruktur

```
qr-inventory/backend/qr-inventory/src/main/java/com/herbei/qr_inventory/
├── config/
│   └── DataInitializer.java           # Initialisiert Demo-User
├── controller/
│   ├── AuthController.java            # Authentifizierungs-Endpunkte
│   └── ItemController.java            # Item-CRUD (geschützt)
├── model/
│   ├── Role.java                      # Rollen-Enum
│   ├── User.java                      # User-Entity
│   ├── Item.java
│   ├── Category.java
│   └── Location.java
├── repository/
│   ├── UserRepository.java            # User-Datenzugriff
│   ├── ItemRepository.java
│   ├── CategoryRepository.java
│   └── LocationRepository.java
├── security/
│   └── SecurityConfig.java            # Spring Security Konfiguration
├── service/
│   ├── CustomUserDetailsService.java  # UserDetailsService Implementierung
│   ├── UserService.java               # User-Business-Logik
│   ├── ItemService.java
│   └── QrCodeService.java
└── QrInventarApplication.java

resources/static/
├── index.html                         # Haupt-UI (geschützt, rollenbasiert)
├── login.html                         # Login-Seite (öffentlich)
├── script.js                          # Frontend-Logik mit Rollenprüfung
└── style.css                          # Styles inkl. Login-Styles
```

## Zusammenfassung

Ihr QR-Inventar-Projekt verfügt nun über:

✅ Vollständige Authentifizierung und Autorisierung  
✅ Rollenbasierte Zugriffskontrolle  
✅ Sichere Passwort-Speicherung (BCrypt)  
✅ Login/Logout-Funktionalität  
✅ Rollenbasierte UI (Admin sieht mehr als User)  
✅ Demo-User für schnelles Testen  
✅ Saubere Code-Organisation in Packages  
✅ REST-API-Sicherheit auf Endpunkt-Ebene  

Die Implementierung folgt Best Practices für Spring Security und ist erweiterbar für zukünftige Anforderungen.
