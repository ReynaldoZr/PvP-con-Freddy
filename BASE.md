# Base compartida del proyecto de clínica

Lee [AGENTS.md](AGENTS.md) antes de trabajar. Esta base fija los nombres que comparten los módulos. Los controladores y pantallas se crean en las ramas de cada tarea.

## Aplicación

- Trabajamos en un único proyecto Maven con JavaFX, FXML, PostgreSQL e Hibernate ORM. Roberto agrega la configuración del proyecto y la conexión con Hibernate siguiendo las prácticas [Introducción a Hibernate ORM](https://uamvirtual.uam.edu.ni/grado/mod/resource/view.php?id=1034873) y [Arquitectura de Hibernate/JavaFX](https://uamvirtual.uam.edu.ni/grado/mod/resource/view.php?id=1034876).
- Paquete raíz: clinica. Código en src/main/java/clinica y vistas en src/main/resources/clinica. Nombres de módulos: acceso, pacientes, citas, expediente y cobros.
- Variables locales: CLINICA_DB_URL (por ejemplo jdbc:postgresql://localhost:5432/clinica), CLINICA_DB_USER y CLINICA_DB_PASSWORD. Configúralas en el entorno de ejecución (Hibernate las usará para conectarse a PostgreSQL). Usa consultas parametrizadas (HQL/JPQL) y datos ficticios.
- Recepción abre Pacientes, Citas y Cobros. Médico abre su agenda y el Expediente. Pasa el ID de la cita al abrir Expediente o Cobros.

## PostgreSQL

Crea una base local vacía llamada clinica y ejecuta base_clinica.sql dentro de ella. El script crea tablas y dos consultorios de prueba. Roberto documenta cómo cargar usuarios de prueba con su implementación de login.

| Módulo | Tablas y contrato |
| --- | --- |
| Acceso | usuarios contiene usuario, clave_hash, nombre y rol. Los roles son RECEPCION y MEDICO. Para elegir médico, consulta usuarios con rol MEDICO. |
| Pacientes | pacientes contiene nombres, apellidos, fecha_nacimiento y telefono. Busca por nombres y apellidos. |
| Citas | citas relaciona paciente_id, medico_id y consultorio_id con fecha y hora de inicio. Usa bloques fijos en :00 y :30. Solo puede haber una cita por médico y una por consultorio en cada bloque. |
| Expediente | consultas registra una atención por cita. La fila guarda motivo, diagnostico, indicaciones, actualizado_por y actualizado_en. Actualiza los dos últimos campos al editar. El historial del paciente sale de consultas unida a citas. |
| Cobros | cobros registra una fila por consulta atendida. El reporte diario filtra por la fecha de registrado_en y suma importe para mostrar el total en córdobas. |

## Orden para integrar

1. Roberto prepara conexión, login y menú.
2. Alex entrega Pacientes y Citas. Roger puede avanzar con el contrato de tablas y coordinar el paso del ID de cita con Alex.
3. Roger entrega Expediente y Cobros. El reporte JasperReports entra por separado según AGENTS.md.
4. Reynaldo prueba el flujo completo y deja el ejemplo de demostración en el README.

Cada cambio entra por Pull Request hacia main y requiere revisión de otro integrante.
