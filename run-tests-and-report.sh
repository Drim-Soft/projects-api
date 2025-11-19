#!/bin/bash

echo "========================================"
echo "Ejecutando Pruebas y Generando Reporte"
echo "========================================"
echo ""

# Limpiar reportes anteriores (opcional)
echo "Limpiando reportes anteriores..."
rm -f target/surefire-reports/TEST-*.xml

echo ""
echo "========================================"
echo "Paso 1: Ejecutando pruebas con Maven..."
echo "========================================"
echo ""

# Ejecutar pruebas con Maven (continuar aunque haya fallos)
./mvnw test -Dmaven.test.failure.ignore=true

TEST_EXIT_CODE=$?
if [ $TEST_EXIT_CODE -ne 0 ]; then
    echo ""
    echo "ADVERTENCIA: Algunas pruebas fallaron, pero continuando con la generación del reporte..."
    echo ""
fi

echo ""
echo "========================================"
echo "Paso 2: Generando reporte HTML..."
echo "========================================"
echo ""

# Verificar si Python está instalado
if ! command -v python3 &> /dev/null; then
    if ! command -v python &> /dev/null; then
        echo "ERROR: Python no esta instalado"
        echo "Por favor instala Python 3"
        exit 1
    else
        PYTHON_CMD=python
    fi
else
    PYTHON_CMD=python3
fi

# Ejecutar el script Python
$PYTHON_CMD generate_test_report.py

if [ $? -ne 0 ]; then
    echo ""
    echo "ERROR: Hubo un problema al generar el reporte"
    exit 1
fi

echo ""
echo "========================================"
echo "Reporte generado exitosamente!"
echo "========================================"
if [ $TEST_EXIT_CODE -ne 0 ]; then
    echo ""
    echo "NOTA: El reporte incluye todas las pruebas, incluyendo las que fallaron."
    echo "      Revisa el reporte HTML para ver los detalles de los fallos."
fi
echo ""
echo "Para ver el reporte:"
echo "  1. Abre test-report.html en tu navegador"
echo "  2. O ejecuta: python3 -m http.server 8000"
echo "     y luego abre: http://localhost:8000/test-report.html"
echo ""

