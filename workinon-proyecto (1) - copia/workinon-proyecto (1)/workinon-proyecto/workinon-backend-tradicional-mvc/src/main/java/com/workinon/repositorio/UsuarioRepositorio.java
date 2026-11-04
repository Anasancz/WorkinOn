package com.workinon.repositorio;

import com.workinon.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * ============================================================
 *  REPOSITORIO — UsuarioRepositorio
 * ============================================================
 * Toda la lógica para leer y guardar usuarios, ya sea en MySQL (JDBC
 * puro: Connection + PreparedStatement + ResultSet) o en memoria si
 * MySQL no está disponible.
 *
 * Los Controladores NUNCA escriben SQL directamente: siempre le piden
 * los datos a un Repositorio, igual que en la versión con Spring
 * Boot usaban UsuarioRepository (solo que ahí Spring generaba el SQL
 * solo, y aquí lo escribimos nosotros a mano).
 */
public class UsuarioRepositorio {

    public static Usuario autenticar(String correo, String contrasena) {
        if (AlmacenMemoria.usandoMySQL) {
            String sql = "SELECT * FROM usuarios WHERE correo = ?";
            try (Connection conn = ConexionBD.obtenerConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, correo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String passGuardada = rs.getString("contrasena");
                        if (coincideContrasena(contrasena, passGuardada)) {
                            return mapearUsuario(rs);
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error JDBC autenticar: " + e.getMessage());
            }
            return null;
        }

        for (int i = 0; i < AlmacenMemoria.usuarios.size(); i++) {
            Usuario u = AlmacenMemoria.usuarios.get(i);
            if (u.getCorreo().equalsIgnoreCase(correo) && coincideContrasena(contrasena, u.getContrasena())) {
                return u;
            }
        }
        return null;
    }

    public static Usuario buscarPorCorreo(String correo) {
        if (AlmacenMemoria.usandoMySQL) {
            String sql = "SELECT * FROM usuarios WHERE correo = ?";
            try (Connection conn = ConexionBD.obtenerConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, correo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapearUsuario(rs);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error JDBC buscarPorCorreo: " + e.getMessage());
            }
            return null;
        }

        for (int i = 0; i < AlmacenMemoria.usuarios.size(); i++) {
            Usuario u = AlmacenMemoria.usuarios.get(i);
            if (u.getCorreo().equalsIgnoreCase(correo)) {
                return u;
            }
        }
        return null;
    }

    public static Usuario buscarPorId(int id) {
        if (AlmacenMemoria.usandoMySQL) {
            String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
            try (Connection conn = ConexionBD.obtenerConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapearUsuario(rs);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error JDBC buscarPorId (usuario): " + e.getMessage());
            }
            return null;
        }

        for (int i = 0; i < AlmacenMemoria.usuarios.size(); i++) {
            Usuario u = AlmacenMemoria.usuarios.get(i);
            if (u.getIdUsuario() == id) {
                return u;
            }
        }
        return null;
    }

    public static void guardar(Usuario usuario) {
        String hashContrasena = usuario.getContrasena();
        if (hashContrasena != null && !hashContrasena.startsWith("$2a$")) {
            try {
                hashContrasena = org.mindrot.jbcrypt.BCrypt.hashpw(usuario.getContrasena(), org.mindrot.jbcrypt.BCrypt.gensalt());
            } catch (Exception ignored) {
                // Si algo falla al cifrar, guardamos la contraseña tal cual llegó.
            }
        }

        if (AlmacenMemoria.usandoMySQL) {
            String sql = "INSERT INTO usuarios (nombre, correo, contrasena, descripcion) VALUES (?, ?, ?, ?)";
            try (Connection conn = ConexionBD.obtenerConexion();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, usuario.getNombre());
                ps.setString(2, usuario.getCorreo());
                ps.setString(3, hashContrasena);
                ps.setString(4, usuario.getDescripcion());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setIdUsuario(rs.getInt(1));
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error JDBC guardar (usuario): " + e.getMessage());
            }
            return;
        }

        usuario.setContrasena(hashContrasena);
        AlmacenMemoria.usuarios.add(usuario);
    }

    // ---------------------------------------------------------
    // Métodos privados de apoyo, solo los usa esta misma clase
    // ---------------------------------------------------------
    private static boolean coincideContrasena(String contrasenaEscrita, String contrasenaGuardada) {
        if (contrasenaGuardada == null) {
            return false;
        }
        boolean esHashBCrypt = contrasenaGuardada.startsWith("$2a$")
                || contrasenaGuardada.startsWith("$2b$")
                || contrasenaGuardada.startsWith("$2y$");
        if (esHashBCrypt) {
            try {
                return org.mindrot.jbcrypt.BCrypt.checkpw(contrasenaEscrita, contrasenaGuardada);
            } catch (Exception ignored) {
                return false;
            }
        }
        return contrasenaEscrita.equals(contrasenaGuardada);
    }

    private static Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id_usuario"),
                rs.getString("nombre"),
                rs.getString("correo"),
                rs.getString("contrasena"),
                rs.getString("descripcion")
        );
    }
}
