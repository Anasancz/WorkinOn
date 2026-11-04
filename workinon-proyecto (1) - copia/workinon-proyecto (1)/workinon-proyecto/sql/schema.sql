-- =========================================================
-- WorkinON — Esquema de base de datos (MySQL 8+)
-- Basado en el modelo relacional entregado por el equipo.
-- Los nombres de columnas se escriben sin acentos ni ñ
-- (buena práctica en MySQL para evitar problemas de
-- codificación/collation entre entornos).
-- =========================================================

CREATE DATABASE IF NOT EXISTS workinon_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE workinon_db;

-- ---------------------------------------------------------
-- USUARIOS
-- ---------------------------------------------------------
CREATE TABLE usuarios (
  id_usuario      INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre          VARCHAR(100) NOT NULL,
  correo          VARCHAR(100) NOT NULL UNIQUE,
  contrasena      VARCHAR(100) NOT NULL,          -- se guarda con hash BCrypt
  descripcion     VARCHAR(255),
  foto_perfil     VARCHAR(100),
  fecha_registro  DATE NOT NULL DEFAULT (CURRENT_DATE)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- CATEGORIA
-- ---------------------------------------------------------
CREATE TABLE categoria (
  id_categoria INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre       VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- PROYECTO
-- ---------------------------------------------------------
CREATE TABLE proyecto (
  id_proyecto           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  id_usuario            INT UNSIGNED NOT NULL,
  id_categoria          INT UNSIGNED NOT NULL,
  nombre                VARCHAR(100) NOT NULL,
  descripcion           VARCHAR(255),
  vacantes_disponibles  SMALLINT,
  fecha_creacion        DATE NOT NULL DEFAULT (CURRENT_DATE),
  estado                BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT fk_proyecto_usuario   FOREIGN KEY (id_usuario)   REFERENCES usuarios(id_usuario)     ON DELETE CASCADE,
  CONSTRAINT fk_proyecto_categoria FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)   ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- PERFIL
-- ---------------------------------------------------------
CREATE TABLE perfil (
  id_perfil    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre       VARCHAR(100) NOT NULL,
  descripcion  VARCHAR(255)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- PARTICIPANTE_PROYECTO
-- ---------------------------------------------------------
CREATE TABLE participante_proyecto (
  id_participante  INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  id_usuario       INT UNSIGNED NOT NULL,
  id_proyecto      INT UNSIGNED NOT NULL,
  rol              VARCHAR(100),
  fecha            DATE,
  horas_invertidas TIME,
  CONSTRAINT fk_participante_usuario  FOREIGN KEY (id_usuario)  REFERENCES usuarios(id_usuario)  ON DELETE CASCADE,
  CONSTRAINT fk_participante_proyecto FOREIGN KEY (id_proyecto) REFERENCES proyecto(id_proyecto) ON DELETE CASCADE,
  UNIQUE KEY uq_participante (id_usuario, id_proyecto)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- PROYECTO_PERFIL (N:M)
-- ---------------------------------------------------------
CREATE TABLE proyecto_perfil (
  id_proyecto INT UNSIGNED NOT NULL,
  id_perfil   INT UNSIGNED NOT NULL,
  PRIMARY KEY (id_proyecto, id_perfil),
  CONSTRAINT fk_proyectoperfil_proyecto FOREIGN KEY (id_proyecto) REFERENCES proyecto(id_proyecto) ON DELETE CASCADE,
  CONSTRAINT fk_proyectoperfil_perfil   FOREIGN KEY (id_perfil)   REFERENCES perfil(id_perfil)     ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- SOLICITUD_UNION
-- ---------------------------------------------------------
CREATE TABLE solicitud_union (
  id_solicitud    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  id_usuario      INT UNSIGNED NOT NULL,
  id_proyecto     INT UNSIGNED NOT NULL,
  procedencia     VARCHAR(100),
  grado           VARCHAR(100),
  -- La experiencia/motivo que la persona escribe al solicitar unirse.
  -- Antes se validaba en el back-end pero nunca se guardaba en ningún lado.
  perfil_experiencia TEXT,
  estado          VARCHAR(100) NOT NULL DEFAULT 'pendiente',
  fecha_solicitud DATE NOT NULL DEFAULT (CURRENT_DATE),
  CONSTRAINT fk_solicitud_usuario  FOREIGN KEY (id_usuario)  REFERENCES usuarios(id_usuario)  ON DELETE CASCADE,
  CONSTRAINT fk_solicitud_proyecto FOREIGN KEY (id_proyecto) REFERENCES proyecto(id_proyecto) ON DELETE CASCADE,
  -- Un mismo usuario no puede tener dos filas de solicitud para el mismo
  -- proyecto (evita duplicados a nivel de base de datos, además de la
  -- validación que ya hace el backend).
  UNIQUE KEY uq_solicitud_usuario_proyecto (id_usuario, id_proyecto)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- SOLICITUD_PERFIL (N:M)
-- ---------------------------------------------------------
CREATE TABLE solicitud_perfil (
  id_solicitud INT UNSIGNED NOT NULL,
  id_perfil    INT UNSIGNED NOT NULL,
  PRIMARY KEY (id_solicitud, id_perfil),
  CONSTRAINT fk_solicitudperfil_solicitud FOREIGN KEY (id_solicitud) REFERENCES solicitud_union(id_solicitud) ON DELETE CASCADE,
  CONSTRAINT fk_solicitudperfil_perfil    FOREIGN KEY (id_perfil)    REFERENCES perfil(id_perfil)             ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- REPORTE
-- ---------------------------------------------------------
CREATE TABLE reporte (
  id_reporte     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  id_usuario     INT UNSIGNED NOT NULL,
  id_proyecto    INT UNSIGNED,
  motivo         MEDIUMTEXT NOT NULL,
  fecha_reporte  DATE NOT NULL DEFAULT (CURRENT_DATE),
  CONSTRAINT fk_reporte_usuario  FOREIGN KEY (id_usuario)  REFERENCES usuarios(id_usuario)  ON DELETE CASCADE,
  CONSTRAINT fk_reporte_proyecto FOREIGN KEY (id_proyecto) REFERENCES proyecto(id_proyecto) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- MEDALLA
-- ---------------------------------------------------------
CREATE TABLE medalla (
  id_medalla       INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre           VARCHAR(100) NOT NULL,
  descripcion      VARCHAR(255),
  duracion_examen  TIME
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- USUARIO_MEDALLA (N:M)
-- ---------------------------------------------------------
CREATE TABLE usuario_medalla (
  id_usuario  INT UNSIGNED NOT NULL,
  id_medalla  INT UNSIGNED NOT NULL,
  PRIMARY KEY (id_usuario, id_medalla),
  CONSTRAINT fk_usuariomedalla_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
  CONSTRAINT fk_usuariomedalla_medalla FOREIGN KEY (id_medalla) REFERENCES medalla(id_medalla)  ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- EVALUACION
-- ---------------------------------------------------------
CREATE TABLE evaluacion (
  id_evaluacion    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  id_medalla       INT UNSIGNED,
  id_proyecto      INT UNSIGNED,
  tipo_evaluacion  VARCHAR(100),
  tipo             VARCHAR(100),
  instruccion      MEDIUMTEXT,
  duracion         TIME,
  CONSTRAINT fk_evaluacion_medalla  FOREIGN KEY (id_medalla)  REFERENCES medalla(id_medalla)   ON DELETE SET NULL,
  CONSTRAINT fk_evaluacion_proyecto FOREIGN KEY (id_proyecto) REFERENCES proyecto(id_proyecto) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- PREGUNTA
-- ---------------------------------------------------------
CREATE TABLE pregunta (
  id_pregunta    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  id_evaluacion  INT UNSIGNED NOT NULL,
  enunciado      MEDIUMTEXT NOT NULL,
  CONSTRAINT fk_pregunta_evaluacion FOREIGN KEY (id_evaluacion) REFERENCES evaluacion(id_evaluacion) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- OPCION_RESPUESTA
-- ---------------------------------------------------------
CREATE TABLE opcion_respuesta (
  id_opcion     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  id_pregunta   INT UNSIGNED NOT NULL,
  descripcion   VARCHAR(255) NOT NULL,
  es_correcta   BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_opcion_pregunta FOREIGN KEY (id_pregunta) REFERENCES pregunta(id_pregunta) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- INTENTO_EVALUACION
-- ---------------------------------------------------------
CREATE TABLE intento_evaluacion (
  id_intento     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  id_usuario     INT UNSIGNED NOT NULL,
  id_evaluacion  INT UNSIGNED NOT NULL,
  id_solicitud   INT UNSIGNED,
  fecha_inicio   DATETIME,
  fecha_fin      DATETIME,
  calificacion   TINYINT UNSIGNED,
  CONSTRAINT fk_intento_usuario    FOREIGN KEY (id_usuario)    REFERENCES usuarios(id_usuario)         ON DELETE CASCADE,
  CONSTRAINT fk_intento_evaluacion FOREIGN KEY (id_evaluacion) REFERENCES evaluacion(id_evaluacion)     ON DELETE CASCADE,
  CONSTRAINT fk_intento_solicitud  FOREIGN KEY (id_solicitud)  REFERENCES solicitud_union(id_solicitud) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- ENTREGA_EVALUACION
-- (en el documento aparece nombrada de nuevo "Evaluación" por
--  error de captura; la llamamos entrega_evaluacion porque su
--  PK es Id_Entrega y referencia a un intento de evaluación)
-- ---------------------------------------------------------
CREATE TABLE entrega_evaluacion (
  id_entrega     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  id_intento     INT UNSIGNED NOT NULL,
  archivo        VARCHAR(100),
  codigo         VARCHAR(100),
  fecha_entrega  DATE NOT NULL DEFAULT (CURRENT_DATE),
  CONSTRAINT fk_entrega_intento FOREIGN KEY (id_intento) REFERENCES intento_evaluacion(id_intento) ON DELETE CASCADE
) ENGINE=InnoDB;

-- =========================================================
-- DATOS SEMILLA (catálogos básicos para probar el prototipo)
-- =========================================================
INSERT INTO categoria (nombre) VALUES
  ('Tecnología'), ('Redes'), ('Salud'), ('Ciencia de Datos');

INSERT INTO medalla (nombre, descripcion, duracion_examen) VALUES
  ('Programador', 'Evalúa conocimientos prácticos de programación.', '01:00:00'),
  ('Redes', 'Evalúa conocimientos de administración de redes.', '00:45:00'),
  ('Base de datos', 'Evalúa diseño y consultas de bases de datos.', '00:50:00'),
  ('Backend', 'Evalúa desarrollo de servicios backend.', '01:00:00'),
  ('Frontend', 'Evalúa conocimientos de desarrollo de interfaces web.', '00:50:00'),
  ('DevOps', 'Evalúa conocimientos de integración y despliegue continuo.', '00:45:00');

INSERT INTO perfil (nombre, descripcion) VALUES
  ('Programador', 'Desarrollo de software en general'),
  ('Diseñador Gráfico', 'Diseño visual y de interfaces'),
  ('Analista de Datos', 'Análisis y visualización de datos'),
  ('Técnico en Redes', 'Infraestructura y administración de redes');
