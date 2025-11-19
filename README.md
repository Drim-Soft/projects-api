docker build -t planifika-projects .

docker run --env-file .env -p 8080:8080 planifika-projects

## Ejecutar Pruebas y Generar Reporte

### Opción 1: Script Automático (Recomendado)

**Windows (PowerShell) - RECOMENDADO:**
```powershell
.\run-tests-and-report.ps1
```

**Windows (CMD o PowerShell):**
```cmd
.\run-tests-and-report.bat
```

**Linux/Mac/Git Bash:**
```bash
chmod +x run-tests-and-report.sh
./run-tests-and-report.sh
```

### Opción 2: Manual (Paso a Paso)

1. **Ejecutar las pruebas con Maven:**
   ```bash
   # Windows
   mvnw.cmd test
   
   # Linux/Mac
   ./mvnw test
   ```

2. **Generar el reporte HTML:**
   ```bash
   python generate_test_report.py
   ```

### Ver el Reporte
   1. Abre `test-report.html` en tu navegador
   2. O ejecuta: `python -m http.server 8000`
      y luego abre: http://localhost:8000/test-report.html

### Notas
- El script automático ejecuta las pruebas primero y luego genera el reporte
- Si solo quieres generar el reporte de pruebas ya ejecutadas, usa: `python generate_test_report.py`
- Los reportes XML se generan en: `target/surefire-reports/`