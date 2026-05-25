# Event-Ticket-Plattform

Eine RESTful-Event-Ticketing-Plattform, entwickelt mit **Java 26** und **Spring Boot 4**. Organisatoren können Events mit mehreren Ticket-Typen erstellen und verwalten, Benutzer können Tickets kaufen (inkl. QR-Code-Generierung) und Personal kann Tickets validieren. Die Authentifizierung erfolgt über **Keycloak** (OAuth2 / OpenID Connect).

---

## Features

- **Event-Management** – Organisatoren können Events mit Ticket-Typen anlegen, bearbeiten, löschen und einsehen
- **Ticket-Verkauf** – Benutzer können Tickets für veröffentlichte Events kaufen; Overselling wird durch pessimistisches Locking verhindert
- **QR-Code-Generierung** – Nach dem Kauf wird automatisch ein QR-Code (ZXing) generiert und als Base64-PNG gespeichert
- **Ticket-Validierung** – Personal kann Tickets per QR-Code oder manuell validieren; Doppelvalidierung wird erkannt
- **Volltext-Suche** – Veröffentlichte Events können per PostgreSQL Full-Text Search durchsucht werden
- **Rollenbasierte Autorisierung** – Drei Rollen: `ORGANIZER`, `STAFF` und authentifizierte Benutzer

---

## Technologie-Stack

| Technologie                                           | Zweck                                   |
| ----------------------------------------------------- | --------------------------------------- |
| Java 26                                               | Programmiersprache                      |
| Spring Boot 4.0.6                                     | Application Framework                   |
| Spring Data JPA / Hibernate                           | Datenbankzugriff (ORM)                  |
| Spring Security + OAuth2 Resource Server              | Authentifizierung & Autorisierung       |
| Spring Validation                                     | Eingabevalidierung                      |
| Keycloak                                              | Identity & Access Management            |
| PostgreSQL                                            | Produktionsdatenbank                    |
| H2                                                    | In-Memory-Datenbank für Tests           |
| MapStruct 1.6.3                                       | DTO-Entity-Mapping                       |
| Lombok 1.18.46                                        | Boilerplate-Reduktion                   |
| ZXing (Google) 3.5.1                                  | QR-Code-Generierung                     |
| Maven                                                 | Build-Tool                              |
| Docker Compose                                        | Lokale Infrastruktur (Postgres, Keycloak) |

---

## Architektur

Die Anwendung folgt einer klassischen **Layered Architecture**:

```
Controller (REST-Endpunkte)
    ↓
Service (Geschäftslogik, Interfaces + Impl)
    ↓
Repository (Spring Data JPA)
    ↓
Datenbank (PostgreSQL)
```

- **DTOs** entkoppeln die API von der Domäne, gemappt mit **MapStruct**
- **Globale Exception-Handling** über `@RestControllerAdvice`
- **JPA-Auditing** für `created_at` / `updated_at` (via `AuditingEntityListener`)

---

## Sicherheitskonzept

- **OAuth2 / JWT** – JWT-Tokens werden von Keycloak ausgestellt und von Spring Security validiert
- **Rollen** aus dem JWT (`realm_access.roles`) werden automatisch in Spring Security `GrantedAuthority` konvertiert
- **User-Provisioning** – Ein Servlet-Filter legt beim ersten Request automatisch einen lokalen `User`-Datensatz aus den JWT-Claims an
- **Endpoint-Sicherheit**:
  - `/api/v1/published-events` – öffentlich (Permit All)
  - `/api/v1/ticket-validations` – nur `ROLE_STAFF`
  - `/api/v1/events` – nur `ROLE_ORGANIZER`
  - Alle anderen Endpunkte – authentifizierte Benutzer

---

## Datenbankmodell

| Tabelle              | Beschreibung                              |
| -------------------- | ----------------------------------------- |
| `users`              | Benutzer (aus Keycloak-JWT provisioniert) |
| `events`             | Events mit Status (DRAFT, PUBLISHED, …)   |
| `ticket_types`       | Ticket-Kategorien (Name, Preis, Kontingent) |
| `tickets`            | Gekaufte Tickets mit Status               |
| `qr_codes`           | QR-Codes (Base64-PNG) zu Tickets          |
| `ticket_validations` | Validierungsversuche mit Ergebnis         |
| `user_attending_events` | N:M-Beziehung User ↔ Event (Teilnehmer) |
| `user_staffing_events`  | N:M-Beziehung User ↔ Event (Personal)  |

