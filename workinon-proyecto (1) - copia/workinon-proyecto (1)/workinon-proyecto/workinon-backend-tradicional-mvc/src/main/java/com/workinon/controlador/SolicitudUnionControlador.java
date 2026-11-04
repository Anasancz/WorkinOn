package com.workinon.controlador;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.workinon.modelo.Proyecto;
import com.workinon.modelo.SolicitudUnion;
import com.workinon.modelo.Usuario;
import com.workinon.repositorio.ProyectoRepositorio;
import com.workinon.repositorio.SolicitudUnionRepositorio;
import com.workinon.repositorio.UsuarioRepositorio;
import com.workinon.vista.JsonRespuesta;

import java.util.List;

/**
 * ============================================================
 *  CONTROLADOR — SolicitudUnionControlador
 * ============================================================
 * Aquí vive el corazón de la colaboración de WorkinON. Las 3 reglas
 * que se corrigieron respecto a la primera versión:
 *   1) No se pueden mandar solicitudes duplicadas al mismo proyecto.
 *   2) No se puede aceptar más gente de la que el proyecto permite.
 *   3) Solo el líder real del proyecto puede aceptar/rechazar.
 */
public class SolicitudUnionControlador {

    public static void crear(HttpExchange exchange) throws Exception {
        JsonObject body = JsonRespuesta.leerCuerpo(exchange);
        int idUsuario = JsonRespuesta.leerEntero(body, "idUsuario");
        int idProyecto = JsonRespuesta.leerEntero(body, "idProyecto");
        String procedencia = JsonRespuesta.leerTexto(body, "procedencia").trim();
        String grado = JsonRespuesta.leerTexto(body, "grado").trim();
        String perfilExperiencia = JsonRespuesta.leerTexto(body, "perfilExperiencia").trim();

        Usuario usuario = UsuarioRepositorio.buscarPorId(idUsuario);
        Proyecto proyecto = ProyectoRepositorio.buscarPorId(idProyecto);
        if (usuario == null || proyecto == null) {
            JsonRespuesta.enviarError(exchange, 400, "Usuario o proyecto no válido");
            return;
        }
        if (proyecto.getUsuario().getIdUsuario() == idUsuario) {
            JsonRespuesta.enviarError(exchange, 400, "No puedes solicitar unirte a tu propio proyecto");
            return;
        }
        if (grado.length() < 3) {
            JsonRespuesta.enviarError(exchange, 400, "Ingresa tu grado universitario");
            return;
        }
        if (perfilExperiencia.length() < 10) {
            JsonRespuesta.enviarError(exchange, 400, "Cuéntanos un poco más sobre tu experiencia");
            return;
        }

        // CORRECCIÓN 1: control de solicitudes duplicadas
        SolicitudUnion existente = SolicitudUnionRepositorio.buscarPorUsuarioYProyecto(idUsuario, idProyecto);
        if (existente != null) {
            if ("pendiente".equalsIgnoreCase(existente.getEstado())) {
                JsonRespuesta.enviarError(exchange, 400, "Ya enviaste una solicitud a este proyecto");
                return;
            }
            if ("aceptada".equalsIgnoreCase(existente.getEstado())) {
                JsonRespuesta.enviarError(exchange, 400, "Ya formas parte de este proyecto");
                return;
            }
            // Si estaba "rechazada", se permite volver a intentarlo.
        }

        SolicitudUnion nueva = new SolicitudUnion(usuario, proyecto, procedencia, grado, perfilExperiencia);
        SolicitudUnionRepositorio.guardar(nueva);
        JsonRespuesta.enviar(exchange, 201, nueva);
    }

    public static void porUsuario(HttpExchange exchange, int idUsuario) throws Exception {
        List<SolicitudUnion> resultado = SolicitudUnionRepositorio.buscarPorUsuario(idUsuario);
        JsonRespuesta.enviar(exchange, 200, resultado);
    }

    public static void porProyecto(HttpExchange exchange, int idProyecto) throws Exception {
        List<SolicitudUnion> resultado = SolicitudUnionRepositorio.buscarPorProyecto(idProyecto);
        JsonRespuesta.enviar(exchange, 200, resultado);
    }

    public static void actualizarEstado(HttpExchange exchange, int idSolicitud) throws Exception {
        JsonObject body = JsonRespuesta.leerCuerpo(exchange);
        int idUsuarioRevisor = JsonRespuesta.leerEntero(body, "idUsuarioRevisor");
        String nuevoEstado = JsonRespuesta.leerTexto(body, "estado");

        SolicitudUnion solicitud = SolicitudUnionRepositorio.buscarPorId(idSolicitud);
        if (solicitud == null) {
            JsonRespuesta.enviarError(exchange, 404, "Solicitud no encontrada");
            return;
        }

        // CORRECCIÓN 2: solo el líder real del proyecto puede decidir
        Proyecto proyecto = solicitud.getProyecto();
        if (proyecto == null || proyecto.getUsuario().getIdUsuario() != idUsuarioRevisor) {
            JsonRespuesta.enviarError(exchange, 403, "Solo el líder del proyecto puede revisar esta solicitud");
            return;
        }

        // CORRECCIÓN 3: no exceder el cupo de vacantes al aceptar
        if ("aceptada".equalsIgnoreCase(nuevoEstado) && !"aceptada".equalsIgnoreCase(solicitud.getEstado())) {
            int aceptadosActuales = SolicitudUnionRepositorio.contarPorProyectoYEstado(proyecto.getIdProyecto(), "aceptada");
            if (aceptadosActuales >= proyecto.getVacantesDisponibles()) {
                JsonRespuesta.enviarError(exchange, 400, "Ya no hay vacantes disponibles en este proyecto");
                return;
            }
        }

        SolicitudUnionRepositorio.actualizarEstado(idSolicitud, nuevoEstado);
        solicitud.setEstado(nuevoEstado);
        JsonRespuesta.enviar(exchange, 200, solicitud);
    }
}
