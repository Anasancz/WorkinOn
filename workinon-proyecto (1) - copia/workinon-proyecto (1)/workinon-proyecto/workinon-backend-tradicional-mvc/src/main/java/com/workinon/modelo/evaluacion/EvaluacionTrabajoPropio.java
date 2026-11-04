package com.workinon.modelo.evaluacion;

public class EvaluacionTrabajoPropio extends Evaluacion implements Notificable {
    private String formatoEntrega;

    public EvaluacionTrabajoPropio(String nombreProyecto, String instrucciones, int duracionMinutos, String formatoEntrega) {
        super(nombreProyecto, instrucciones, duracionMinutos);
        this.formatoEntrega = formatoEntrega;
    }

    @Override
    public String generarInstruccionesEspecificas() {
        return "Sube un archivo o portafolio en formato (" + formatoEntrega + ") que demuestre tu trabajo previo.";
    }

    @Override
    public int duracionSugeridaMinutos() {
        return 60; // Duración estándar para revisión de portafolio
    }

    @Override
    public String getTipoEvaluacion() {
        return "Entrega de Trabajo Propio (" + formatoEntrega + ")";
    }

    @Override
    public String generarMensajeNotificacion() {
        return "Tu proyecto entregado en formato " + formatoEntrega + " ha sido recibido para evaluación.";
    }

    @Override
    public boolean enviarNotificacion(String destinatario) {
        System.out.println("Notificación enviada a " + destinatario + ": " + generarMensajeNotificacion());
        return true;
    }

    public String getFormatoEntrega() { return formatoEntrega; }
}
