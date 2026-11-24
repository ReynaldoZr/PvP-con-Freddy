# Issue #5: Expediente y cobros

Se reutiliza `JPAUtil.getEntityManager()` y la unidad de persistencia `clinicaPU`. No se cambia el esquema ni el pom.xml.
Las pantallas se abren desde la agenda usando las funciones existentes del menú. Expediente comprueba la asignación del médico; Cobros permite consultar por fecha desde el menú y registrar desde una cita. Se registran las entidades Consulta y Cobro en persistence.xml.

## Pruebas pendientes (datos ficticios)
1. Iniciar con las variables de entorno y usuarios de prueba de docs/CONFIGURACION.md.
2. Como recepción, agendar una cita ficticia. Intentar cobrar antes de atenderla: debe rechazarse.
3. Como médico asignado, abrir su agenda, seleccionar la cita y guardar motivo, diagnóstico e indicaciones. Editar y comprobar el historial, usuario y hora. Abrir otra cita del mismo paciente y comprobar el historial.
4. Como recepción, abrir Cobros desde la cita, registrar C$ 350.00 y volver a intentar: debe rechazarse. Registrar otro cobro de C$ 150.00 en otra cita atendida; comprobar listado y total C$ 500.00 si no hay otros cobros del día. Cambiar el filtro de fecha.
5. Comprobar que los roles no abren módulos ajenos.

JasperReports: conservar el diseño local para la entrega en rama y PR separados según AGENTS.md. Pendiente de confirmar el ejemplo de Freddy y probar la vista previa.

Estas pruebas no se han ejecutado todavía.