---

## API-Endpunkte

### Events (Organizer)
| Methode | Pfad                              | Beschreibung               |
| ------- | --------------------------------- | -------------------------- |
| POST    | `/api/v1/events`                  | Event mit Ticket-Typen anlegen |
| PUT     | `/api/v1/events/{eventId}`        | Event aktualisieren        |
| GET     | `/api/v1/events`                  | Eigene Events auflisten    |
| GET     | `/api/v1/events/{eventId}`        | Event-Details abrufen      |
| DELETE  | `/api/v1/events/{eventId}`        | Event löschen              |

### Veröffentlichte Events (öffentlich)
| Methode | Pfad                                     | Beschreibung                          |
| ------- | ---------------------------------------- | ------------------------------------- |
| GET     | `/api/v1/published-events`               | Veröffentlichte Events (mit `?q=` Suche) |
| GET     | `/api/v1/published-events/{eventId}`     | Details eines veröffentlichten Events |

### Tickets (Benutzer)
| Methode | Pfad                                      | Beschreibung                |
| ------- | ----------------------------------------- | --------------------------- |
| GET     | `/api/v1/tickets`                         | Eigene Tickets auflisten    |
| GET     | `/api/v1/tickets/{ticketId}`              | Ticket-Details abrufen      |
| GET     | `/api/v1/tickets/{ticketId}/qr-codes`     | QR-Code-Bild abrufen        |
| POST    | `/api/v1/events/{eventId}/ticket-types/{ticketTypeId}/tickets` | Ticket kaufen |

### Validierung (Staff)
| Methode | Pfad                              | Beschreibung               |
| ------- | --------------------------------- | -------------------------- |
| POST    | `/api/v1/ticket-validations`      | Ticket validieren          |

---

## Installation & lokale Entwicklung

### Voraussetzungen
- Docker & Docker Compose
- JDK 26
- Maven (oder `./mvnw`)

### 1. Infrastruktur starten

```bash
docker compose up -d
```

Startet:
- **PostgreSQL** auf Port `5432`
- **Adminer** (DB-UI) auf Port `8888`
- **Keycloak** auf Port `9090` (Admin: `admin` / `admin`)

### 2. Keycloak konfigurieren

1. `http://localhost:9090` öffnen, mit `admin` / `admin` anmelden
2. Realm **event-ticket-platform** erstellen
3. Client (z. B. `event-ticket-client`) anlegen
4. Benutzer anlegen und Rollen (`ROLE_ORGANIZER`, `ROLE_STAFF`) zuweisen

### 3. Anwendung starten

```bash
./mvnw spring-boot:run
```

### 4. Tests ausführen

```bash
./mvnw test
```

---

## Technische Highlights (für potenzielle Arbeitgeber)

- **Pessimistic Locking** – `TicketTypeRepository.findByIdWithLock()` verwendet `PESSIMISTIC_WRITE`, um Overselling beim parallelen Ticketkauf auszuschließen
- **QR-Code-Validierung** – Validierung stellt sicher, dass ein Ticket nur einmal erfolgreich validiert werden kann (doppelte Validierung → `INVALID`)
- **PostgreSQL Full-Text Search** – Native SQL-Query mit `to_tsvector` / `plainto_tsquery` für performante Volltext-Suche über Events
- **User-Provisioning-Filter** – Automatische Synchronisation zwischen Keycloak und lokaler User-Tabelle via `OncePerRequestFilter`
- **MapStruct-Mapping** – Saubere Trennung von API-DTOs und Domänen-Entities zur Vermeidung von zirkulären Referenzen
- **Modulare Fehlerbehandlung** – Einheitliches Error-Handling mit benutzerdefinierten Exceptions, die von `EventTicketException` erben
- **Modernster Stack** – Spring Boot 4 + Java 26 + Jakarta EE (kein Legacy-Javax)
