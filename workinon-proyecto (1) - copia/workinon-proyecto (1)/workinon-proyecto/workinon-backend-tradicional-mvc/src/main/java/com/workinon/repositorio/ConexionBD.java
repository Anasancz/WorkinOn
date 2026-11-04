package com.workinon.repositorio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * CLASE DE CONEXIÓN A BASE DE DATOS (JDBC TRADICIONAL)
 * Demuestra:
 *  - JDBC ESTÁNDAR: Conexión nativa de Java a MySQL usando java.sql.DriverManager.
 *  - CONEXIÓN ROBUSTA: Si MySQL está encendido, usa la base de datos `workinon_db`. Si no está encendido, activa respaldo en memoria sin congelar la app.
 */
public class ConexionBD {

    private static String URL = "jdbc:mysql://localhost:3306/workinon_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String USUARIO = "root";
    private static String CONTRASENA = "123456"; // Contraseña configurada por el usuario

    static {
        try {
            // Cargar explícitamente el Driver JDBC de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC de MySQL no encontrado en el classpath.");
        }
    }

    public static Connection obtenerConexion() {
        try {
            return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        } catch (SQLException e) {
            // Intento secundario con contraseña vacía (muy común en instalaciones locales de XAMPP/WAMP)
            try {
                return DriverManager.getConnection(URL, USUARIO, "");
            } catch (SQLException ignored) {
                return null;
            }
        }
    }

    public static boolean probarConexion() {
        try (Connection conn = obtenerConexion()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }
}
