package com.workinon.modelo.evaluacion;

/**
 * SUBCLASE CONCRETA — EvaluacionCodigo
 * Demuestra:
 *  - HERENCIA: `extends Evaluacion`
 *  - IMPLEMENTACIÓN DE INTERFAZ: `implements Revisable`
 *  - SOBREESCRITURA (`@Override`): Implementa los métodos abstractos de la clase padre y de la interfaz.
 */
public class EvaluacionCodigo extends Evaluacion implements Revisable {
    private String lenguaje;
    private String observaciones;

    public EvaluacionCodigo(String nombreProyecto, String instrucciones, int duracionMinutos, String lenguaje) {
        super(nombreProyecto, instrucciones, duracionMinutos);
        this.lenguaje = lenguaje;
        this.observaciones = "";
    }

    @Override
    public String generarInstruccionesEspecificas() {
        return "Resuelve el problema práctico escribiendo código limpio en " + lenguaje + ".";
    }

    @Override
    public int duracionSugeridaMinutos() {
        return getDuracionMinutos() + 15; // Requiere más tiempo por codificación
    }

    @Override
    public String getTipoEvaluacion() {
        return "Evaluación de Código (" + lenguaje + ")";
    }

    // Métodos de la interfaz Revisable
    @Override
    public boolean necesitaRevisionManual() {
        return true;
    }

    @Override
    public void registrarObservaciones(String notas) {
        this.observaciones = notas;
    }

    public String getLenguaje() { return lenguaje; }
    public String getObservaciones() { return observaciones; }
}
