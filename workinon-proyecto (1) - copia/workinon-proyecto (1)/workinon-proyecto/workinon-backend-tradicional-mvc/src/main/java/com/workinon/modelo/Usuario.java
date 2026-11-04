package com.workinon.modelo;

/**
 * CLASE CONCRETA — Usuario
 * Demuestra:
 *  - ENCAPSULAMIENTO: Atributos privados con acceso controlado mediante getters/setters.
 *  - SOBRECARGA DE CONSTRUCTORES: Múltiples constructores para instanciar el objeto de distintas formas.
 */
public class Usuario {
    private static int contadorId = 1;

    // Se usa una sola vez, al cargar los usuarios de ejemplo con ID
    // fijo, para que el contador no vuelva a repetir esos mismos IDs
    // cuando alguien se registre después.
    public static void establecerContadorId(int valor) {
        contadorId = valor;
    }

    private int idUsuario;
    private String nombre;
    private String correo;
    private String contrasena;
    private String descripcion;
    private String fechaRegistro;

    // Constructor completo
    public Usuario(int idUsuario, String nombre, String correo, String contrasena, String descripcion) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.descripcion = descripcion;
        this.fechaRegistro = java.time.LocalDate.now().toString();
    }

    // SOBRECARGA 1: Constructor que autogenera el ID
    public Usuario(String nombre, String correo, String contrasena, String descripcion) {
        this(contadorId++, nombre, correo, contrasena, descripcion);
    }

    // SOBRECARGA 2: Constructor simplificado sin descripción inicial
    public Usuario(String nombre, String correo, String contrasena) {
        this(contadorId++, nombre, correo, contrasena, "Nuevo miembro de WorkinON");
    }

    // Getters y Setters con validaciones (Encapsulamiento)
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre != null && !nombre.trim().isEmpty()) {
            this.nombre = nombre;
        }
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        if (correo != null && correo.contains("@")) {
            this.correo = correo;
        }
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    @Override
    public String toString() {
        return "Usuario #" + idUsuario + ": " + nombre + " (" + correo + ")";
    }
}
