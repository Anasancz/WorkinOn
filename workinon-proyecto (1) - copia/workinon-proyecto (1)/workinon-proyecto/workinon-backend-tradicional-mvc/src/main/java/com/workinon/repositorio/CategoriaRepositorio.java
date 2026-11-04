package com.workinon.repositorio;

import com.workinon.modelo.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * REPOSITORIO — CategoriaRepositorio
 * Catálogo fijo (Tecnología, Redes, etc.), solo se lee, nunca se crea
 * desde la app — igual que en la versión con Spring Boot.
 */
public class CategoriaRepositorio {

    public static List<Categoria> listarTodas() {
        if (AlmacenMemoria.usandoMySQL) {
            List<Categoria> resultado = new ArrayList<>();
            String sql = "SELECT * FROM categoria";
            try (Connection conn = ConexionBD.obtenerConexion();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(new Categoria(rs.getInt("id_categoria"), rs.getString("nombre")));
                }
                return resultado;
            } catch (SQLException e) {
                System.err.println("Error JDBC listarTodas (categoria): " + e.getMessage());
            }
        }
        return AlmacenMemoria.categorias;
    }

    public static Categoria buscarPorId(int id) {
        List<Categoria> todas = listarTodas();
        for (int i = 0; i < todas.size(); i++) {
            if (todas.get(i).getIdCategoria() == id) {
                return todas.get(i);
            }
        }
        return null;
    }
}
