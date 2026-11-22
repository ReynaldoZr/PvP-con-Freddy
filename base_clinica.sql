BEGIN;

CREATE TABLE usuarios (
    id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario varchar(60) NOT NULL UNIQUE,
    clave_hash text NOT NULL,
    nombre varchar(120) NOT NULL,
    rol varchar(12) NOT NULL CHECK (rol IN ('RECEPCION', 'MEDICO'))
);

CREATE TABLE pacientes (
    id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombres varchar(80) NOT NULL,
    apellidos varchar(80) NOT NULL,
    fecha_nacimiento date,
    telefono varchar(20)
);

CREATE TABLE consultorios (
    id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre varchar(80) NOT NULL UNIQUE
);

CREATE TABLE citas (
    id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    paciente_id integer NOT NULL REFERENCES pacientes (id),
    medico_id integer NOT NULL REFERENCES usuarios (id),
    consultorio_id integer NOT NULL REFERENCES consultorios (id),
    fecha date NOT NULL,
    hora time NOT NULL,
    CONSTRAINT cita_en_bloque_de_media_hora CHECK (
        EXTRACT(MINUTE FROM hora) IN (0, 30)
        AND EXTRACT(SECOND FROM hora) = 0
        AND hora < TIME '24:00'
    ),
    CONSTRAINT cita_medico_ocupado UNIQUE (medico_id, fecha, hora),
    CONSTRAINT cita_consultorio_ocupado UNIQUE (consultorio_id, fecha, hora)
);

CREATE TABLE consultas (
    id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cita_id integer NOT NULL UNIQUE REFERENCES citas (id),
    motivo text NOT NULL,
    diagnostico text NOT NULL,
    indicaciones text,
    actualizado_por integer NOT NULL REFERENCES usuarios (id),
    actualizado_en timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cobros (
    id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    consulta_id integer NOT NULL UNIQUE REFERENCES consultas (id),
    concepto varchar(120) NOT NULL,
    importe numeric(10, 2) NOT NULL CHECK (importe > 0),
    registrado_en timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO consultorios (nombre) VALUES ('Consultorio 1'), ('Consultorio 2');

COMMIT;
