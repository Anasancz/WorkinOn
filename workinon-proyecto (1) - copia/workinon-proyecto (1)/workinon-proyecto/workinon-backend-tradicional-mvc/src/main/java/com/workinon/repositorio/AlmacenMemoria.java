package com.workinon.repositorio;

import com.workinon.modelo.*;
import com.workinon.modelo.evaluacion.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *  ALMACÉN EN MEMORIA (respaldo cuando MySQL no está corriendo)
 * ============================================================
 * Todos los Repositorios comparten esta misma clase para dos cosas:
 *  1) Saber, UNA sola vez, si hay MySQL disponible (usandoMySQL).
 *  2) Si NO hay MySQL, leer/escribir sobre estas listas en memoria en
 *     vez de tronar la aplicación — así puedes seguir haciendo tu
 *     demo en clase aunque se te olvide prender MySQL.
 *
 * Esto es lo mismo que hacía GestorWorkinON antes, solo que ahora
 * cada Repositorio (Usuario, Proyecto, Solicitud...) tiene su propia
 * clase separada en vez de tener todo junto en un solo archivo gigante.
 */
public class AlmacenMemoria {

    public static final boolean usandoMySQL = ConexionBD.probarConexion();

    public static List<Usuario> usuarios = new ArrayList<>();
    public static List<Proyecto> proyectos = new ArrayList<>();
    public static List<Categoria> categorias = new ArrayList<>();
    public static List<Perfil> perfiles = new ArrayList<>();
    public static List<Medalla> medallas = new ArrayList<>();
    public static List<SolicitudUnion> solicitudes = new ArrayList<>();
    public static List<Reporte> reportes = new ArrayList<>();
    public static List<int[]> usuarioMedalla = new ArrayList<>(); // [idUsuario, idMedalla]

    private static boolean inicializado = false;

    // Se llama una sola vez, al arrancar el servidor (ver Main).
    public static void inicializar() {
        if (inicializado) {
            return;
        }
        inicializado = true;

        if (usandoMySQL) {
            System.out.println("CONEXION EXITOSA A MYSQL (workinon_db). Usando base de datos real...");
            return;
        }

        System.out.println("MYSQL NO DETECTADO. Modo activado: almacenamiento en memoria (ArrayList)...");

        Categoria c1 = new Categoria(1, "Tecnología");
        Categoria c2 = new Categoria(2, "Redes");
        Categoria c3 = new Categoria(3, "Ciencia de datos");
        Categoria c4 = new Categoria(4, "Salud");
        categorias.add(c1); categorias.add(c2); categorias.add(c3); categorias.add(c4);

        Perfil p1 = new Perfil(1, "Java");
        Perfil p2 = new Perfil(2, "C++");
        Perfil p3 = new Perfil(3, "HTML");
        Perfil p4 = new Perfil(4, "UX");
        Perfil p5 = new Perfil(5, "JS");
        Perfil p6 = new Perfil(6, "Redes");
        Perfil p7 = new Perfil(7, "BD");
        Perfil p8 = new Perfil(8, "Cisco");
        perfiles.add(p1); perfiles.add(p2); perfiles.add(p3); perfiles.add(p4);
        perfiles.add(p5); perfiles.add(p6); perfiles.add(p7); perfiles.add(p8);

        medallas.add(new Medalla(1, "Programador"));
        medallas.add(new Medalla(2, "Redes"));
        medallas.add(new Medalla(3, "Base de datos"));
        medallas.add(new Medalla(4, "Backend"));
        medallas.add(new Medalla(5, "Frontend"));
        medallas.add(new Medalla(6, "DevOps"));

        Usuario u1 = new Usuario(1, "Carlos Pérez", "carlos@gmail.com", "123456", "Desarrollador Java & Web");
        Usuario u2 = new Usuario(2, "Ana Gómez", "ana@gmail.com", "123456", "Diseñadora UX/UI");
        Usuario u3 = new Usuario(3, "Mariana Silva", "mariana@gmail.com", "123456", "Especialista en Redes");
        usuarios.add(u1); usuarios.add(u2); usuarios.add(u3);

        usuarioMedalla.add(new int[]{1, 1});
        usuarioMedalla.add(new int[]{1, 4});

        Proyecto pr1 = new Proyecto(1, "Coffeelovers", "Una plataforma para amantes del café...", 5, u1, c1);
        pr1.agregarHabilidad(p2); pr1.agregarHabilidad(p3);

        Proyecto pr2 = new Proyecto(2, "Calico", "Rediseño de infraestructura de red...", 4, u3, c2);
        pr2.agregarHabilidad(p6); pr2.agregarHabilidad(p8);

        Proyecto pr3 = new Proyecto(3, "Blimond", "Sistema de gestión y calendarización...", 7, u2, c3);
        pr3.agregarHabilidad(p1); pr3.agregarHabilidad(p7);

        Proyecto pr4 = new Proyecto(4, "App WorkinON", "La propia plataforma colaborativa WorkinON...", 5, u1, c1);
        pr4.agregarHabilidad(p4); pr4.agregarHabilidad(p5);

        proyectos.add(pr1); proyectos.add(pr2); proyectos.add(pr3); proyectos.add(pr4);

        // CORRECCIÓN: sin esto, el próximo usuario/proyecto que se
        // cree en modo memoria repetiría el ID 1, chocando con los
        // datos semilla de arriba.
        Usuario.establecerContadorId(4);
        Proyecto.establecerContadorId(5);
    }
}
