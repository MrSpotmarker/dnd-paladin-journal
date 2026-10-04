# 🛡️ D&D Paladin (2024) Character-Sheet Android App

Eine native Android-App zur Verwaltung eines Paladins basierend auf den neuen **D&D 2024 Core Rules (D&D 5.2 / 5.5e)**.

---

## ✨ Features

1. **Paladin 2024 Regelmechaniken**:
   - **Zaubern ab Level 1**: Spellcasting beginnt sofort auf Stufe 1 mit 2 Zauberslots und 4 vorbereiteten Zaubern.
   - **Weapon Masteries**: Vollständige Unterstützung der 2024er Meisterschaften (*Sap, Cleave, Push, Slow, Topple, Vex, Graze, Nick*) für angelegte Waffen.
   - **Paladin's Smite**: Integrierter Hinweis & Referenz für den überarbeiteten Bonus-Aktions-Spruch.
   - **Lay on Hands**: Pool ($5 \times \text{Level}$), verbraucht nur noch eine Bonus-Aktion; Schnellbutton zur Zustandsheilung (kostet 5 HP).
   - **Aura of Protection**: Automatische Addition des Charisma-Modifikators auf **alle** Rettungswürfe ab Stufe 6.
   - **Channel Divinity**: Verwaltung ab Stufe 3 (2 Ladungen, ab Stufe 11: 3 Ladungen).

2. **Live-Ressourcentracking & Rasten**:
   - **Hit Points & Temporary HP**: Eingehender Schaden wird automatisch zuerst von Temp HP abgezogen.
   - **Kurze Rast (Short Rest)**: Dialog zum Ausgeben von Trefferwürfeln ($1d10 + \text{CON}$) mit automatischer Regeneration von **1 Channel Divinity-Ladung** (2024 Regel).
   - **Lange Rast (Long Rest)**: Vollständige Erholung von HP, Spell Slots, Lay on Hands, Channel Divinity, Rücksetzung von Temp HP und halber Hit-Dice-Pool-Regeneration.

3. **DM-Override-System ("House Rules")**:
   - Jede automatische Berechnung (AC, HP Max, Attribute, Rettungswürfe, Spell DC) kann manuell überschrieben oder mit spontanen Zusatzboni versehen werden.
   - Dezent hervorgehobener Status für modifizierte Werte und 1-Klick-Reset auf offizielle Standardregeln.

4. **Inventar & Dynamische Effekte**:
   - Rüstungsklassen-Formeln (Plattenrüstung AC 18 flat, Mittlere Rüstung mit DEX max 2, Schild +2 AC).
   - Magische Effekte (z. B. *Gauntlets of Ogre Power* $\rightarrow$ STR 19, *Ring des Schutzes* $\rightarrow$ +1 AC & +1 Saves).
   - Waffenschaden, Gewichts- und Tragekapazitätsberechnung ($15 \times \text{STR}$ lbs).

5. **Rechtskonformes Zauberbuch & SRD 5.2.1**:
   - Alle Standard-Zauber und Gegenstände stammen aus dem offiziellen, freien **SRD 5.2.1 (CC-BY-4.0)**.
   - Editor für eigene Nicht-SRD-Inhalte (z. B. zusätzliche PHB-Subklassen wie *Oath of Vengeance* oder private Homebrews).
   - Vorbereitungszähler mit Validierung gegen das Stufenlimit.

6. **100 % Lokale Daten & Backup**:
   - Offline-First ohne Accountzwang.
   - Vollständiger JSON-Export und Import über die Zwischenablage oder Datei.

---

## 🚀 Projekt öffnen & starten

### Mit Android Studio:
1. Öffne Android Studio.
2. Wähle **Open** und navigiere zu diesem Verzeichnis (`/Users/joachim/workspace/paladin-app`).
3. Warte, bis der Gradle-Sync abgeschlossen ist.
4. Starte die App auf einem verbundenen Android-Gerät oder Emulator (erfordert minSdk 26 / Android 8.0+).

### Projektstruktur:
```
app/src/main/
├── assets/
│   └── srd_catalog.json             # Vorbefüllter CC-BY 5.2.1 Datenkatalog
├── java/com/paladin/app/
│   ├── MainActivity.kt              # NavigationBar & App Scaffold
│   ├── data/
│   │   └── CharacterRepository.kt   # JSON & Catalog Datenhaltung
│   ├── engine/
│   │   ├── CharacterStatsEngine.kt  # 2024 Paladin Berechnungslogik
│   │   └── RestService.kt           # Short & Long Rest Mechaniken
│   ├── model/
│   │   ├── Ability.kt               # Attribute & Modifikatoren
│   │   ├── Item.kt                  # Items, Masteries & Effekte
│   │   ├── Spell.kt                 # Zauber & Slot-Modelle
│   │   ├── DmOverrides.kt           # Hausregel-Overrides
│   │   └── CharacterSheet.kt        # Gesamter Charakterbogen
│   └── ui/
│       ├── CharacterViewModel.kt    # State & Aktionen
│       ├── theme/                   # Paladin Dark-Fantasy Theme
│       ├── components/              # Health-, Rest- & Override-Dialoge
│       └── screens/                 # Combat, Waffen, Inventar, Zauber, Stufe/Backup
└── res/                             # Android Manifest, Strings & Themes
```
