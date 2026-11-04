package com.workinon.repositorio;

import com.workinon.modelo.Proyecto;
import com.workinon.modelo.SolicitudUnion;
import com.workinon.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *  REPOSITORIO — SolicitudUnionRepositorio
 * ============================================================
 * Corrección importante respecto a la versión anterior: antes,
 * "aceptar" o "rechazar" una solicitud solo cambiaba el campo estado
 * del objeto Java en memoria de esa única petición — nunca se
 * guardaba en MySQL, así que el cambio desaparecía en la siguiente
 * consulta. Aquí actualizarEstado() sí ejecuta un UPDATE real.
 */
public class SolicitudUnionRepositorio {

    public static List<SolicitudUnion> listarTodas() {
        if (AlmacenMemoria.usandoMySQL) {
            List<SolicitudUnion> resultado = new ArrayList<>();
            String sql = "SELECT * FROM solicitud_union";
            try (Connection conn = ConexionBD.obtenerConexion();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapearSolicitud(rs));
                }
                return resultado;
            } catch (SQLException e) {
                System.err.println("Error JDBC listarTodas (solicitud): " + e.getMessage());
            }
        }
        return AlmacenMemoria.solicitudes;
    }

    public static List<SolicitudUnion> buscarPorUsuario(int idUsuario) {
        if (AlmacenMemoria.usandoMySQL) {
            List<SolicitudUnion> resultado = new ArrayList<>();
            String sql = "SELECT * FROM solicitud_union WHERE id_usuario = ?";
            try (Connection conn = ConexionBD.obtenerConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        resultado.add(mapearSolicitud(rs));
                    }
                }
                return resultado;
            } catch (SQLException e) {
                System.err.println("Error JDBC buscarPorUsuario (solicitud): " + e.getMessage());
            }
        }

        List<SolicitudUnion> resultado = new ArrayList<>();
        for (int i = 0; i < AlmacenMemoria.solicitudes.size(); i++) {
            SolicitudUnion s = AlmacenMemoria.solicitudes.get(i);
            if (s.getUsuario().getIdUsuario() == idUsuario) {
                resultado.add(s);
            }
        }
        return resultado;
    }

    public static List<SolicitudUnion> buscarPorProyecto(int idProyecto) {
        if (AlmacenMemoria.usandoMySQL) {
            List<SolicitudUnion> resultado = new ArrayList<>();
            String sql = "SELECT * FROM solicitud_union WHERE id_proyecto = ?";
            try (Connection conn = ConexionBD.obtenerConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idProyecto);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        resultado.add(mapearSolicitud(rs));
                    }
                }
                return resultado;
            } catch (SQLException e) {
                System.err.println("Error JDBC buscarPorProyecto (solicitud): " + e.getMessage());
            }
        }

        List<SolicitudUnion> resultado = new ArrayList<>();
        for (int i = 0; i < AlmacenMemoria.solicitudes.size(); i++) {
            SolicitudUnion s = AlmacenMemoria.solicitudes.get(i);
            if (s.getProyecto().getIdProyecto() == idProyecto) {
                resultado.add(s);
            }
        }
        return resultado;
    }

    // Para el control de duplicados: ¿ya existe una solicitud de este
    // usuario a este proyecto?
    public static SolicitudUnion buscarPorUsuarioYProyecto(int idUsuario, int idProyecto) {
        List<SolicitudUnion> deEsteUsuario = buscarPorUsuario(idUsuario);
        for (int i = 0; i < deEsteUsuario.size(); i++) {
            SolicitudUnion s = deEsteUsuario.get(i);
            if (s.getProyecto().getIdProyecto() == idProyecto) {
                return s;
            }
        }
        return null;
    }

    public static SolicitudUnion buscarPorId(int id) {
        List<SolicitudUnion> todas = listarTodas();
        for (int i = 0; i < todas.size(); i++) {
            if (todas.get(i).getIdSolicitud() == id) {
                return todas.get(i);
            }
        }
        return null;
    }

    // Cuenta cuántas solicitudes de un proyecto ya están en un estado
    // dado (lo usa el Controlador para no exceder las vacantes).
    public static int contarPorProyectoYEstado(int idProyecto, String estado) {
        if (AlmacenMemoria.usandoMySQL) {
            String sql = "SELECT COUNT(*) FROM solicitud_union WHERE id_proyecto = ? AND UPPER(estado) = UPPER(?)";
            try (Connection conn = ConexionBD.obtenerConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idProyecto);
                ps.setString(2, estado);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error JDBC contarPorProyectoYEstado: " + e.getMessage());
            }
            return 0;
        }

        int contador = 0;
        List<SolicitudUnion> deEsteProyecto = buscarPorProyecto(idProyecto);
        for (int i = 0; i < deEsteProyecto.size(); i++) {
            if (deEsteProyecto.get(i).getEstado().equalsIgnoreCase(estado)) {
                contador++;
            }
        }
        return contador;
    }

    public static void guardar(SolicitudUnion solicitud) {
        if (AlmacenMemoria.usandoMySQL) {
            String sql = "INSERT INTO solicitud_union (id_usuario, id_proyecto, procedencia, grado, perfil_experiencia, estado) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = ConexionBD.obtenerConexion();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, solicitud.getUsuario().getIdUsuario());
                ps.setInt(2, solicitud.getProyecto().getIdProyecto());
                ps.setString(3, solicitud.getProcedencia());
                ps.setString(4, solicitud.getGrado());
                ps.setString(5, solicitud.getPerfilExperiencia());
                ps.setString(6, solicitud.getEstado());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        solicitud.setIdSolicitud(rs.getInt(1));
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error JDBC guardar (solicitud): " + e.getMessage());
            }
            return;
        }

        AlmacenMemoria.solicitudes.add(solicitud);
    }

    // CORRECCIÓN: antes este cambio no se guardaba en MySQL, solo en
    // el objeto de esa única petición. Ahora sí actualiza la fila real.
    public static void actualizarEstado(int idSolicitud, String nuevoEstado) {
        if (AlmacenMemoria.usandoMySQL) {
            String sql = "UPDATE solicitud_union SET estado = ? WHERE id_solicitud = ?";
            try (Connection conn = ConexionBD.obtenerConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nuevoEstado);
                ps.setInt(2, idSolicitud);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error JDBC actualizarEstado (solicitud): " + e.getMessage());
            }
            return;
        }

        SolicitudUnion s = buscarPorId(idSolicitud);
        if (s != null) {
            s.setEstado(nuevoEstado);
        }
    }

    private static SolicitudUnion mapearSolicitud(ResultSet rs) throws SQLException {
        Usuario u = UsuarioRepositorio.buscarPorId(rs.getInt("id_usuario"));
        Proyecto p = ProyectoRepositorio.buscarPorId(rs.getInt("id_proyecto"));
        SolicitudUnion s = new SolicitudUnion(u, p, rs.getString("procedencia"), rs.getString("grado"), rs.getString("perfil_experiencia"));
        s.setEstado(rs.getString("estado"));
        s.setIdSolicitud(rs.getInt("id_solicitud"));
        return s;
    }
}
