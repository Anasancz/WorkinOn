package com.workinon.modelo.evaluacion;

import java.time.LocalDate;

/**
 * CLASE ABSTRACTA — Evaluacion
 * Demuestra:
 *  - ABSTRACCIÓN: Define la estructura común de cualquier evaluación sin implementar detalles específicos.
 *  - HERENCIA: Las subclases extenderán esta clase reutilizando atributos y métodos comunes.
 *  - ENCAPSULAMIENTO: Atributos privados/protegidos con métodos de acceso.
 */
public abstract class Evaluacion {
    private static int contadorId = 1;

    private final int idEvaluacion;
    private String nombreProyecto;
    private String instrucciones;
    private int duracionMinutos;
    private String nombreCandidato;
    private String fechaCreacion;

    protected int puntajeMaximo;
    protected double calificacionObtenida;
    protected String estado;

    public Evaluacion(String nombreProyecto, String instrucciones, int duracionMinutos) {
        this.idEvaluacion = contadorId++;
        this.nombreProyecto = nombreProyecto;
        this.instrucciones = instrucciones;
        this.duracionMinutos = duracionMinutos;
        this.nombreCandidato = "Sin asignar";
        this.fechaCreacion = LocalDate.now().toString();
        this.puntajeMaximo = 100;
        this.calificacionObtenida = 0.0;
        this.estado = "pendiente";
    }

    // ============================================================
    // MÉTODOS ABSTRACTOS (Obligatorios para las subclases)
    // ============================================================
    public abstract String generarInstruccionesEspecificas();
    public abstract int duracionSugeridaMinutos();
    public abstract String getTipoEvaluacion();

    // Método concreto heredado
    public String mostrarResumen() {
        return "[#" + idEvaluacion + "] " + getTipoEvaluacion() + " - Proyecto: " + nombreProyecto + " | Estado: " + estado;
    }

    // Getters y Setters
    public int getIdEvaluacion() { return idEvaluacion; }
    public String getNombreProyecto() { return nombreProyecto; }
    public void setNombreProyecto(String nombreProyecto) { this.nombreProyecto = nombreProyecto; }
    public String getInstrucciones() { return instrucciones; }
    public void setInstrucciones(String instrucciones) { this.instrucciones = instrucciones; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    public String getNombreCandidato() { return nombreCandidato; }
    public void setNombreCandidato(String nombreCandidato) { this.nombreCandidato = nombreCandidato; }
    public String getFechaCreacion() { return fechaCreacion; }
    public int getPuntajeMaximo() { return puntajeMaximo; }
    public double getCalificacionObtenida() { return calificacionObtenida; }
    public void setCalificacionObtenida(double calificacionObtenida) { this.calificacionObtenida = calificacionObtenida; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
