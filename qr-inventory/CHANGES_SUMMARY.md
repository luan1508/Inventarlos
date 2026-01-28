# Spring Security Integration - Zusammenfassung der Änderungen

## Neu erstellte Dateien

### Backend - Model Package
1. **Role.java** - Enum für Benutzerrollen (ROLE_ADMIN, ROLE_USER)
2. **User.java** - User-Entity mit Username, Passwort und Rollen

### Backend - Repository Package
3. **UserRepository.java** - JPA Repository für User-Datenzugriff

### Backend - Service Package
4. **CustomUserDetailsService.java** - Spring Security UserDetailsService
5. **UserService.java** - Business-Logik für User-Verwaltung

### Backend - Security Package (NEU)
6. **SecurityConfig.java** - Zentrale Spring Security Konfiguration

### Backend - Controller Package
7. **AuthController.java** - REST-Endpunkte für Authentifizierung

### Backend - Config Package (NEU)
8. **DataInitializer.java** - Initialisiert Demo-User beim Start

### Frontend
9. **login.html** - Login-Seite mit Demo-Zugangsdaten

### Dokumentation
10. **SECURITY_README.md** - Umfassende Dokumentation der Security-Integration

## Angepasste Dateien

### Backend
1. **pom.xml** - Spring Security Dependencies hinzugefügt
   - spring-boot-starter-security
   - spring-security-test

### Frontend
2. **index.html** - Header mit User-Info und Logout-Button
3. **script.js** - Rollenbasierte Logik und Berechtigungsprüfungen
4. **style.css** - Styles für Login, Header und Logout-Button

## Funktionsübersicht nach Rolle

### Admin-Benutzer (admin / admin123)
- ✅ Items erstellen (POST /items)
- ✅ Items ansehen (GET /items)
- ✅ Items bearbeiten (PUT /items/{id})
- ✅ Items löschen (DELETE /items/{id})
- ✅ UI: Sieht alle Buttons (Bearbeiten, Löschen)

### Standard-User (user / user123)
- ✅ Items erstellen (POST /items)
- ✅ Items ansehen (GET /items)
- ❌ Items bearbeiten (PUT /items/{id}) - 403 Forbidden
- ❌ Items löschen (DELETE /items/{id}) - 403 Forbidden
- ❌ UI: Sieht keine Bearbeiten/Löschen-Buttons

## Schnellstart

1. **Anwendung starten:**
   ```bash
   cd qr-inventory/backend/qr-inventory
   ./mvnw spring-boot:run
   ```

2. **Im Browser öffnen:**
   ```
   http://localhost:8080
   ```

3. **Anmelden:**
   - Als Admin: `admin` / `admin123`
   - Als User: `user` / `user123`

4. **Testen:**
   - Als Admin können Sie alle Funktionen nutzen
   - Als User können Sie nur Items erstellen und ansehen

## Technische Details

- **Passwort-Verschlüsselung:** BCryptPasswordEncoder
- **Authentifizierung:** Form-basiertes Login
- **Autorisierung:** Rollenbasiert auf HTTP-Methode und Endpunkt
- **Session-Management:** Spring Security Standard (JSESSIONID Cookie)
- **Frontend-Security:** JavaScript-basierte Rollenprüfung + Backend-Validierung

## Sicherheitsarchitektur

```
Request → SecurityFilterChain
    ↓
    Login erforderlich? → Ja → Weiterleitung zu /login.html
    ↓
    Eingeloggt? → Ja → Rollenprüfung
    ↓
    Berechtigung? → Ja → Controller → Service → Repository → Datenbank
    ↓
    Keine Berechtigung → 403 Forbidden
```

## Alle Endpunkte im Überblick

| Endpunkt | Methode | Admin | User | Öffentlich |
|----------|---------|-------|------|------------|
| /login.html | GET | ✅ | ✅ | ✅ |
| /auth/login | POST | ✅ | ✅ | ✅ |
| /auth/logout | POST | ✅ | ✅ | - |
| /auth/user-info | GET | ✅ | ✅ | - |
| /items | GET | ✅ | ✅ | - |
| /items | POST | ✅ | ✅ | - |
| /items/{id} | GET | ✅ | ✅ | - |
| /items/{id} | PUT | ✅ | ❌ | - |
| /items/{id} | DELETE | ✅ | ❌ | - |
| /items/{id}/qrcode | GET | ✅ | ✅ | - |
| /items/location | GET/POST | ✅ | ✅ | - |
| /items/category | GET/POST | ✅ | ✅ | - |

Legende: ✅ = Zugriff erlaubt, ❌ = Zugriff verweigert (403)
