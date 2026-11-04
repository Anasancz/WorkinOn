package com.workinon.controlador;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.workinon.modelo.evaluacion.Evaluacion;
import com.workinon.modelo.evaluacion.EvaluacionCodigo;
import com.workinon.modelo.evaluacion.EvaluacionCorregirCodigo;
import com.workinon.modelo.evaluacion.EvaluacionFormulario;
import com.workinon.modelo.evaluacion.EvaluacionTrabajoPropio;
import com.workinon.modelo.evaluacion.GestorEvaluaciones;
import com.workinon.vista.JsonRespuesta;

/**
 * ============================================================
 *  CONTROLADOR — EvaluacionControlador
 * ============================================================
 * Aquí es donde tu jerarquía de POO (abstracción, herencia,
 * interfaces, polimorfismo) se conecta con la página web.
 *
 * gestor es un único GestorEvaluaciones compartido por todo el
 * servidor (se crea una sola vez, ver el campo static abajo) — así
 * las evaluaciones que se van creando no se pierden entre peticiones.
 */
public class EvaluacionControlador {

    private static final GestorEvaluaciones gestor = new GestorEvaluaciones();

    public static void listar(HttpExchange exchange) throws Exception {
        JsonRespuesta.enviar(exchange, 200, gestor.listarTodas());
    }

    public static void crear(HttpExchange exchange) throws Exception {
        JsonObject body = JsonRespuesta.leerCuerpo(exchange);
        String tipo = body.has("tipo") ? body.get("tipo").getAsString() : "formulario";
        String proyecto = body.has("nombreProyecto") ? body.get("nombreProyecto").getAsString() : "General";
        String instrucciones = body.has("instrucciones") ? body.get("instrucciones").getAsString() : "Sin instrucciones";
        int duracion = body.has("duracionMinutos") ? body.get("duracionMinutos").getAsInt() : 30;

        // INSTANCIACIÓN POLIMÓRFICA: según el "tipo" que llega de la
        // página, se crea una subclase distinta, pero todas quedan
        // guardadas en el mismo ArrayList<Evaluacion> del gestor.
        Evaluacion nueva;
        if ("codigo".equalsIgnoreCase(tipo)) {
            String lenguaje = body.has("lenguaje") ? body.get("lenguaje").getAsString() : "Java";
            nueva = new EvaluacionCodigo(proyecto, instrucciones, duracion, lenguaje);
        } else if ("corregir".equalsIgnoreCase(tipo)) {
            int errores = body.has("erroresOcultos") ? body.get("erroresOcultos").getAsInt() : 3;
            nueva = new EvaluacionCorregirCodigo(proyecto, instrucciones, duracion, errores);
        } else if ("trabajo".equalsIgnoreCase(tipo)) {
            String formato = body.has("formatoEntrega") ? body.get("formatoEntrega").getAsString() : "PDF";
            nueva = new EvaluacionTrabajoPropio(proyecto, instrucciones, duracion, formato);
        } else {
            int preguntas = body.has("numeroPreguntas") ? body.get("numeroPreguntas").getAsInt() : 10;
            nueva = new EvaluacionFormulario(proyecto, instrucciones, duracion, preguntas);
        }

        gestor.agregar(nueva);
        JsonRespuesta.enviar(exchange, 201, nueva);
    }
}
