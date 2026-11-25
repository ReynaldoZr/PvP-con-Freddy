# Proyecto de gestión clínica

Proyecto final de Programación de Aplicaciones de Escritorio. La aplicación usa JavaFX, Hibernate ORM y PostgreSQL.

## Iniciar el proyecto

1. Prepara JDK 21, Maven y PostgreSQL. Sigue la [configuración local](docs/CONFIGURACION.md) para crear la base `clinica`, ejecutar [base_clinica.sql](base_clinica.sql) y configurar `CLINICA_DB_URL`, `CLINICA_DB_USER` y `CLINICA_DB_PASSWORD`.
2. Desde la raíz del proyecto, inicia la aplicación:

   ```sh
   mvn javafx:run
   ```

   En IntelliJ IDEA también puedes ejecutar `clinica.Launcher` con las mismas variables de entorno.

## Demostración del flujo

Usa una base de prueba y datos ficticios. Para que el total del ejemplo sea exacto, el día elegido no debe tener otros cobros.

1. **Recepción:** registra a una paciente ficticia, por ejemplo, Ana Demo, y confirma que aparece en Pacientes.
2. **Recepción:** agenda dos citas para esa paciente en el mismo día, con el médico y consultorio disponibles. Usa horarios distintos, por ejemplo, 9:00 a. m. y 9:30 a. m.
3. **Médico:** inicia sesión con el médico asignado. Desde su agenda, atiende ambas citas y guarda un motivo, diagnóstico e indicaciones ficticios. Revisa el expediente de la paciente.
4. **Recepción:** abre Cobros desde cada cita atendida. Registra C$100.00 por la primera y C$150.00 por la segunda. Comprueba que un segundo cobro para la misma consulta se rechaza y que ambos pagos aparecen en el listado de la fecha elegida.
5. **Reporte diario:** sigue la [guía de JasperReports](README_JASPER.md). En Jaspersoft Studio Community 7.0.8, selecciona la misma fecha en el parámetro `fecha` y ejecuta **Preview**. Deben aparecer los dos cobros y un total de **C$250.00**.

La guía del reporte explica la vista previa en Jaspersoft Studio. La integración para abrirlo desde JavaFX queda pendiente del ejemplo del profesor, según `AGENTS.md`.
