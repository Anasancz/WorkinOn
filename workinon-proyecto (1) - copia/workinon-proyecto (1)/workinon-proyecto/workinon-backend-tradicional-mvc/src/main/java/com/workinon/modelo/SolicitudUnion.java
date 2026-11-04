package com.workinon.modelo;

public class SolicitudUnion {
    private static int contadorId = 1;

    private int idSolicitud;
    private Usuario usuario;
    private Proyecto proyecto;
    private String procedencia;
    private String grado;
    private String perfilExperiencia;
    private String estado; // "pendiente", "aceptada", "rechazada"

    public SolicitudUnion(Usuario usuario, Proyecto proyecto, String procedencia, String grado, String perfilExperiencia) {
        this.idSolicitud = contadorId++;
        this.usuario = usuario;
        this.proyecto = proyecto;
        this.procedencia = procedencia;
        this.grado = grado;
        this.perfilExperiencia = perfilExperiencia;
        this.estado = "pendiente";
    }

    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }
    public Usuario getUsuario() { return usuario; }
    public Proyecto getProyecto() { return proyecto; }
    public String getProcedencia() { return procedencia; }
    public String getGrado() { return grado; }
    public String getPerfilExperiencia() { return perfilExperiencia; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
