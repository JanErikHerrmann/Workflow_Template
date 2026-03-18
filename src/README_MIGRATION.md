README_MIGRATION.md

Zweck
-----
Dieses kurze Dokument beschreibt, wie Sie das lokale PostgreSQL-Schema für das Projekt mit der vorhandenen SQL-Datei (`src/main/resources/db/migration/V1__init.sql` oder `src/main/resources/schema.sql`) anwenden.

Voraussetzungen
--------------
- PostgreSQL-Server läuft lokal (Port 5432)
- PostgreSQL Client (`psql`) installiert und in PATH oder Pfad bekannt
- Optional: Java & Maven, falls Sie Flyway über Maven nutzen wollen

Standard-Datenbank-Zugang (aus Ihrer Angabe)
-------------------------------------------
Host: localhost
Port: 5432
Database: MasterThesis_Workflow
Username: postgres
Password: postgres_postgres

Schnellbefehle (PowerShell)
---------------------------
Hinweis: Diese Befehle setzen temporär die Umgebungsvariable `PGPASSWORD`, damit `psql` das Passwort verwendet.

# Ausführen der mitgelieferten Migration (Standard-Datei V1__init.sql)
$env:PGPASSWORD = 'postgres_postgres'
psql -h localhost -p 5432 -U postgres -d MasterThesis_Workflow -f "C:\Users\jonas\IdeaProjects\Workflow_Template\src\main\resources\db\migration\V1__init.sql"

# Oder: Verwende das mitgelieferte Skript (empfohlen)
# Wechsle ins Projektverzeichnis und führe das Skript aus:
Set-Location "C:\Users\jonas\IdeaProjects\Workflow_Template\src\scripts"
.\apply-migration.ps1

# Prüfen, ob die Tabellen vorhanden sind
$env:PGPASSWORD = 'postgres_postgres'
psql -h localhost -p 5432 -U postgres -d MasterThesis_Workflow -c "\dt public.*"

Fehlerbehebung
---------------
- psql nicht gefunden: Installieren Sie PostgreSQL Client oder geben Sie den vollständigen Pfad zu `psql.exe` an (Parameter `-PsqlPath` des Skripts).
- `mvnw` startet nicht: Prüfen Sie `java -version` und `.
- Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser` falls PowerShell-Skripte blockiert sind.

Alternative: Flyway
-------------------
Wenn Sie Maven oder Flyway-CLI verwenden möchten, siehe unten (kurz):

- Flyway-CLI:
flyway -url="jdbc:postgresql://localhost:5432/MasterThesis_Workflow" -user=postgres -password=postgres_postgres -locations=filesystem:"C:\Users\jonas\IdeaProjects\Workflow_Template\src\main\resources\db\migration" migrate

- Maven mit Flyway-Plugin (falls `mvn`/`mvnw` funktioniert):
mvn -Dflyway.url=jdbc:postgresql://localhost:5432/MasterThesis_Workflow -Dflyway.user=postgres -Dflyway.password=postgres_postgres flyway:migrate

Sicherheits-Hinweis
-------------------
Speichern Sie Passwörter nicht dauerhaft in Skripten in Klartext für Produktions- oder geteilte Umgebungen. Für lokale Tests ist die temporäre Verwendung von `PGPASSWORD` akzeptabel.

Support
-------
Wenn Sie möchten, kann ich:
- das Skript anpassen (anderen Pfad, Logging, Backup etc.)
- Flyway-Maven-Schritte in `pom.xml` einfügen
- Hilfestellung beim Lösen des `mvnw`-Problems geben (Logs prüfen)


