Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Ejecutando Pruebas y Generando Reporte" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Limpiar reportes anteriores (opcional)
Write-Host "Limpiando reportes anteriores..." -ForegroundColor Yellow
if (Test-Path "target\surefire-reports\TEST-*.xml") {
    Remove-Item "target\surefire-reports\TEST-*.xml" -Force
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Paso 1: Ejecutando pruebas con Maven..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Ejecutar pruebas con Maven (continuar aunque haya fallos)
& .\mvnw.cmd test "-Dmaven.test.failure.ignore=true"

$testExitCode = $LASTEXITCODE
if ($testExitCode -ne 0) {
    Write-Host ""
    Write-Host "ADVERTENCIA: Algunas pruebas fallaron, pero continuando con la generación del reporte..." -ForegroundColor Yellow
    Write-Host ""
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Paso 2: Generando reporte HTML..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar si Python está instalado
try {
    $pythonVersion = python --version 2>&1
    Write-Host "Python encontrado: $pythonVersion" -ForegroundColor Green
} catch {
    Write-Host "ERROR: Python no esta instalado o no esta en el PATH" -ForegroundColor Red
    Write-Host "Por favor instala Python 3 desde https://www.python.org/" -ForegroundColor Red
    Read-Host "Presiona Enter para salir"
    exit 1
}

# Ejecutar el script Python
python generate_test_report.py

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "ERROR: Hubo un problema al generar el reporte" -ForegroundColor Red
    Read-Host "Presiona Enter para salir"
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Reporte generado exitosamente!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
if ($testExitCode -ne 0) {
    Write-Host ""
    Write-Host "NOTA: El reporte incluye todas las pruebas, incluyendo las que fallaron." -ForegroundColor Yellow
    Write-Host "      Revisa el reporte HTML para ver los detalles de los fallos." -ForegroundColor Yellow
}
Write-Host ""
Write-Host "Para ver el reporte:" -ForegroundColor Yellow
Write-Host "  1. Abre test-report.html en tu navegador" -ForegroundColor White
Write-Host "  2. O ejecuta: python -m http.server 8000" -ForegroundColor White
Write-Host "     y luego abre: http://localhost:8000/test-report.html" -ForegroundColor White
Write-Host ""
Read-Host "Presiona Enter para salir"

