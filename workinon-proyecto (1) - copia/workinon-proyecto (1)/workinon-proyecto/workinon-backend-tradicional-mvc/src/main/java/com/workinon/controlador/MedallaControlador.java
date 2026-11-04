package com.workinon.controlador;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.workinon.modelo.Medalla;
import com.workinon.repositorio.MedallaRepositorio;
import com.workinon.vista.JsonRespuesta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CONTROLADOR — MedallaControlador
 * Atiende /api/medallas.
 * Se agregó otorgar(), que no existía antes: sin este endpoint, las
 * medallas que un usuario ganaba en un examen nunca llegaban a
 * guardarse en ningún lado.
 */
public class MedallaControlador {

    public static void listar(HttpExchange exchange) throws Exception {
        List<Medalla> todas = MedallaRepositorio.listarTodas();
        JsonRespuesta.enviar(exchange, 200, todas);
    }

    public static void porUsuario(HttpExchange exchange, int idUsuario) throws Exception {
        List<Medalla> medallas = MedallaRepositorio.buscarPorUsuario(idUsuario);
        JsonRespuesta.enviar(exchange, 200, medallas);
    }

    public static void otorgar(HttpExchange exchange) throws Exception {
        JsonObject body = JsonRespuesta.leerCuerpo(exchange);
        int idUsuario = JsonRespuesta.leerEntero(body, "idUsuario");
        int idMedalla = JsonRespuesta.leerEntero(body, "idMedalla");

        if (!MedallaRepositorio.usuarioYaTieneMedalla(idUsuario, idMedalla)) {
            MedallaRepositorio.otorgar(idUsuario, idMedalla);
        }
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("ok", true);
        JsonRespuesta.enviar(exchange, 200, respuesta);
    }
}
