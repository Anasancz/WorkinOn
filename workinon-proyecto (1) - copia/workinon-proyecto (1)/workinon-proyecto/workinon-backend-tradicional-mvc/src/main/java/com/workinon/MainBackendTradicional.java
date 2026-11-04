package com.workinon;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.workinon.controlador.AuthControlador;
import com.workinon.controlador.CategoriaControlador;
import com.workinon.controlador.EvaluacionControlador;
import com.workinon.controlador.MedallaControlador;
import com.workinon.controlador.PerfilControlador;
import com.workinon.controlador.ProyectoControlador;
import com.workinon.controlador.ReporteControlador;
import com.workinon.controlador.SolicitudUnionControlador;
import com.workinon.repositorio.AlmacenMemoria;
import com.workinon.vista.JsonRespuesta;

import java.net.InetSocketAddress;

/**
 * ============================================================
 *  SERVIDOR HTTP TRADICIONAL EN JAVA PURO (Sin Spring Boot)
 * ============================================================
 * Este archivo YA NO contiene lógica de negocio (validaciones,
 * reglas de colaboración, etc.) — solo prende el servidor y decide,
 * según la URL y el método HTTP, a qué Controlador mandarle la
 * petición. Toda la lógica real vive en com.workinon.controlador.
 *
 * Es exactamente el mismo papel que cumple Spring internamente
 * cuando lee tus anotaciones @GetMapping/@PostMapping — solo que
 * aquí ese enrutamiento lo escribimos nosotros mismos, a mano, con
 * if/else, para que se vea explícitamente cómo funciona.
 */
public class MainBackendTradicional {

    private static final int PUERTO = 8080;

