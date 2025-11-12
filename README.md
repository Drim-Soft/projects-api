docker build -t planifika-projects .

docker run --env-file .env -p 8080:8080 planifika-projects

Ejecutar las pruebas EN AMBIENTE DE PRUEBAS:
python generate_test_report.py

Para ver el reporte:
   1. Abre test-report.html en tu navegador
   2. O ejecuta: python -m http.server 8000
      y luego abre: http://localhost:8000/test-report.html