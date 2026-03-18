<#
apply-migration.ps1
Wendet eine SQL-Migrationsdatei (standard: src/main/resources/db/migration/V1__init.sql)
auf eine lokale PostgreSQL-Datenbank an.

Beispiele:
 .\apply-migration.ps1                         # verwendet die eingebetteten Defaults
 .\apply-migration.ps1 -SqlFile ..\main\resources\schema.sql -UseSchemaSql
 .\apply-migration.ps1 -Host db.example.com -Port 5432 -Database MyDB -Username dbuser -Password secret

#>
[CmdletBinding()]
param(
    [string]$Host = 'localhost',
    [int]$Port = 5432,
    [string]$Database = 'MasterThesis_Workflow',
    [string]$Username = 'postgres',
    [string]$Password = 'postgres_postgres',
    [string]$SqlFile = '',
    [string]$PsqlPath = '',
    [switch]$UseSchemaSql,
    [switch]$VerboseLog
)

function ExitWithError($message, $code = 1) {
    Write-Error $message
    exit $code
}

# Bestimme Standard-SQL-Datei, wenn nicht gesetzt
if (-not $SqlFile -or $SqlFile -eq '') {
    if ($UseSchemaSql) {
        $SqlFile = Join-Path $PSScriptRoot "..\main\resources\schema.sql"
    } else {
        $SqlFile = Join-Path $PSScriptRoot "..\main\resources\db\migration\V1__init.sql"
    }
}

# Auflösen des Pfads
try {
    $ResolvedSql = Resolve-Path -Path $SqlFile -ErrorAction Stop
    $SqlFileFull = $ResolvedSql.Path
} catch {
    ExitWithError "SQL-Datei nicht gefunden: $SqlFile"
}

if ($VerboseLog) { Write-Host "SQL-Datei: $SqlFileFull" }

# Bestimme psql-Binary
if ($PsqlPath -and (Test-Path $PsqlPath)) {
    $psql = $PsqlPath
} else {
    # Versuche psql im PATH
    try {
        $cmd = Get-Command psql -ErrorAction Stop
        $psql = $cmd.Source
    } catch {
        # Versuche übliche Installationspfade (Postgres). Nicht alle Versionen abdecken.
        $possible = @(
            "$env:ProgramFiles\PostgreSQL\14\bin\psql.exe",
            "$env:ProgramFiles\PostgreSQL\13\bin\psql.exe",
            "$env:ProgramFiles(x86)\PostgreSQL\13\bin\psql.exe",
            "$env:ProgramFiles\PostgreSQL\12\bin\psql.exe"
        )
        $found = $possible | Where-Object { Test-Path $_ } | Select-Object -First 1
        if ($found) { $psql = $found } else { ExitWithError "psql-Binary nicht gefunden. Bitte installieren Sie PostgreSQL-Client oder übergeben Sie -PsqlPath." }
    }
}

# Setze PGPASSWORD temporär
$oldPG = $env:PGPASSWORD
$env:PGPASSWORD = $Password

# Führe das SQL aus
Write-Host "Wende Migration an: $SqlFileFull auf $Username@$Host:$Port / DB: $Database"
$arguments = @(
    '-h', $Host,
    '-p', $Port.ToString(),
    '-U', $Username,
    '-d', $Database,
    '-f', $SqlFileFull
)

try {
    $proc = Start-Process -FilePath $psql -ArgumentList $arguments -NoNewWindow -Wait -PassThru -ErrorAction Stop
    if ($proc.ExitCode -eq 0) {
        Write-Host "Migration erfolgreich angewendet (ExitCode 0)."
        if ($VerboseLog) { Write-Host "psql Pfad: $psql" }
        # Rücksetzen
        if ($null -ne $oldPG) { $env:PGPASSWORD = $oldPG } else { Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue }
        exit 0
    } else {
        ExitWithError "psql beendete mit ExitCode $($proc.ExitCode)" $proc.ExitCode
    }
} catch {
    ExitWithError "Fehler beim Ausführen von psql: $($_.Exception.Message)"
} finally {
    # Stelle PGPASSWORD wieder her
    if ($null -ne $oldPG) { $env:PGPASSWORD = $oldPG } else { Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue }
}

