@echo off
echo ========================================
echo Generador de Dashboard de Pruebas
echo ========================================
echo.

REM Verificar si Python está instalado
python --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Python no esta instalado o no esta en el PATH
    echo Por favor instala Python 3 desde https://www.python.org/
    pause
    exit /b 1
)

REM Ejecutar el script Python
python generate_test_report.py

if errorlevel 1 (
    echo.
    echo ERROR: Hubo un problema al generar el reporte
    pause
    exit /b 1
)

echo.
echo ========================================
echo Reporte generado exitosamente!
echo ========================================
echo.
echo Para ver el reporte:
echo   1. Abre test-report.html en tu navegador
echo   2. O ejecuta: python -m http.server 8000
echo      y luego abre: http://localhost:8000/test-report.html
echo.
pause

