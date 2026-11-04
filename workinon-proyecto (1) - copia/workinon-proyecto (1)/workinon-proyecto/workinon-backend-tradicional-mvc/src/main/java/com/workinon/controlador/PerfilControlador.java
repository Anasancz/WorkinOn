package com.workinon.controlador;

import com.sun.net.httpserver.HttpExchange;
import com.workinon.modelo.Perfil;
import com.workinon.repositorio.PerfilRepositorio;
import com.workinon.vista.JsonRespuesta;

import java.util.List;

/**
 * CONTROLADOR — PerfilControlador
 * Atiende /api/perfiles. Solo expone el catálogo de habilidades.
 */
public class PerfilControlador {

    public static void listar(HttpExchange exchange) throws Exception {
        List<Perfil> todos = PerfilRepositorio.listarTodos();
        JsonRespuesta.enviar(exchange, 200, todos);
    }
}
