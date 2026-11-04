package com.workinon.controlador;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.workinon.modelo.Usuario;
import com.workinon.repositorio.UsuarioRepositorio;
import com.workinon.vista.JsonRespuesta;

/**
 * CONTROLADOR — AuthControlador
 * Atiende /api/auth/login y /api/auth/register.
 * Mismas reglas de validación que la versión con Spring Boot, para
 * que ambos backends se comporten exactamente igual desde la página.
 */
public class AuthControlador {

    public static void login(HttpExchange exchange) throws Exception {
        JsonObject body = JsonRespuesta.leerCuerpo(exchange);
        String correo = JsonRespuesta.leerTexto(body, "correo").trim();
        String contrasena = JsonRespuesta.leerTexto(body, "contrasena");

        Usuario usuario = UsuarioRepositorio.autenticar(correo, contrasena);
        if (usuario == null) {
            JsonRespuesta.enviarError(exchange, 401, "Correo o contraseña incorrectos");
            return;
        }
        JsonRespuesta.enviar(exchange, 200, usuario);
    }

    public static void registrar(HttpExchange exchange) throws Exception {
        JsonObject body = JsonRespuesta.leerCuerpo(exchange);
        String nombre = JsonRespuesta.leerTexto(body, "nombre").trim();
        String correo = JsonRespuesta.leerTexto(body, "correo").trim();
        String contrasena = JsonRespuesta.leerTexto(body, "contrasena");

        if (nombre.length() < 3) {
            JsonRespuesta.enviarError(exchange, 400, "El nombre debe tener al menos 3 caracteres");
            return;
        }
        if (!correo.contains("@")) {
            JsonRespuesta.enviarError(exchange, 400, "Ingresa un correo electrónico válido");
            return;
        }
        if (contrasena.length() < 8) {
            JsonRespuesta.enviarError(exchange, 400, "La contraseña debe tener al menos 8 caracteres");
            return;
        }
        if (UsuarioRepositorio.buscarPorCorreo(correo) != null) {
            JsonRespuesta.enviarError(exchange, 400, "Ese correo ya está registrado");
            return;
        }

        Usuario nuevo = new Usuario(nombre, correo, contrasena);
        UsuarioRepositorio.guardar(nuevo);
        JsonRespuesta.enviar(exchange, 201, nuevo);
    }
}
