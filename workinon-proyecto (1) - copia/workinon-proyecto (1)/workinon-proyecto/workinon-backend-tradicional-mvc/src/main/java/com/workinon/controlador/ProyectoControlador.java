package com.workinon.controlador;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.workinon.modelo.Categoria;
import com.workinon.modelo.Perfil;
import com.workinon.modelo.Proyecto;
import com.workinon.modelo.SolicitudUnion;
import com.workinon.modelo.Usuario;
import com.workinon.repositorio.CategoriaRepositorio;
import com.workinon.repositorio.PerfilRepositorio;
import com.workinon.repositorio.ProyectoRepositorio;
import com.workinon.repositorio.SolicitudUnionRepositorio;
import com.workinon.repositorio.UsuarioRepositorio;
import com.workinon.vista.JsonRespuesta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CONTROLADOR — ProyectoControlador
 * Atiende /api/proyectos.
 */
public class ProyectoControlador {

    public static void listar(HttpExchange exchange) throws Exception {
        List<Proyecto> lista = ProyectoRepositorio.listarTodos();

        // Recalcular miembros aceptados de cada proyecto con un for tradicional
        for (int i = 0; i < lista.size(); i++) {
            Proyecto p = lista.get(i);
            int aceptados = SolicitudUnionRepositorio.contarPorProyectoYEstado(p.getIdProyecto(), "aceptada");
            p.setMiembrosAceptados(aceptados);
        }

        JsonRespuesta.enviar(exchange, 200, lista);
    }

    public static void crear(HttpExchange exchange) throws Exception {
        JsonObject body = JsonRespuesta.leerCuerpo(exchange);
        String nombre = JsonRespuesta.leerTexto(body, "nombre").trim();
        String descripcion = JsonRespuesta.leerTexto(body, "descripcion").trim();
        int vacantes = JsonRespuesta.leerEntero(body, "vacantesDisponibles");
        int idUsuario = JsonRespuesta.leerEntero(body, "idUsuario");
        int idCategoria = JsonRespuesta.leerEntero(body, "idCategoria");

        if (nombre.length() < 3) {
            JsonRespuesta.enviarError(exchange, 400, "El nombre del proyecto es muy corto");
            return;
        }
        if (descripcion.length() < 15) {
            JsonRespuesta.enviarError(exchange, 400, "La descripción es muy corta");
            return;
        }
        if (vacantes < 1) {
            JsonRespuesta.enviarError(exchange, 400, "Ingresa un tamaño de equipo válido");
            return;
        }

        Usuario creador = UsuarioRepositorio.buscarPorId(idUsuario);
        Categoria categoria = CategoriaRepositorio.buscarPorId(idCategoria);
        if (creador == null || categoria == null) {
            JsonRespuesta.enviarError(exchange, 400, "Usuario o categoría no válidos");
            return;
        }

        Proyecto nuevo = new Proyecto(nombre, descripcion, vacantes, creador, categoria);

        if (body.has("idsHabilidades") && body.get("idsHabilidades").isJsonArray()) {
            JsonArray idsHabilidades = body.get("idsHabilidades").getAsJsonArray();
            for (int i = 0; i < idsHabilidades.size(); i++) {
                int idHabilidad = idsHabilidades.get(i).getAsInt();
                Perfil perfil = PerfilRepositorio.buscarPorId(idHabilidad);
                if (perfil != null) {
                    nuevo.agregarHabilidad(perfil);
                }
            }
        }

        ProyectoRepositorio.guardar(nuevo);
        JsonRespuesta.enviar(exchange, 201, nuevo);
    }

    // CORRECCIÓN DE COLABORACIÓN: solo el líder del proyecto o un
    // miembro ya aceptado puede ver el equipo — antes era visible
    // para cualquier usuario, sin ningún control.
    public static void miembros(HttpExchange exchange, int idProyecto, int idUsuarioSolicitante) throws Exception {
        Proyecto proyecto = ProyectoRepositorio.buscarPorId(idProyecto);
        if (proyecto == null) {
            JsonRespuesta.enviarError(exchange, 404, "Proyecto no encontrado");
            return;
        }

        boolean esDueno = proyecto.getUsuario().getIdUsuario() == idUsuarioSolicitante;
        boolean esAceptado = false;
        List<SolicitudUnion> solicitudesDelProyecto = SolicitudUnionRepositorio.buscarPorProyecto(idProyecto);
        for (int i = 0; i < solicitudesDelProyecto.size(); i++) {
            SolicitudUnion s = solicitudesDelProyecto.get(i);
            if (s.getUsuario().getIdUsuario() == idUsuarioSolicitante && "aceptada".equalsIgnoreCase(s.getEstado())) {
                esAceptado = true;
            }
        }

        if (!esDueno && !esAceptado) {
            JsonRespuesta.enviarError(exchange, 403, "Solo el líder o los miembros aceptados pueden ver el equipo");
            return;
        }

        List<Usuario> miembros = new ArrayList<>();
        for (int i = 0; i < solicitudesDelProyecto.size(); i++) {
            SolicitudUnion s = solicitudesDelProyecto.get(i);
            if ("aceptada".equalsIgnoreCase(s.getEstado())) {
                miembros.add(s.getUsuario());
            }
        }

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("propietario", proyecto.getUsuario());
        respuesta.put("miembros", miembros);
        JsonRespuesta.enviar(exchange, 200, respuesta);
    }
}
