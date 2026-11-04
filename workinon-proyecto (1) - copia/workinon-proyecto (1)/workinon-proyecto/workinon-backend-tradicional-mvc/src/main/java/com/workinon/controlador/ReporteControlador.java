package com.workinon.controlador;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.workinon.modelo.Reporte;
import com.workinon.repositorio.ReporteRepositorio;
import com.workinon.vista.JsonRespuesta;

/**
 * CONTROLADOR — ReporteControlador
 * Atiende /api/reportes.
 */
public class ReporteControlador {

    public static void crear(HttpExchange exchange) throws Exception {
        JsonObject body = JsonRespuesta.leerCuerpo(exchange);
        String tipo = JsonRespuesta.leerTexto(body, "tipo");
        String elementoReportado = JsonRespuesta.leerTexto(body, "elementoReportado");
        String motivo = JsonRespuesta.leerTexto(body, "motivo").trim();

        if (motivo.length() < 10) {
            JsonRespuesta.enviarError(exchange, 400, "Cuéntanos con un poco más de detalle el motivo del reporte");
            return;
        }

        Reporte nuevo = new Reporte(tipo, elementoReportado, motivo);
        ReporteRepositorio.guardar(nuevo);
        JsonRespuesta.enviar(exchange, 201, nuevo);
    }
}
