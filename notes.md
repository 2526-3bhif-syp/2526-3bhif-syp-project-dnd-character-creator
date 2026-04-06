[[SYP]]

**Kontext:** Wir arbeiten im Branch `character creation UI`. Ich muss für die Schule zwingend sehr kleinschrittig committen (immer nach ca. 20 bis 30 Zeilen Code).

**Datenbank-Info für diesen Schritt:** Hier ist der relevante Auszug aus unserer DDL für die Charakter-Attribute. Nutze diese Struktur als Basis:
``` SQL
CREATE TABLE ability_scores (
  "index" TEXT PRIMARY KEY,
  name TEXT,
  full_name TEXT,
  url TEXT
);

CREATE TABLE ability_scores_desc (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  ability_scores_index TEXT,
  order_num INTEGER,
  "value" TEXT,
  FOREIGN KEY (ability_scores_index) REFERENCES ability_scores("index")
);

```
_(Hinweis für die KI: Die Spieler sollen in der UI Werte wie 15, 14, 13, 12, 10, 8 auf diese `ability_scores` verteilen können)._

**Unsere gesamte To-Do-Liste:**

1. Schritt 3: Attribute (Stats) verteilen.
    
2. Schritt 4: Hintergrund & Fertigkeiten (Skills).
    
3. Schritt 5: Ausrüstung & Zauber.
    
4. Schritt 6: Charakter-Zusammenfassung (Dashboard).
    

**Deine aktuelle Aufgabe:** Bitte schreibe jetzt **NUR den Code für Schritt 3 (Attribute)**. **Ganz wichtig:** Teile deinen Code so auf, dass du mir immer nur Blöcke von maximal 20 bis 30 Zeilen gibst. Unterbrich deine Antwort nach jedem Block und frag mich, ob ich den Code committet habe. Erst wenn ich "weiter" sage, gibst du mir die nächsten 25 Zeilen für Schritt 3.