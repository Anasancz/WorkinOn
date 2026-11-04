package com.workinon.modelo;

public class Reporte {
    private static int contadorId = 1;

    private int idReporte;
    private String tipo; // "proyecto" o "usuario"
    private String elementoReportado;
    private String motivo;
    private String fechaReporte;

    public Reporte(String tipo, String elementoReportado, String motivo) {
        this.idReporte = contadorId++;
        this.tipo = tipo;
        this.elementoReportado = elementoReportado;
        this.motivo = motivo;
        this.fechaReporte = java.time.LocalDate.now().toString();
    }

    public int getIdReporte() { return idReporte; }
    public String getTipo() { return tipo; }
    public String getElementoReportado() { return elementoReportado; }
    public String getMotivo() { return motivo; }
    public String getFechaReporte() { return fechaReporte; }
}
