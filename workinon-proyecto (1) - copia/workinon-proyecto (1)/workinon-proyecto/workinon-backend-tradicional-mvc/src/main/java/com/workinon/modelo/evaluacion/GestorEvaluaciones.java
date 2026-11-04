package com.workinon.modelo.evaluacion;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * ============================================================
 *  CLASE PRINCIPAL 2 — GestorEvaluaciones
 * ============================================================
 * Administra en memoria todas las evaluaciones creadas, usando un
 * ArrayList — mismo papel que la clase "Ventas" en el ejemplo de la
 * agencia de carros: no hereda de nada, y se relaciona con las
 * subclases de Evaluacion por COMPOSICIÓN (tiene un ArrayList de
 * ellas adentro), igual que Ventas tenía un atributo "Vehiculo"
 * adentro sin heredar de él.
 *
 * A diferencia de la versión con Spring Boot, aquí NO se usa
 * @Component: en Java puro, la única instancia compartida se logra
 * simplemente creando el objeto una sola vez en el controlador y
 * reutilizándolo (ver EvaluacionControlador.java).
 */
public class GestorEvaluaciones {

    // ---------------------------------------------------------
    // 6 ATRIBUTOS de esta clase principal
    // ---------------------------------------------------------
    private ArrayList<Evaluacion> listaEvaluaciones = new ArrayList<>();
    private String nombreGestor;
    private int totalEvaluacionesCreadas;
    private double promedioCalificaciones;
    private boolean aceptandoNuevasEvaluaciones;
    private String fechaUltimaActualizacion;

    public GestorEvaluaciones() {
        this.nombreGestor = "Gestor de Evaluaciones WorkinON";
        this.totalEvaluacionesCreadas = 0;
        this.promedioCalificaciones = 0;
        this.aceptandoNuevasEvaluaciones = true;
        this.fechaUltimaActualizacion = LocalDate.now().toString();
    }

    // ============================================================
    // 6 MÉTODOS DE LÓGICA
    // ============================================================

    public Evaluacion agregar(Evaluacion evaluacion) {
        listaEvaluaciones.add(evaluacion);
        totalEvaluacionesCreadas++;
        fechaUltimaActualizacion = LocalDate.now().toString();
        return evaluacion;
    }

    public ArrayList<Evaluacion> listarTodas() {
        return listaEvaluaciones;
    }

    public ArrayList<Evaluacion> buscarPorProyecto(String nombreProyecto) {
        ArrayList<Evaluacion> resultado = new ArrayList<>();
        for (int i = 0; i < listaEvaluaciones.size(); i++) {
            Evaluacion e = listaEvaluaciones.get(i);
            if (e.getNombreProyecto().equalsIgnoreCase(nombreProyecto)) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    public boolean eliminar(int idEvaluacion) {
        for (int i = 0; i < listaEvaluaciones.size(); i++) {
            Evaluacion e = listaEvaluaciones.get(i);
            if (e.getIdEvaluacion() == idEvaluacion) {
                listaEvaluaciones.remove(i);
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> listarResumenes() {
        ArrayList<String> resumenes = new ArrayList<>();
        for (int i = 0; i < listaEvaluaciones.size(); i++) {
            Evaluacion e = listaEvaluaciones.get(i);
            resumenes.add(e.mostrarResumen());
        }
        return resumenes;
    }

    /**
     * Reporte agrupado por tipo. Aquí se ve el POLIMORFISMO en acción:
     * recorremos una lista de "Evaluacion" (el tipo base/abstracto) y
     * llamamos a getTipoEvaluacion() / generarInstruccionesEspecificas()
     * en cada una — Java ejecuta automáticamente la versión de CADA
     * subclase real, sin necesidad de preguntar "¿de qué tipo eres?".
     */
    public String generarReportePorTipo() {
        if (listaEvaluaciones.isEmpty()) {
            return "Aún no se han creado evaluaciones.";
        }

        String reporte = "====== REPORTE DE EVALUACIONES ======\n";

        int totalFormulario = 0;
        int totalCodigo = 0;
        int totalCorregir = 0;
        int totalTrabajo = 0;
        double sumaCalificaciones = 0;

        for (int i = 0; i < listaEvaluaciones.size(); i++) {
            Evaluacion e = listaEvaluaciones.get(i);

            reporte = reporte + e.mostrarResumen()
                    + " | Instrucciones: " + e.generarInstruccionesEspecificas()
                    + " | Duración sugerida: " + e.duracionSugeridaMinutos() + " min\n";

            sumaCalificaciones = sumaCalificaciones + e.getCalificacionObtenida();

            String tipo = e.getTipoEvaluacion();
            if (tipo.equals("Formulario")) {
                totalFormulario++;
            } else if (tipo.equals("Código")) {
                totalCodigo++;
            } else if (tipo.equals("Corrige un código")) {
                totalCorregir++;
            } else if (tipo.equals("Trabajo propio")) {
                totalTrabajo++;
            }
        }

        promedioCalificaciones = sumaCalificaciones / listaEvaluaciones.size();

        reporte = reporte + "--------------------------------------\n";
        reporte = reporte + "Total Formulario: " + totalFormulario + "\n";
        reporte = reporte + "Total Código: " + totalCodigo + "\n";
        reporte = reporte + "Total Corrige un código: " + totalCorregir + "\n";
        reporte = reporte + "Total Trabajo propio: " + totalTrabajo + "\n";
        reporte = reporte + "Total general: " + listaEvaluaciones.size() + "\n";
        reporte = reporte + "Promedio de calificaciones: " + promedioCalificaciones;

        return reporte;
    }

    // ---------------------------------------------------------
    // Getters y setters de los 6 atributos
    // ---------------------------------------------------------
    public String getNombreGestor() { return nombreGestor; }
    public void setNombreGestor(String nombreGestor) { this.nombreGestor = nombreGestor; }

    public int getTotalEvaluacionesCreadas() { return totalEvaluacionesCreadas; }

    public double getPromedioCalificaciones() { return promedioCalificaciones; }

    public boolean getAceptandoNuevasEvaluaciones() { return aceptandoNuevasEvaluaciones; }
    public void setAceptandoNuevasEvaluaciones(boolean v) { this.aceptandoNuevasEvaluaciones = v; }

    public String getFechaUltimaActualizacion() { return fechaUltimaActualizacion; }
}
