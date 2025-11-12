#!/bin/bash

echo "========================================"
echo "Generador de Dashboard de Pruebas"
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
echo ""
echo "Para ver el reporte:"
echo "  1. Abre test-report.html en tu navegador"
echo "  2. O ejecuta: python3 -m http.server 8000"
echo "     y luego abre: http://localhost:8000/test-report.html"
echo ""

