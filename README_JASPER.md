# JasperReports — Cobros del día

Archivo: `src/main/resources/reportes/cobros_del_dia.jrxml`

Abrir en Jaspersoft Studio Community 7.0.8. Crear un adaptador de datos JDBC para PostgreSQL y conectarlo a la base `clinica`. El reporte espera el parámetro `fecha` de tipo `java.sql.Date`; escoger un día que tenga cobros ficticios y ejecutar Preview.

El resultado incluye fecha, concepto, importe de cada cobro y la suma del día. El total se calcula con una variable `Sum` sobre el mismo conjunto de filas filtrado por fecha.

La integración del botón JavaFX para visualizar el reporte queda pendiente del ejemplo de Freddy, como indica `AGENTS.md`. No se agregó una forma de carga distinta por cuenta propia.
