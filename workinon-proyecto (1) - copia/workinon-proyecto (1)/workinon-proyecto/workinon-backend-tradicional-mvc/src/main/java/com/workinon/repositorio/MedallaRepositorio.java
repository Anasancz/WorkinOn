package com.workinon.repositorio;

import com.workinon.modelo.Medalla;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * REPOSITORIO — MedallaRepositorio
 * Catálogo de medallas + qué medallas ganó cada usuario.
 *
 * NOTA: el método otorgar() no existía en la versión anterior del
 * backend tradicional (no había forma de guardar una medalla ganada
 * en la base de datos). Se agregó aquí para que el flujo completo de
 * "termina el examen -> gana la medalla -> se guarda" funcione de
 * verdad, igual que ya funciona en la versión con Spring Boot.
 */
public class MedallaRepositorio {

    public static List<Medalla> listarTodas() {
        if (AlmacenMemoria.usandoMySQL) {
            List<Medalla> resultado = new ArrayList<>();
            String sql = "SELECT * FROM medalla";
            try (Connection conn = ConexionBD.obtenerConexion();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(new Medalla(rs.getInt("id_medalla"), rs.getString("nombre")));
                }
                return resultado;
            } catch (SQLException e) {
                System.err.println("Error JDBC listarTodas (medalla): " + e.getMessage());
            }
        }
        return AlmacenMemoria.medallas;
    }

    public static List<Medalla> buscarPorUsuario(int idUsuario) {
        if (AlmacenMemoria.usandoMySQL) {
            List<Medalla> resultado = new ArrayList<>();
            String sql = "SELECT m.* FROM medalla m JOIN usuario_medalla um ON m.id_medalla = um.id_medalla WHERE um.id_usuario = ?";
            try (Connection conn = ConexionBD.obtenerConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        resultado.add(new Medalla(rs.getInt("id_medalla"), rs.getString("nombre")));
                    }
                }
                return resultado;
            } catch (SQLException e) {
                System.err.println("Error JDBC buscarPorUsuario (medalla): " + e.getMessage());
            }
        }

        List<Medalla> ganadas = new ArrayList<>();
        for (int i = 0; i < AlmacenMemoria.usuarioMedalla.size(); i++) {
            int[] par = AlmacenMemoria.usuarioMedalla.get(i);
            if (par[0] == idUsuario) {
                for (int j = 0; j < AlmacenMemoria.medallas.size(); j++) {
                    Medalla m = AlmacenMemoria.medallas.get(j);
                    if (m.getIdMedalla() == par[1]) {
                        ganadas.add(m);
                    }
                }
            }
        }
        return ganadas;
    }

    public static boolean usuarioYaTieneMedalla(int idUsuario, int idMedalla) {
        List<Medalla> yaGanadas = buscarPorUsuario(idUsuario);
        for (int i = 0; i < yaGanadas.size(); i++) {
            if (yaGanadas.get(i).getIdMedalla() == idMedalla) {
                return true;
            }
        }
        return false;
    }

    public static void otorgar(int idUsuario, int idMedalla) {
        if (AlmacenMemoria.usandoMySQL) {
            String sql = "INSERT INTO usuario_medalla (id_usuario, id_medalla) VALUES (?, ?)";
            try (Connection conn = ConexionBD.obtenerConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                ps.setInt(2, idMedalla);
                ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error JDBC otorgar (medalla): " + e.getMessage());
            }
            return;
        }

        AlmacenMemoria.usuarioMedalla.add(new int[]{idUsuario, idMedalla});
    }
}
