# Reglas de trabajo del proyecto de la clínica

## Antes de programar

1. Lee la consigna, la propuesta del equipo y la práctica de clase que corresponde a tu tarea. Si algo cambia el alcance o la consigna es ambigua, consulta al equipo.
2. Empieza por el flujo de Pacientes para acordar un patrón de formulario, tabla y conexión. Usa ese patrón en Citas, Expediente y Cobros. Acuerda desde el inicio qué puede hacer Recepción y qué puede hacer el Médico.
3. Usa los ejemplos del profesor y las herramientas que ya practicaron. La base de referencia es JavaFX con FXML, Scene Builder, controladores, Hibernate ORM y PostgreSQL. Sigue las versiones, la estructura y los nombres que acuerde el equipo. Cambios de dependencias, versiones, estructura o tablas fuera de tu tarea se consultan antes de hacerlos.

## Una tarea, una rama y un Pull Request

1. Crea la rama de trabajo a partir de la versión vigente de main. Usa un nombre que describa la tarea, por ejemplo, feat/busqueda-pacientes.
2. Limita los cambios a la tarea. Si ves otra mejora, propónla aparte. Aplica el mismo flujo al código Java, FXML, SQL y documentación.
3. Prueba el caso normal y una validación relacionada con el cambio. Usa datos ficticios. Mantén las credenciales fuera del repositorio.
4. Abre el Pull Request con destino a main. En la descripción indica qué cambió, qué práctica usaste, cómo reproducir la prueba y qué quedó pendiente de comprobar. Si cambiaste la base de datos, indica cómo aplicar el cambio en una base de prueba.
5. Cualquier compañero distinto del autor puede revisar y aprobar el PR. Revisa el alcance y reproduce la prueba antes de aprobar. Atiende las observaciones en la misma rama. Integra a main después de la aprobación y con las observaciones resueltas. El trabajo del equipo entra por PR, sin commits directos a main.

## Cómo guardar y recuperar versiones

- Etiqueta con un tag de Git los puntos estables de main que ya se probaron, usando números como v0.1.0 y v0.2.0. Describe brevemente qué funciona en esa versión. Etiqueta avances que valga la pena poder recuperar.
- Para probar una versión anterior, abre una rama a partir de su tag. Si hay que deshacer un cambio en main, prepara una reversión en una rama y pásala por un PR revisado. Conserva el historial de main.
- Si una versión depende de cambios en PostgreSQL, documenta qué scripts necesita. Una etiqueta de Git identifica el código y sus scripts. Los datos de una base se recuperan por separado.

## Instrucciones para usar IA

- Antes de editar, identifica la tarea, la práctica que vas a seguir y los archivos que necesitas tocar.
- Aplica el ejemplo de clase con el cambio más pequeño que resuelva la tarea. Consulta al equipo si necesitas salir de ese alcance, cambiar el esquema fuera de lo acordado o usar algo que no aparece en la práctica.
- Escribe código que quien hizo el PR pueda explicar. Usa nombres claros y comenta lo que ayude a entender una decisión o validación.
- Al terminar, resume qué editaste, cómo se prueba y qué quedó sin verificar. El autor del PR debe entender el cambio y comprobarlo antes de pedir revisión.

## JasperReports

JasperReports forma parte de la entrega. Empieza con un solo reporte sencillo de cobros del día. Usa datos ficticios y los campos que ya tiene ese módulo. La práctica puede avanzar junto con los demás módulos.

1. Pide a Freddy López el enlace o el archivo de su ejemplo. Cuando esté disponible, agrega el enlace aquí, ejecútalo y sigue su forma de cargar los datos y mostrar el reporte en JavaFX.
2. Mientras llega el ejemplo, practica en Jaspersoft Studio con una tabla ficticia de cobros. El reporte debe mostrar fecha, concepto, importe y total del día. Guarda el diseño y comprueba la vista previa. Si el ejemplo sigue sin estar disponible cuando toque integrar, consulta a Freddy qué conexión y forma de mostrar el reporte acepta para ajustar la práctica.
3. Integra el reporte en su propia rama y PR hacia main. En el PR identifica la referencia usada y deja los pasos para comprobar el listado y el total con datos ficticios.

