package com.workinon.repositorio;

import com.workinon.modelo.Categoria;
import com.workinon.modelo.Perfil;
import com.workinon.modelo.Proyecto;
import com.workinon.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * REPOSITORIO — ProyectoRepositorio
 * Leer y guardar proyectos, incluyendo sus habilidades relacionadas
 * (tabla intermedia proyecto_perfil).
 */
public class ProyectoRepositorio {

    public static List<Proyecto> listarTodos() {
        if (AlmacenMemoria.usandoMySQL) {
            List<Proyecto> resultado = new ArrayList<>();
            String sql = "SELECT p.*, c.nombre as cat_nombre, u.nombre as user_nombre, u.correo as user_correo "
                    + "FROM proyecto p "
                    + "JOIN categoria c ON p.id_categoria = c.id_categoria "
                    + "JOIN usuarios u ON p.id_usuario = u.id_usuario "
                    + "ORDER BY p.fecha_creacion DESC";
            try (Connection conn = ConexionBD.obtenerConexion();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario u = new Usuario(rs.getInt("id_usuario"), rs.getString("user_nombre"), rs.getString("user_correo"), "", "");
                    Categoria cat = new Categoria(rs.getInt("id_categoria"), rs.getString("cat_nombre"));
                    Proyecto p = new Proyecto(
                            rs.getInt("id_proyecto"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getInt("vacantes_disponibles"),
                            u, cat
                    );
                    p.setHabilidades(PerfilRepositorio.buscarPorProyecto(p.getIdProyecto()));
                    resultado.add(p);
                }
                return resultado;
            } catch (SQLException e) {
                System.err.println("Error JDBC listarTodos (proyecto): " + e.getMessage());
            }
        }
        return AlmacenMemoria.proyectos;
    }

    public static Proyecto buscarPorId(int id) {
        List<Proyecto> todos = listarTodos();
        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).getIdProyecto() == id) {
                return todos.get(i);
            }
        }
        return null;
    }

    public static void guardar(Proyecto proyecto) {
        if (AlmacenMemoria.usandoMySQL) {
            String sql = "INSERT INTO proyecto (id_usuario, id_categoria, nombre, descripcion, vacantes_disponibles) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = ConexionBD.obtenerConexion();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, proyecto.getUsuario().getIdUsuario());
                ps.setInt(2, proyecto.getCategoria().getIdCategoria());
                ps.setString(3, proyecto.getNombre());
                ps.setString(4, proyecto.getDescripcion());
                ps.setInt(5, proyecto.getVacantesDisponibles());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idNuevoProyecto = rs.getInt(1);
                        proyecto.setIdProyecto(idNuevoProyecto);
                        List<Perfil> habilidades = proyecto.getHabilidades();
                        for (int i = 0; i < habilidades.size(); i++) {
                            PerfilRepositorio.vincularConProyecto(idNuevoProyecto, habilidades.get(i).getIdPerfil());
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error JDBC guardar (proyecto): " + e.getMessage());
            }
            return;
        }

        AlmacenMemoria.proyectos.add(proyecto);
    }
}
