# WorkinON — Backend Tradicional (Java puro, estructura MVC)

Backend sin Spring Boot: JDBC directo a MySQL (con respaldo automático
en memoria si MySQL no está prendido) y un servidor HTTP hecho con
`com.sun.net.httpserver.HttpServer`, el que ya trae el propio JDK.

## Cómo correrlo

Doble clic en `run.bat` (o desde terminal: `run.bat`). Compila todo y
levanta el servidor en `http://localhost:8080`. Si MySQL no está
prendido, arranca solo con datos de ejemplo en memoria — así nunca se
cae la demo en clase por no traer la base de datos encendida.

## Estructura (Modelo - Vista - Controlador)

```
src/main/java/com/workinon/
├── MainBackendTradicional.java   Arranque del servidor + enrutador (solo plomería HTTP)
│
├── modelo/                        M — cómo se ve cada dato
│   ├── Usuario.java, Proyecto.java, Categoria.java, Perfil.java,
│   │   Medalla.java, Reporte.java, SolicitudUnion.java
│   └── evaluacion/                 Jerarquía POO completa (ver abajo)
│
├── repositorio/                   M — cómo se guarda/busca cada dato
│   ├── ConexionBD.java             Conexión JDBC a MySQL
│   ├── AlmacenMemoria.java         Respaldo en memoria + datos de ejemplo
│   └── UsuarioRepositorio.java, ProyectoRepositorio.java,
│       SolicitudUnionRepositorio.java, MedallaRepositorio.java,
│       CategoriaRepositorio.java, PerfilRepositorio.java,
│       ReporteRepositorio.java
│
├── controlador/                   C — qué hacer con cada petición
│   ├── AuthControlador.java, ProyectoControlador.java,
│   │   SolicitudUnionControlador.java, MedallaControlador.java,
│   │   CategoriaControlador.java, PerfilControlador.java,
│   │   ReporteControlador.java, EvaluacionControlador.java
│
└── vista/                         V — cómo se presenta la respuesta
    └── JsonRespuesta.java          Lee/escribe JSON, siempre con el mismo formato
```

## Los 3 bugs que se corrigieron en esta versión

1. **Faltaba `GestorEvaluaciones.java`** (la segunda clase principal,
   la "Ventas" del diagrama). Ya está de vuelta en `modelo/evaluacion/`.
2. **"Aceptar/Rechazar" no se guardaba en la base de datos.** Antes solo
   cambiaba el objeto en memoria de esa única petición. Ahora
   `SolicitudUnionRepositorio.actualizarEstado()` hace un `UPDATE` real.
3. **No existía el endpoint para otorgar medallas.** Se agregó
   `POST /api/medallas/otorgar` con su repositorio y controlador.

También se corrigió el mismo bug de CORS que encontramos en la versión
Spring (le faltaba `PATCH` en `Access-Control-Allow-Methods`), y un
choque de IDs en modo memoria (los usuarios/proyectos nuevos repetían
el ID de los datos de ejemplo).

## Mapeo directo a tu pizarrón (A.2, incisos a-g)

Todo lo que pide revisar está en `modelo/evaluacion/`:

| Inciso | Dónde está |
|---|---|
| a) Clases | `Evaluacion` (abstracta), `GestorEvaluaciones`, y las 4 subclases |
| b) Métodos | 3 abstractos + 2 privados + 1 público en `Evaluacion`; 6 en `GestorEvaluaciones`; 6 propios en cada subclase |
| c) Atributos | 12 en `Evaluacion` (6 privados + 6 protected); 6 en `GestorEvaluaciones`; 6 propios en cada subclase |
| d) Encapsulamiento | Atributos `private`/`protected` con getters/setters validados en las 8 clases |
| e) Polimorfismo | `EvaluacionControlador.crear()` instancia distinta subclase según el tipo, y `GestorEvaluaciones.generarReportePorTipo()` llama a los mismos métodos sobre objetos de tipo `Evaluacion` |
| f) Abstracción | `Evaluacion` es `abstract`, con 3 métodos abstractos que cada subclase implementa a su manera |
| g) Interfaces | `Revisable` (`EvaluacionCodigo`, `EvaluacionCorregirCodigo`) y `Notificable` (`EvaluacionFormulario`, `EvaluacionTrabajoPropio`) |

Y para el punto A.2 en general ("Estructura del Proyecto MVC"), usa el
árbol de carpetas de arriba: cada carpeta corresponde exactamente a
una letra de MVC.
