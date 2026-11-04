package com.workinon.vista;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * ============================================================
 *  CAPA VISTA — JsonRespuesta
 * ============================================================
 * En una app de escritorio, la "Vista" es la pantalla que el usuario
 * ve. Aquí, como no hay pantallas de Java (el que ve algo es el
 * navegador, con tu HTML/CSS/JS), la Vista es la capa que decide CÓMO
 * se le presenta la información a quien pidió los datos: en qué
 * formato (JSON), con qué código de estado HTTP, y qué encabezados.
 *
 * Ningún Controlador arma el JSON a mano — todos llaman a esta clase,
 * así el formato de salida es siempre el mismo en toda la app.
 */
public class JsonRespuesta {

    private static final Gson gson = new Gson();

    // Lee el cuerpo (body) de la petición HTTP y lo convierte en un
    // JsonObject fácil de leer campo por campo.
    public static JsonObject leerCuerpo(HttpExchange exchange) throws Exception {
        InputStreamReader lector = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        return JsonParser.parseReader(lector).getAsJsonObject();
    }

    // Convierte cualquier objeto Java (Usuario, Proyecto, una lista, un
    // mapa de error, etc.) a JSON y lo manda como respuesta HTTP.
    public static void enviar(HttpExchange exchange, int codigoEstado, Object datos) throws Exception {
        String textoJson = gson.toJson(datos);
        byte[] bytes = textoJson.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(codigoEstado, bytes.length);
        OutputStream salida = exchange.getResponseBody();
        salida.write(bytes);
        salida.close();
    }

    // Atajo para respuestas de error, que siempre traen la misma forma:
    // { "message": "..." }
    public static void enviarError(HttpExchange exchange, int codigoEstado, String mensaje) throws Exception {
        JsonObject error = new JsonObject();
        error.addProperty("message", mensaje);
        enviar(exchange, codigoEstado, error);
    }

    // Lee un campo de texto obligatorio del JSON de entrada.
    public static String leerTexto(JsonObject body, String campo) {
        if (body.has(campo) && !body.get(campo).isJsonNull()) {
            return body.get(campo).getAsString();
        }
        return "";
    }

    // Lee un campo entero obligatorio del JSON de entrada.
    public static int leerEntero(JsonObject body, String campo) {
        if (body.has(campo) && !body.get(campo).isJsonNull()) {
            return body.get(campo).getAsInt();
        }
        return 0;
    }
}
