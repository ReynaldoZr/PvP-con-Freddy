# Configuración local del proyecto (Maven, JavaFX, Hibernate y PostgreSQL)

## 1. Requisitos

- **JDK 21** (configurado en IntelliJ IDEA en *Project Structure -> Project SDK*).
- **Maven** (puede usarse el Maven integrado de IntelliJ IDEA).
- **PostgreSQL** en ejecución local.

## 2. Base de datos PostgreSQL

1. Crea una base de datos vacía llamada `clinica`:
   ```sql
   CREATE DATABASE clinica;
   ```
2. Ejecuta el script [base_clinica.sql](../base_clinica.sql) dentro de la base `clinica` (desde `psql` o pgAdmin). El script crea las tablas y dos consultorios de prueba.

## 3. Variables de entorno

Hibernate toma la conexión desde las variables definidas en [BASE.md](../BASE.md), sin guardar credenciales en el repositorio:

- `CLINICA_DB_URL`: `jdbc:postgresql://localhost:5432/clinica`
- `CLINICA_DB_USER`: tu usuario local de PostgreSQL (por ejemplo `postgres`)
- `CLINICA_DB_PASSWORD`: tu contraseña local de PostgreSQL

### En IntelliJ IDEA
1. Abre **Run -> Edit Configurations...**
2. En la configuración de ejecución de `clinica.Launcher` (o Maven `javafx:run`), agrega en **Environment variables**:
   `CLINICA_DB_URL=jdbc:postgresql://localhost:5432/clinica;CLINICA_DB_USER=postgres;CLINICA_DB_PASSWORD=TU_CLAVE_LOCAL`

### En PowerShell
```powershell
$env:CLINICA_DB_URL = "jdbc:postgresql://localhost:5432/clinica"
$env:CLINICA_DB_USER = "postgres"
$env:CLINICA_DB_PASSWORD = "TU_CLAVE_LOCAL"
```

## 4. Crear cuentas de prueba locales (Recepción y Médico)

Para no guardar contraseñas en el repositorio, genera tus usuarios locales ejecutando `clinica.acceso.RegistrarUsuarioPrueba` (por ejemplo, desde IntelliJ IDEA pasando los argumentos del programa o con Maven):

```powershell
# Cuenta de Recepción
mvn -q compile exec:java -Dexec.mainClass="clinica.acceso.RegistrarUsuarioPrueba" -Dexec.args="recepcion1 ClaveFicticia1! 'Ana Lopez' RECEPCION"

# Cuenta de Médico
mvn -q compile exec:java -Dexec.mainClass="clinica.acceso.RegistrarUsuarioPrueba" -Dexec.args="medico1 ClaveFicticia2! 'Dr. Carlos Ruiz' MEDICO"
```

## 5. Ejecutar y probar

1. Ejecuta la clase `clinica.Launcher` desde IntelliJ IDEA (o `mvn javafx:run`).
2. Prueba el login con la cuenta `RECEPCION`: solo deben aparecer **Pacientes**, **Citas** y **Cobros**.
3. Cierra sesión y entra con la cuenta `MEDICO`: solo deben aparecer **Citas** y **Expediente**.
4. Prueba una contraseña incorrecta para verificar que el acceso sea rechazado.