Referencia disponible: [Software de la asignatura](https://uamvirtual.uam.edu.ni/grado/mod/url/view.php?id=955677) y [carpeta del profesor con Jaspersoft Studio Community 7.0.8](https://drive.google.com/drive/folders/1JFigs622vkpB6zLocOjjv4U3eN3DFxCZ?usp=sharing). El enlace al ejemplo práctico queda pendiente.

## Referencias del profesor en UAM Virtual

Entra con tu cuenta UAM. Los enlaces abren los recursos originales del curso.

| Para qué sirve | Material |
| --- | --- |
| Revisar el alcance del proyecto | [Syllabus del grupo 3](https://uamvirtual.uam.edu.ni/grado/mod/resource/view.php?id=958659) y [propuesta entregada por el equipo](https://uamvirtual.uam.edu.ni/grado/mod/assign/view.php?id=1040755). |
| Armar pantallas y formularios | Semana 2: [Diseño Form 1](https://uamvirtual.uam.edu.ni/grado/mod/resource/view.php?id=970607), [Diseño Form 2](https://uamvirtual.uam.edu.ni/grado/mod/resource/view.php?id=970698) y [Scene Builder](https://uamvirtual.uam.edu.ni/grado/mod/url/view.php?id=970647). |
| Cargar datos en una tabla y seguir el estilo de clase | Semana 3: [Form 1 - TableView](https://uamvirtual.uam.edu.ni/grado/mod/resource/view.php?id=979145) y [Convenciones en JavaFX](https://uamvirtual.uam.edu.ni/grado/mod/resource/view.php?id=980944). |
| Conectar JavaFX con PostgreSQL (Hibernate ORM) | Semana 12: [Introducción a Hibernate ORM](https://uamvirtual.uam.edu.ni/grado/mod/resource/view.php?id=1034873) y [Arquitectura de Hibernate/JavaFX](https://uamvirtual.uam.edu.ni/grado/mod/resource/view.php?id=1034876). (Referencia base de PostgreSQL en Semanas 6 y 7: [Práctica de conexión a BD](https://uamvirtual.uam.edu.ni/grado/mod/forum/view.php?id=1000429) y [Guía práctica JavaFX y PostgreSQL](https://uamvirtual.uam.edu.ni/grado/mod/resource/view.php?id=1004489)). |
| Guardar cambios y proteger la integridad de las tablas | Semana 8: [PostgreSQL + CRUD](https://uamvirtual.uam.edu.ni/grado/mod/resource/view.php?id=1012686) y [CONSTRAINTS de una tabla](https://uamvirtual.uam.edu.ni/grado/mod/page/view.php?id=1012687). |
| Construir la primera pantalla y reutilizar el patrón | Semana 9: [Configuraciones Iniciales](https://uamvirtual.uam.edu.ni/grado/mod/page/view.php?id=1017522), [Código de CRUD](https://uamvirtual.uam.edu.ni/grado/mod/page/view.php?id=1018068), [Búsqueda parametrizada](https://uamvirtual.uam.edu.ni/grado/mod/page/view.php?id=1018087) y [Actividad práctica de CRUD y búsqueda](https://uamvirtual.uam.edu.ni/grado/mod/forum/view.php?id=1017520). |
| Consultar otros temas si la consigna los pide | Semana 10: [Concurrencia en Java](https://uamvirtual.uam.edu.ni/grado/mod/resource/view.php?id=1024205). Semana 12: [Introducción a Hibernate ORM](https://uamvirtual.uam.edu.ni/grado/mod/resource/view.php?id=1034873) y [Arquitectura de Hibernate/JavaFX](https://uamvirtual.uam.edu.ni/grado/mod/resource/view.php?id=1034876). Semana 14: [Servicios web, HTTP y JSON](https://uamvirtual.uam.edu.ni/grado/mod/resource/view.php?id=1046244), [Servicios web, JSON y JavaFX](https://uamvirtual.uam.edu.ni/grado/mod/resource/view.php?id=1046245) y [Postman](https://uamvirtual.uam.edu.ni/grado/mod/url/view.php?id=1048118). |
