#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Generador de Dashboard HTML para Reportes de Pruebas Maven Surefire
Lee los archivos XML de target/surefire-reports/ y genera un dashboard HTML interactivo
"""

import os
import xml.etree.ElementTree as ET
from datetime import datetime
from pathlib import Path
import json

def parse_surefire_report(xml_file):
    """Parsea un archivo XML de reporte de Maven Surefire"""
    try:
        tree = ET.parse(xml_file)
        root = tree.getroot()
        
        suite_name = root.get('name', 'Unknown')
        total_tests = int(root.get('tests', 0))
        errors = int(root.get('errors', 0))
        failures = int(root.get('failures', 0))
        skipped = int(root.get('skipped', 0))
        time = float(root.get('time', 0))
        
        passed = total_tests - errors - failures - skipped
        
        testcases = []
        for testcase in root.findall('testcase'):
            test_name = testcase.get('name', 'Unknown')
            classname = testcase.get('classname', 'Unknown')
            test_time = float(testcase.get('time', 0))
            
            status = 'passed'
            error_message = ''
            error_type = ''
            
            if testcase.find('error') is not None:
                status = 'error'
                error_elem = testcase.find('error')
                error_message = error_elem.get('message', '')
                error_type = error_elem.get('type', '')
            elif testcase.find('failure') is not None:
                status = 'failure'
                failure_elem = testcase.find('failure')
                error_message = failure_elem.get('message', '')
                error_type = failure_elem.get('type', '')
            elif testcase.find('skipped') is not None:
                status = 'skipped'
            
            testcases.append({
                'name': test_name,
                'classname': classname,
                'status': status,
                'time': test_time,
                'error_message': error_message,
                'error_type': error_type
            })
        
        return {
            'suite_name': suite_name,
            'total_tests': total_tests,
            'passed': passed,
            'failures': failures,
            'errors': errors,
            'skipped': skipped,
            'time': time,
            'testcases': testcases
        }
    except Exception as e:
        print(f"Error parseando {xml_file}: {e}")
        return None

def generate_html_dashboard(reports, output_file='test-report.html'):
    """Genera un dashboard HTML con los reportes de pruebas"""
    
    # Calcular estadísticas totales
    total_tests = sum(r['total_tests'] for r in reports)
    total_passed = sum(r['passed'] for r in reports)
    total_failures = sum(r['failures'] for r in reports)
    total_errors = sum(r['errors'] for r in reports)
    total_skipped = sum(r['skipped'] for r in reports)
    total_time = sum(r['time'] for r in reports)
    
    success_rate = (total_passed / total_tests * 100) if total_tests > 0 else 0
    
    # Preparar datos para gráficos
    chart_data = {
        'passed': total_passed,
        'failures': total_failures,
        'errors': total_errors,
        'skipped': total_skipped
    }
    
    html = f"""<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard de Pruebas - Projects API</title>
    <style>
        * {{
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }}
        
        body {{
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }}
        
        .container {{
            max-width: 1400px;
            margin: 0 auto;
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            overflow: hidden;
        }}
        
        .header {{
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px;
            text-align: center;
        }}
        
        .header h1 {{
            font-size: 2.5em;
            margin-bottom: 10px;
        }}
        
        .header .timestamp {{
            opacity: 0.9;
            font-size: 1.1em;
        }}
        
        .stats-grid {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            padding: 30px;
            background: #f8f9fa;
        }}
        
        .stat-card {{
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
            transition: transform 0.3s;
        }}
        
        .stat-card:hover {{
            transform: translateY(-5px);
        }}
        
        .stat-card.passed {{
            border-left: 5px solid #28a745;
        }}
        
        .stat-card.failed {{
            border-left: 5px solid #dc3545;
        }}
        
        .stat-card.errors {{
            border-left: 5px solid #ffc107;
        }}
        
        .stat-card.skipped {{
            border-left: 5px solid #6c757d;
        }}
        
        .stat-card.total {{
            border-left: 5px solid #007bff;
        }}
        
        .stat-card.time {{
            border-left: 5px solid #17a2b8;
        }}
        
        .stat-label {{
            font-size: 0.9em;
            color: #6c757d;
            text-transform: uppercase;
            letter-spacing: 1px;
            margin-bottom: 10px;
        }}
        
        .stat-value {{
            font-size: 2.5em;
            font-weight: bold;
            color: #2c3e50;
        }}
        
        .stat-percentage {{
            font-size: 1.2em;
            margin-top: 10px;
            color: #28a745;
            font-weight: bold;
        }}
        
        .chart-container {{
            padding: 30px;
            background: white;
        }}
        
        .chart-title {{
            font-size: 1.5em;
            margin-bottom: 20px;
            color: #2c3e50;
        }}
        
        .progress-bar {{
            width: 100%;
            height: 40px;
            background: #e9ecef;
            border-radius: 20px;
            overflow: hidden;
            margin: 10px 0;
            position: relative;
        }}
        
        .progress-fill {{
            height: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: bold;
            transition: width 0.5s;
        }}
        
        .progress-fill.passed {{
            background: linear-gradient(90deg, #28a745, #20c997);
        }}
        
        .progress-fill.failed {{
            background: linear-gradient(90deg, #dc3545, #c82333);
        }}
        
        .progress-fill.errors {{
            background: linear-gradient(90deg, #ffc107, #e0a800);
        }}
        
        .progress-fill.skipped {{
            background: linear-gradient(90deg, #6c757d, #5a6268);
        }}
        
        .suites-section {{
            padding: 30px;
        }}
        
        .suites-title {{
            font-size: 1.8em;
            margin-bottom: 20px;
            color: #2c3e50;
        }}
        
        .suite-card {{
            background: #f8f9fa;
            border-radius: 15px;
            padding: 25px;
            margin-bottom: 20px;
            border-left: 5px solid #667eea;
        }}
        
        .suite-header {{
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
            flex-wrap: wrap;
        }}
        
        .suite-name {{
            font-size: 1.3em;
            font-weight: bold;
            color: #2c3e50;
        }}
        
        .suite-stats {{
            display: flex;
            gap: 15px;
            flex-wrap: wrap;
        }}
        
        .suite-stat {{
            padding: 8px 15px;
            border-radius: 20px;
            font-size: 0.9em;
            font-weight: bold;
        }}
        
        .suite-stat.passed {{
            background: #d4edda;
            color: #155724;
        }}
        
        .suite-stat.failed {{
            background: #f8d7da;
            color: #721c24;
        }}
        
        .suite-stat.errors {{
            background: #fff3cd;
            color: #856404;
        }}
        
        .suite-stat.skipped {{
            background: #e2e3e5;
            color: #383d41;
        }}
        
        .testcases-table {{
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }}
        
        .testcases-table th {{
            background: #667eea;
            color: white;
            padding: 12px;
            text-align: left;
            font-weight: 600;
        }}
        
        .testcases-table td {{
            padding: 12px;
            border-bottom: 1px solid #e9ecef;
        }}
        
        .testcases-table tr:hover {{
            background: #f8f9fa;
        }}
        
        .status-badge {{
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 0.85em;
            font-weight: bold;
            display: inline-block;
        }}
        
        .status-badge.passed {{
            background: #d4edda;
            color: #155724;
        }}
        
        .status-badge.failure {{
            background: #f8d7da;
            color: #721c24;
        }}
        
        .status-badge.error {{
            background: #fff3cd;
            color: #856404;
        }}
        
        .status-badge.skipped {{
            background: #e2e3e5;
            color: #383d41;
        }}
        
        .error-details {{
            margin-top: 10px;
            padding: 15px;
            background: #fff3cd;
            border-left: 4px solid #ffc107;
            border-radius: 5px;
            font-family: 'Courier New', monospace;
            font-size: 0.9em;
            color: #856404;
        }}
        
        .error-details strong {{
            display: block;
            margin-bottom: 5px;
        }}
        
        .summary {{
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }}
        
        .summary h2 {{
            font-size: 2em;
            margin-bottom: 15px;
        }}
        
        .summary-text {{
            font-size: 1.2em;
            opacity: 0.95;
        }}
        
        @media (max-width: 768px) {{
            .stats-grid {{
                grid-template-columns: 1fr;
            }}
            
            .suite-header {{
                flex-direction: column;
                align-items: flex-start;
            }}
            
            .testcases-table {{
                font-size: 0.85em;
            }}
        }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Dashboard de Pruebas</h1>
            <div class="timestamp">Generado el {datetime.now().strftime('%d/%m/%Y %H:%M:%S')}</div>
        </div>
        
        <div class="stats-grid">
            <div class="stat-card total">
                <div class="stat-label">Total de Pruebas</div>
                <div class="stat-value">{total_tests}</div>
            </div>
            
            <div class="stat-card passed">
                <div class="stat-label">Exitosas</div>
                <div class="stat-value">{total_passed}</div>
                <div class="stat-percentage">{success_rate:.1f}%</div>
            </div>
            
            <div class="stat-card failed">
                <div class="stat-label">Fallidas</div>
                <div class="stat-value">{total_failures}</div>
            </div>
            
            <div class="stat-card errors">
                <div class="stat-label">Errores</div>
                <div class="stat-value">{total_errors}</div>
            </div>
            
            <div class="stat-card skipped">
                <div class="stat-label">Omitidas</div>
                <div class="stat-value">{total_skipped}</div>
            </div>
            
            <div class="stat-card time">
                <div class="stat-label">Tiempo Total</div>
                <div class="stat-value">{total_time:.2f}s</div>
            </div>
        </div>
        
        <div class="chart-container">
            <div class="chart-title">Distribución de Resultados</div>
            <div class="progress-bar">
                <div class="progress-fill passed" style="width: {total_passed/total_tests*100 if total_tests > 0 else 0}%">
                    {total_passed} Exitosas
                </div>
            </div>
            <div class="progress-bar">
                <div class="progress-fill failed" style="width: {total_failures/total_tests*100 if total_tests > 0 else 0}%">
                    {total_failures} Fallidas
                </div>
            </div>
            <div class="progress-bar">
                <div class="progress-fill errors" style="width: {total_errors/total_tests*100 if total_tests > 0 else 0}%">
                    {total_errors} Errores
                </div>
            </div>
            <div class="progress-bar">
                <div class="progress-fill skipped" style="width: {total_skipped/total_tests*100 if total_tests > 0 else 0}%">
                    {total_skipped} Omitidas
                </div>
            </div>
        </div>
        
        <div class="suites-section">
            <div class="suites-title">Detalles por Suite de Pruebas</div>
"""
    
    # Agregar cada suite
    for report in reports:
        suite = report['suite_name']
        suite_html = f"""
            <div class="suite-card">
                <div class="suite-header">
                    <div class="suite-name">{suite}</div>
                    <div class="suite-stats">
                        <span class="suite-stat passed">{report['passed']} Exitosas</span>
                        <span class="suite-stat failed">{report['failures']} Fallidas</span>
                        <span class="suite-stat errors">{report['errors']} Errores</span>
                        <span class="suite-stat skipped">{report['skipped']} Omitidas</span>
                        <span class="suite-stat" style="background: #d1ecf1; color: #0c5460;">{report['time']:.2f}s</span>
                    </div>
                </div>
                
                <table class="testcases-table">
                    <thead>
                        <tr>
                            <th>Prueba</th>
                            <th>Estado</th>
                            <th>Tiempo</th>
                        </tr>
                    </thead>
                    <tbody>
"""
        
        for testcase in report['testcases']:
            status_class = testcase['status']
            status_text = {
                'passed': 'Exitoso',
                'failure': 'Fallido',
                'error': 'Error',
                'skipped': 'Omitido'
            }.get(status_class, status_class)
            
            suite_html += f"""
                        <tr>
                            <td>
                                <strong>{testcase['name']}</strong><br>
                                <small style="color: #6c757d;">{testcase['classname']}</small>
"""
            
            if testcase['error_message']:
                suite_html += f"""
                                <div class="error-details">
                                    <strong>{testcase['error_type']}</strong>
                                    {testcase['error_message']}
                                </div>
"""
            
            suite_html += f"""
                            </td>
                            <td><span class="status-badge {status_class}">{status_text}</span></td>
                            <td>{testcase['time']:.3f}s</td>
                        </tr>
"""
        
        suite_html += """
                    </tbody>
                </table>
            </div>
"""
        html += suite_html
    
    # Resumen final
    html += f"""
        </div>
        
        <div class="summary">
            <h2>{"Todas las pruebas pasaron" if total_failures == 0 and total_errors == 0 else "Hay pruebas fallidas o con errores"}</h2>
            <div class="summary-text">
                {total_passed} de {total_tests} pruebas exitosas ({success_rate:.1f}%)
            </div>
        </div>
    </div>
</body>
</html>
"""
    
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(html)
    
    return output_file

def main():
    """Función principal"""
    # Buscar archivos XML de reportes
    reports_dir = Path('target/surefire-reports')
    
    if not reports_dir.exists():
        print(f"ERROR: No se encontro el directorio {reports_dir}")
        print("   Asegurate de ejecutar las pruebas primero con: mvn test o .\\mvnw.cmd test")
        return
    
    xml_files = list(reports_dir.glob('TEST-*.xml'))
    
    if not xml_files:
        print(f"ERROR: No se encontraron archivos de reporte XML en {reports_dir}")
        print("   Asegurate de ejecutar las pruebas primero con: mvn test o .\\mvnw.cmd test")
        return
    
    print(f"Procesando {len(xml_files)} archivo(s) de reporte...")
    
    reports = []
    for xml_file in xml_files:
        print(f"   Leyendo {xml_file.name}...")
        report = parse_surefire_report(xml_file)
        if report:
            reports.append(report)
    
    if not reports:
        print("ERROR: No se pudieron parsear los reportes")
        return
    
    # Generar dashboard HTML
    output_file = 'test-report.html'
    print(f"\nGenerando dashboard HTML...")
    generate_html_dashboard(reports, output_file)
    
    print(f"\nDashboard generado exitosamente: {output_file}")
    print(f"\nPara ver el reporte:")
    print(f"   1. Abre {output_file} en tu navegador")
    print(f"   2. O ejecuta: python -m http.server 8000")
    print(f"      y luego abre: http://localhost:8000/{output_file}")

if __name__ == '__main__':
    main()

