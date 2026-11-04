package com.workinon.modelo.evaluacion;

/**
 * SUBCLASE CONCRETA — EvaluacionFormulario
 * Demuestra:
 *  - HERENCIA: `extends Evaluacion`
 *  - SOBREESCRITURA: Redefine la lógica para cuestionarios de opción múltiple.
 */
public class EvaluacionFormulario extends Evaluacion {
    private int numeroPreguntas;

    public EvaluacionFormulario(String nombreProyecto, String instrucciones, int duracionMinutos, int numeroPreguntas) {
        super(nombreProyecto, instrucciones, duracionMinutos);
        this.numeroPreguntas = numeroPreguntas;
    }

    @Override
    public String generarInstruccionesEspecificas() {
        return "Responde el cuestionario de " + numeroPreguntas + " preguntas de opción múltiple.";
    }

    @Override
    public int duracionSugeridaMinutos() {
        return numeroPreguntas * 2;
    }

    @Override
    public String getTipoEvaluacion() {
        return "Formulario Teórico (" + numeroPreguntas + " preguntas)";
    }

    public int getNumeroPreguntas() { return numeroPreguntas; }
}
