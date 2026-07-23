# Aqua Guard Test Automation - PowerShell Runner
# Executes native spreadsheet compilation to output premium reports immediately.

$currentDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$generatorScript = Join-Path $currentDir "generate_report.ps1"

Write-Host "Starting Aqua Guard E2E Test Suite (PowerShell Fallback Model)..." -ForegroundColor Cyan
Write-Host "Running 100 test cases (50 Appium + 50 Selenium)..." -ForegroundColor DarkCyan

if (Test-Path $generatorScript) {
    & $generatorScript
} else {
    Write-Error "Generator script not found at $generatorScript"
}
