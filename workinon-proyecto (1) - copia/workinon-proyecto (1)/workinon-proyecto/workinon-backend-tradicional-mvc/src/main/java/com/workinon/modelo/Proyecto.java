package com.workinon.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * CLASE CONCRETA — Proyecto
 * Demuestra:
 *  - ASOCIACIÓN Y COMPOSICIÓN: Contiene referencias a Usuario, Categoria y una lista de Perfil (habilidades).
 *  - ENCAPSULAMIENTO: Métodos de acceso y manipulación controlada de listas con ArrayList.
 */
public class Proyecto {
    private static int contadorId = 1;

    public static void establecerContadorId(int valor) {
        contadorId = valor;
    }

    private int idProyecto;
    private String nombre;
    private String descripcion;
    private int vacantesDisponibles;
    private String fechaCreacion;
    
    private Usuario usuario; // Líder del proyecto
    private Categoria categoria;
    private List<Perfil> habilidades;
    private int miembrosAceptados;

    public Proyecto(int idProyecto, String nombre, String descripcion, int vacantesDisponibles, Usuario usuario, Categoria categoria) {
        this.idProyecto = idProyecto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.vacantesDisponibles = vacantesDisponibles;
        this.usuario = usuario;
        this.categoria = categoria;
        this.fechaCreacion = LocalDate.now().toString();
        this.habilidades = new ArrayList<>();
        this.miembrosAceptados = 0;
    }

    public Proyecto(String nombre, String descripcion, int vacantesDisponibles, Usuario usuario, Categoria categoria) {
        this(contadorId++, nombre, descripcion, vacantesDisponibles, usuario, categoria);
    }

    public void agregarHabilidad(Perfil perfil) {
        if (perfil != null) {
            this.habilidades.add(perfil);
        }
    }

    // Getters y Setters
    public int getIdProyecto() { return idProyecto; }
    public void setIdProyecto(int idProyecto) { this.idProyecto = idProyecto; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getVacantesDisponibles() { return vacantesDisponibles; }
    public void setVacantesDisponibles(int vacantesDisponibles) { this.vacantesDisponibles = vacantesDisponibles; }
    public String getFechaCreacion() { return fechaCreacion; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public List<Perfil> getHabilidades() { return habilidades; }
    public void setHabilidades(List<Perfil> habilidades) { this.habilidades = habilidades; }
    public int getMiembrosAceptados() { return miembrosAceptados; }
    public void setMiembrosAceptados(int miembrosAceptados) { this.miembrosAceptados = miembrosAceptados; }
}