    public static void main(String[] args) {
        try {
            // Antes de aceptar peticiones, nos aseguramos de que la
            // base de datos (o el respaldo en memoria) esté lista.
            AlmacenMemoria.inicializar();

            HttpServer server = HttpServer.create(new InetSocketAddress(PUERTO), 0);
            System.out.println("=================================================");
            System.out.println("BACKEND EN JAVA PURO");
            System.out.println("   Escuchando en http://localhost:" + PUERTO);
            System.out.println("   Frontend compatible: workinon/workinon/index.html");
            System.out.println("=================================================");

            server.createContext("/api", new Enrutador());
            server.setExecutor(null);
            server.start();

        } catch (Exception e) {
            System.err.println("Error al iniciar el servidor Java puro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Enrutador manual: revisa el método HTTP (GET/POST/PATCH...) y la
     * ruta (/api/...) y llama al Controlador correspondiente.
     */
    static class Enrutador implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) {
            try {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                // IMPORTANTE: PATCH está incluido aquí. Si se te olvida
                // agregar un método nuevo a esta lista, el navegador
                // bloqueará esa petición por CORS aunque el backend
                // esté funcionando perfectamente bien.
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

                String metodo = exchange.getRequestMethod();
                if ("OPTIONS".equalsIgnoreCase(metodo)) {
                    exchange.sendResponseHeaders(204, -1);
                    return;
                }

                String ruta = exchange.getRequestURI().getPath();
                System.out.println("-> Petición recibida: [" + metodo + "] " + ruta);

                despachar(exchange, metodo, ruta);

            } catch (Exception e) {
                System.err.println("Error procesando petición: " + e.getMessage());
                try {
                    JsonRespuesta.enviarError(exchange, 500, "Error interno en servidor: " + e.getMessage());
                } catch (Exception ignorado) {
                    // Si ni siquiera se pudo mandar el error, ya no hay nada más que hacer.
                }
            }
        }

        private void despachar(HttpExchange exchange, String metodo, String ruta) throws Exception {

            // ---------- AUTENTICACIÓN ----------
            if (ruta.equals("/api/auth/login") && metodo.equalsIgnoreCase("POST")) {
                AuthControlador.login(exchange);

            } else if (ruta.equals("/api/auth/register") && metodo.equalsIgnoreCase("POST")) {
                AuthControlador.registrar(exchange);

            // ---------- PROYECTOS ----------
            } else if (ruta.equals("/api/proyectos") && metodo.equalsIgnoreCase("GET")) {
                ProyectoControlador.listar(exchange);

            } else if (ruta.equals("/api/proyectos") && metodo.equalsIgnoreCase("POST")) {
                ProyectoControlador.crear(exchange);

            } else if (ruta.matches("/api/proyectos/\\d+/miembros") && metodo.equalsIgnoreCase("GET")) {
                int idProyecto = extraerNumero(ruta, 3);
                int idUsuario = extraerParametro(exchange, "idUsuario");
                ProyectoControlador.miembros(exchange, idProyecto, idUsuario);

            // ---------- CATÁLOGOS ----------
            } else if (ruta.equals("/api/categorias") && metodo.equalsIgnoreCase("GET")) {
                CategoriaControlador.listar(exchange);

            } else if (ruta.equals("/api/perfiles") && metodo.equalsIgnoreCase("GET")) {
                PerfilControlador.listar(exchange);

            // ---------- MEDALLAS ----------
            } else if (ruta.equals("/api/medallas") && metodo.equalsIgnoreCase("GET")) {
                MedallaControlador.listar(exchange);

            } else if (ruta.matches("/api/medallas/usuario/\\d+") && metodo.equalsIgnoreCase("GET")) {
                int idUsuario = extraerNumero(ruta, 4);
                MedallaControlador.porUsuario(exchange, idUsuario);

            } else if (ruta.equals("/api/medallas/otorgar") && metodo.equalsIgnoreCase("POST")) {
                MedallaControlador.otorgar(exchange);

            // ---------- SOLICITUDES DE UNIÓN ----------
            } else if (ruta.equals("/api/solicitudes") && metodo.equalsIgnoreCase("POST")) {
                SolicitudUnionControlador.crear(exchange);

            } else if (ruta.matches("/api/solicitudes/usuario/\\d+") && metodo.equalsIgnoreCase("GET")) {
                int idUsuario = extraerNumero(ruta, 4);
                SolicitudUnionControlador.porUsuario(exchange, idUsuario);

            } else if (ruta.matches("/api/solicitudes/proyecto/\\d+") && metodo.equalsIgnoreCase("GET")) {
                int idProyecto = extraerNumero(ruta, 4);
                SolicitudUnionControlador.porProyecto(exchange, idProyecto);

            } else if (ruta.matches("/api/solicitudes/\\d+") && metodo.equalsIgnoreCase("PATCH")) {
                int idSolicitud = extraerNumero(ruta, 3);
                SolicitudUnionControlador.actualizarEstado(exchange, idSolicitud);

            // ---------- EVALUACIONES ----------
            } else if (ruta.equals("/api/evaluaciones") && metodo.equalsIgnoreCase("GET")) {
                EvaluacionControlador.listar(exchange);

            } else if (ruta.equals("/api/evaluaciones") && metodo.equalsIgnoreCase("POST")) {
                EvaluacionControlador.crear(exchange);

            // ---------- REPORTES ----------
            } else if (ruta.equals("/api/reportes") && metodo.equalsIgnoreCase("POST")) {
                ReporteControlador.crear(exchange);

            } else {
                JsonRespuesta.enviarError(exchange, 404, "Ruta no encontrada en backend tradicional: " + metodo + " " + ruta);
            }
        }

        // Saca el número que está en la posición "posicion" de la ruta,
        // separando por "/". Ej: "/api/proyectos/5/miembros" separado
        // por "/" da ["", "api", "proyectos", "5", "miembros"], así que
        // el número de proyecto está en la posición 3.
        private int extraerNumero(String ruta, int posicion) {
            String[] partes = ruta.split("/");
            return Integer.parseInt(partes[posicion]);
        }

        // Lee un parámetro de la URL después del "?", ej: ?idUsuario=7
        private int extraerParametro(HttpExchange exchange, String nombreParametro) {
            String query = exchange.getRequestURI().getQuery();
            if (query == null) {
                return 0;
            }
            String[] pares = query.split("&");
            for (int i = 0; i < pares.length; i++) {
                String[] partes = pares[i].split("=");
                if (partes.length == 2 && partes[0].equals(nombreParametro)) {
                    return Integer.parseInt(partes[1]);
                }
            }
            return 0;
        }
    }
}
