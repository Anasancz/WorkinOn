package com.workinon.controlador;

import com.sun.net.httpserver.HttpExchange;
import com.workinon.modelo.Categoria;
import com.workinon.repositorio.CategoriaRepositorio;
import com.workinon.vista.JsonRespuesta;

import java.util.List;

/**
 * CONTROLADOR — CategoriaControlador
 * Atiende /api/categorias. Solo expone el catálogo, no valida nada.
 */
public class CategoriaControlador {

    public static void listar(HttpExchange exchange) throws Exception {
        List<Categoria> todas = CategoriaRepositorio.listarTodas();
        JsonRespuesta.enviar(exchange, 200, todas);
    }
}
