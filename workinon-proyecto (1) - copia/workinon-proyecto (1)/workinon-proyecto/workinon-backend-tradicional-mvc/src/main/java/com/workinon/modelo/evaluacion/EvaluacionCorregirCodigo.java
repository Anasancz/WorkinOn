package com.workinon.modelo.evaluacion;

public class EvaluacionCorregirCodigo extends Evaluacion implements Revisable {
    private int erroresOcultos;
    private String observaciones;

    public EvaluacionCorregirCodigo(String nombreProyecto, String instrucciones, int duracionMinutos, int erroresOcultos) {
        super(nombreProyecto, instrucciones, duracionMinutos);
        this.erroresOcultos = erroresOcultos;
        this.observaciones = "";
    }

    @Override
    public String generarInstruccionesEspecificas() {
        return "Encuentra y corrige los " + erroresOcultos + " errores dentro del código proporcionado.";
    }

    @Override
    public int duracionSugeridaMinutos() {
        return getDuracionMinutos();
    }

    @Override
    public String getTipoEvaluacion() {
        return "Depuración de Código (" + erroresOcultos + " bugs)";
    }

    @Override
    public boolean necesitaRevisionManual() {
        return true;
    }

    @Override
    public void registrarObservaciones(String notas) {
        this.observaciones = notas;
    }

    public int getErroresOcultos() { return erroresOcultos; }
    public String getObservaciones() { return observaciones; }
}
