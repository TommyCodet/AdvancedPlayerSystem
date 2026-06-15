# AdvancedPlayerSystem

Ein modernes Spielerverwaltungssystem für PaperMC 1.21.1.

## Features

* Spielerprofile
* Spielzeit-Tracking
* Economy-System
* Profil-GUI
* Join- und Leave-Nachrichten
* Konfigurierbare Nachrichten
* YAML- oder SQLite-Datenspeicherung
* Permissions-System
* Reload-Befehl

## Voraussetzungen

* Java 21
* PaperMC 1.21.1
* Maven 3.9+

## Installation

1. Lade die neueste `.jar` aus den Releases herunter.
2. Lege die Datei in den Ordner `plugins/`.
3. Starte den Server.
4. Passe die Konfiguration nach Bedarf an.

## Befehle

| Befehl                    | Beschreibung               |
| ------------------------- | -------------------------- |
| `/profile [Spieler]`      | Zeigt Spielerinformationen |
| `/balance [Spieler]`      | Zeigt Guthaben             |
| `/pay <Spieler> <Betrag>` | Überweist Geld             |
| `/aps reload`             | Konfiguration neu laden    |
| `/aps help`               | Hilfe anzeigen             |

## Permissions

| Permission                          | Beschreibung     |
| ----------------------------------- | ---------------- |
| `advancedplayersystem.profile`      | Profil ansehen   |
| `advancedplayersystem.balance`      | Guthaben ansehen |
| `advancedplayersystem.pay`          | Geld senden      |
| `advancedplayersystem.admin.reload` | Plugin neu laden |

## Build

Projekt lokal bauen:

```bash
mvn clean package
```

Die fertige Datei befindet sich anschließend unter:

```text
target/AdvancedPlayerSystem-x.x.x.jar
```

## Projektstruktur

```text
src/main/java
├── command
├── listener
├── gui
├── manager
├── data
├── util
└── AdvancedPlayerSystem.java
```

## Lizenz

MIT License
