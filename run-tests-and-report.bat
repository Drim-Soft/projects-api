@echo off
echo ========================================
echo Ejecutando Pruebas y Generando Reporte
echo ========================================
echo.

REM Limpiar reportes anteriores (opcional)
echo Limpiando reportes anteriores...
if exist target\surefire-reports\TEST-*.xml (
    del /Q target\surefire-reports\TEST-*.xml
)

echo.
echo ========================================
echo Paso 1: Ejecutando pruebas con Maven...
echo ========================================
echo.

REM Ejecutar pruebas con Maven (continuar aunque haya fallos)
call mvnw.cmd test -Dmaven.test.failure.ignore=true

if errorlevel 1 (
    echo.
    echo ADVERTENCIA: Algunas pruebas fallaron, pero continuando con la generacion del reporte...
    echo.
)

echo.
echo ========================================
echo Paso 2: Generando reporte HTML...
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
if errorlevel 1 (
    echo.
    echo NOTA: El reporte incluye todas las pruebas, incluyendo las que fallaron.
    echo       Revisa el reporte HTML para ver los detalles de los fallos.
)
echo.
echo Para ver el reporte:
echo   1. Abre test-report.html en tu navegador
echo   2. O ejecuta: python -m http.server 8000
echo      y luego abre: http://localhost:8000/test-report.html
echo.
pause

