package com.workinon.repositorio;

import com.workinon.modelo.Perfil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * REPOSITORIO — PerfilRepositorio
 * Catálogo fijo de habilidades (Java, Redes, UX, etc.).
 */
public class PerfilRepositorio {

    public static List<Perfil> listarTodos() {
        if (AlmacenMemoria.usandoMySQL) {
            List<Perfil> resultado = new ArrayList<>();
            String sql = "SELECT * FROM perfil";
            try (Connection conn = ConexionBD.obtenerConexion();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(new Perfil(rs.getInt("id_perfil"), rs.getString("nombre")));
                }
                return resultado;
            } catch (SQLException e) {
                System.err.println("Error JDBC listarTodos (perfil): " + e.getMessage());
            }
        }
        return AlmacenMemoria.perfiles;
    }

    public static Perfil buscarPorId(int id) {
        List<Perfil> todos = listarTodos();
        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).getIdPerfil() == id) {
                return todos.get(i);
            }
        }
        return null;
    }

    public static List<Perfil> buscarPorProyecto(int idProyecto) {
        List<Perfil> resultado = new ArrayList<>();
        if (!AlmacenMemoria.usandoMySQL) {
            return resultado;
        }
        String sql = "SELECT pf.* FROM perfil pf JOIN proyecto_perfil pp ON pf.id_perfil = pp.id_perfil WHERE pp.id_proyecto = ?";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProyecto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(new Perfil(rs.getInt("id_perfil"), rs.getString("nombre")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error JDBC buscarPorProyecto (perfil): " + e.getMessage());
        }
        return resultado;
    }

    public static void vincularConProyecto(int idProyecto, int idPerfil) {
        if (!AlmacenMemoria.usandoMySQL) {
            return;
        }
        String sql = "INSERT INTO proyecto_perfil (id_proyecto, id_perfil) VALUES (?, ?)";
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProyecto);
            ps.setInt(2, idPerfil);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error JDBC vincularConProyecto: " + e.getMessage());
        }
    }
}
